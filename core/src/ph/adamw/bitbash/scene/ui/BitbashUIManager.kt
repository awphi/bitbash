package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.layer.Layer
import java.util.*
import kotlin.collections.HashSet

object BitbashUIManager {
    private val mapViewerTable = VisTable()
    private val mapContent = WidgetGroup()
    private val mapViewerCache = Hashtable<Vector2, Actor>()

    fun loadMapViewer(layer: Layer) {
        mapViewerTable.setFillParent(true)
        mapViewerTable.pad(20f)
        if(BitbashApplication.DEBUG) {
            mapViewerTable.debugAll()
            mapContent.debug()
        }

        mapContent.setFillParent(true)

        val label = Label("MapViewer Mk III", UIUtils.SKIN)
        mapViewerTable.add(label).center()
        mapViewerTable.row()
        mapViewerTable.add(mapContent).fill().grow()
        layer.addActor(mapViewerTable)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>) {
        for(i in regions) {
            val rg = map.getRegion(i)
            if(rg!!.isDirty) {
                mapContent.removeActor(mapViewerCache[i])
                val r = UIUtils.generateMapRegionOverview(rg)
                r.setPosition(i.x * 16, i.y * 16)
                val p = BitbashPlayScene.mapState!!.player.readOnlyTilePosition
                mapContent.addActor(r)
                mapViewerCache[i] = r
            }
        }
    }
}