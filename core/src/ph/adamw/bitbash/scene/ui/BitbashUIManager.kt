package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
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

    val devUiTable = VisTable()

    fun loadMapViewer(stage: Stage) {
        mapMarker.setTexture("marker")
        mapMarker.setScale(0.5f)
        stage.addActor(mapMarker)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>, stage: Stage) {
        for(i in regions) {
            val rg = map.getRegion(i)
            if(rg!!.isDirty) {
                stage.root.removeActor(mapViewerCache[i])
                val r = UIUtils.generateMapRegionOverview(rg)
                r.setPosition(i.x * r.width * r.scaleX, i.y * r.height * r.scaleY)
                mapMarker.toFront()
                stage.addActor(r)
                mapViewerCache[i] = r
            }
        }
    }

    fun updateMapViewerMarker(stage: Stage) {
        val p = BitbashPlayScene.mapState!!.player.readOnlyTilePosition
        val w = (mapMarker.width * mapMarker.scaleX) / (2f * UIUtils.MAP_REGION_OVERVIEW_SCALE)
        val h = (mapMarker.height * mapMarker.scaleY) / (2f * UIUtils.MAP_REGION_OVERVIEW_SCALE)
        mapMarker.setPosition((p.x - w) * UIUtils.MAP_REGION_OVERVIEW_SCALE, (p.y - h) * UIUtils.MAP_REGION_OVERVIEW_SCALE)
        stage.camera.position.set(mapMarker.x + w, mapMarker.y + h, 0f)
        stage.camera.update()
    }

    fun loadDevUIInto(layer: Layer) {
        devUiTable.setFillParent(true)

        devUiTable.pad(10f)

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