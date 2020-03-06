package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorSimple
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.layer.Layer
import java.util.*
import kotlin.collections.HashSet

object BitbashUIManager {
    private val mapViewerCache = Hashtable<Vector2, Actor>()
    private val mapMarker = ActorSimple("map_marker")

    private val devUiTable = VisTable()
    private val mapUiTable = VisTable()

    private val clipBounds = Rectangle()
    private val scissors = Rectangle()

    private var mapViewerCell : Cell<Actor>? = null

    private const val MAP_TABLE_PADDING : Float = 50f

    fun loadMapViewer(layer: Layer, stage: Stage, map: Map, regions: HashSet<Vector2>) {
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

        mapViewerCell = mapUiTable.add().fill().grow()
        mapUiTable.debugAll()
        layer.addActor(mapUiTable)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>, stage: Stage) {
        for(i in regions) {
            val rg = map.getOrLoadRegion(i)
            if(rg!!.isDirty) {
                stage.root.removeActor(mapViewerCache[i])
                val r = UIUtils.generateMapRegionOverview(rg)
                r.setPosition(i.x * r.width, i.y * r.height)
                stage.addActor(r)
                mapMarker.toFront()
                mapViewerCache[i] = r
            }
        }
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

    fun loadDevUIInto(layer: Layer) {
        devUiTable.setFillParent(true)

        devUiTable.pad(10f)

        //TODO update this data
        devUiTable.add("Dev Tools:").left()
        devUiTable.add().fillX().growX()
        devUiTable.row()
        devUiTable.add(" Position: (0, 0)").left()
        devUiTable.row()
        devUiTable.add(" Velocity: (0, 0)").left()
        devUiTable.row()
        devUiTable.add().grow().fill().colspan(2)

        layer.addActor(devUiTable)
    }
}