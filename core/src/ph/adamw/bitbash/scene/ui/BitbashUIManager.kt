package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorSimple
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegionFlag
import ph.adamw.bitbash.scene.BitbashPlayScene
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashSet

object BitbashUIManager {
    private val mapViewerCache = Hashtable<Vector2, Actor>()
    private val mapMarker = ActorSimple("map_marker")

    private val mapUiTable = VisTable()

    private val clipBounds = Rectangle()
    private val scissors = Rectangle()

    private val consoleDisplay = VisTable()
    private val consoleInput = TextField("", UIUtils.SKIN)

    private var mapViewerCell : Cell<*>? = null

    private var mapViewerThreadPool : ExecutorService = Executors.newCachedThreadPool()

    private const val MAP_TABLE_PADDING : Float = 50f

    val fpsLabel = Label("60 FPS", UIUtils.SKIN)
    val positionLabel = Label("Position: (0, 0)", UIUtils.SKIN)
    val velocityLabel = Label("Velocity: (0, 0)", UIUtils.SKIN)

    fun dispose() {
        mapViewerThreadPool.shutdown()
    }

    fun loadMapViewer(layer: Group, stage: Stage, map: Map, regions: HashSet<Vector2>) {
        mapMarker.setTexture("marker")
        stage.addActor(mapMarker)

        mapUiTable.setFillParent(true)

        mapUiTable.pad(MAP_TABLE_PADDING)
        mapUiTable.add(Label("Map Viewer", UIUtils.SKIN, "title")).center()
        mapUiTable.row()

        updateMapViewer(map, regions, stage)
        for(i in regions) {
            map.unloadRegion(i)
        }

        val a = Container<ActorSimple>()
        a.background = TextureRegionDrawable(ActorGameObject.getTexture("map-background"))

        mapViewerCell = mapUiTable.add(a).fill().grow().expand()
        mapUiTable.debugAll()
        layer.addActor(mapUiTable)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>, stage: Stage) {
        mapViewerThreadPool.submit {
            for(i in regions) {
                val rg = map.getOrLoadRegion(i)

                if (rg != null && rg.isDirty(MapRegionFlag.NEEDS_MINIMAP)) {
                    rg.markUndirty(MapRegionFlag.NEEDS_MINIMAP)
                    val pixmap = UIUtils.generateMapRegionOverview(rg)
                    Gdx.app.log("MINIMAP", "Adding $rg to minimap.")
                    val r = ActorSimple("map_ui_element_$rg")

                    Gdx.app.postRunnable {
                        r.texture = TextureRegion(Texture(pixmap))
                        r.setPosition(rg.coords.x * r.width, rg.coords.y * r.height)
                        stage.root.removeActor(mapViewerCache[i])
                        stage.addActor(r)
                        mapViewerCache[i] = r
                    }
                }
            }
        }

        mapMarker.toFront()
    }

    fun updateMapViewerMarker(stage: Stage) : Rectangle? {
        val p = BitbashPlayScene.mapState!!.player.readOnlyTilePosition
        mapMarker.setPosition(p.x, p.y, Align.center)
        stage.camera.position.set(mapMarker.x + mapMarker.width / 2f, mapMarker.y + mapMarker.height / 2f, 0f)
        stage.camera.update()

        if(mapViewerCell != null) {
            val wo = (stage.viewport.worldWidth / Gdx.graphics.width)
            val ho = (stage.viewport.worldHeight / Gdx.graphics.height)

            val w1 = mapViewerCell!!.actorWidth * wo
            val h1 = mapViewerCell!!.actorHeight * ho

            //818.0 0.5 480.0 960
            //818.0 0.4790419 480.0 1002

            // Account for the title row
            val r = mapUiTable.getRowHeight(0) * ho

            clipBounds.set(
                    stage.camera.position.x - (w1 / 2f),
                    stage.camera.position.y - (h1 / 2f) - r / 2f,
                    w1,
                    h1
            )

            ScissorStack.calculateScissors(
                    stage.camera,
                    stage.batch.transformMatrix,
                    clipBounds,
                    scissors
            )

            return scissors
        }

        return null
    }

    fun loadDevUIInto(layer: Group) {
        val topLeftContainer = Container(VisTable())
        val topLeftTable = topLeftContainer.actor
        topLeftContainer.align(Align.topLeft)
        topLeftContainer.setFillParent(true)
        topLeftTable.background = TextureRegionDrawable(ActorGameObject.getTexture("map-background"))

        topLeftContainer.pad(10f)
        topLeftTable.pad(5f)

        topLeftTable.add(fpsLabel).left()
        topLeftTable.row()
        topLeftTable.add(positionLabel).left()
        topLeftTable.row()
        topLeftTable.add(velocityLabel).left()

        layer.addActor(topLeftContainer)
    }

    fun addToConsole(tag: String, content: String) {
        val t = UIUtils.DATE_FORMATTER.format(Date())
        consoleDisplay.row()
        consoleDisplay.add("<$t> [${tag.toUpperCase()}] $content").left()
    }

    fun loadMainUIInto(mainUi: Group) {
        val consoleContainer = Container(VisTable())
        val consoleTable = consoleContainer.actor

        consoleContainer.pad(10f)
        consoleTable.pad(5f)

        consoleTable.background = TextureRegionDrawable(ActorGameObject.getTexture("map-background"))


        for(i in 0..10) {
            addToConsole("DEBUG", "test $i")
        }

        val scrollPane = ScrollPane(consoleDisplay)

        consoleContainer.debugAll()

        consoleContainer.align(Align.bottomLeft)
        consoleContainer.setFillParent(true)

        consoleTable.add(scrollPane).left()
        consoleTable.row()
        consoleTable.add(consoleInput).fill()

        consoleInput.addListener(object : InputListener() {
            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                if(consoleInput.hasKeyboardFocus()) {
                    if(keycode == Input.Keys.ESCAPE) {
                        closeConsole()
                    } else if(keycode == Input.Keys.ENTER) {
                        //TODO command parser
                        addToConsole("INPUT", consoleInput.text)
                        closeConsole()
                    }

                    return true
                }

                return super.keyUp(event, keycode)
            }
        })

        mainUi.addActor(consoleContainer)
    }

    fun closeConsole() {
        consoleInput.text = ""
        GameManager.UI_STAGE.unfocus(consoleInput)
    }

    fun openConsole() {
        GameManager.UI_STAGE.keyboardFocus = consoleInput
    }
}