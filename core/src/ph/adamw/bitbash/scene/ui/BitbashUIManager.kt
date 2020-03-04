package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
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

    fun loadMapViewer(layer: Layer, stage: Stage) {
        mapMarker.setTexture("marker")
        stage.addActor(mapMarker)

        mapUiTable.setFillParent(true)

        mapUiTable.pad(50f)
        mapUiTable.add("Map Viewer").center()
        mapUiTable.row()

        //TODO cull map viewer stage to this cell's bounds
        mapViewerCell = mapUiTable.add().fill().grow()
        layer.addActor(mapUiTable)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>, stage: Stage) {
        //TODO when not in dev mode only show the active regions - not all of em
        for(i in regions) {
            val rg = map.getRegion(i)
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
            val w1 = mapViewerCell!!.actorWidth
            val h1 = mapViewerCell!!.actorHeight
            val r = mapUiTable.getRowHeight(0) / 2f
            clipBounds.set(stage.camera.position.x - w1 / 2f, stage.camera.position.y - h1 / 2f - r, w1, h1)
            ScissorStack.calculateScissors(stage.camera, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(), stage.batch.transformMatrix, clipBounds, scissors)
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