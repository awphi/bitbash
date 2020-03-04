package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.layer.Layer

object BitbashUILoader {
    private val mapViewerTable = VisTable()
    private val mapContent = Group()

    fun loadMapViewer(layer: Layer) {
        mapViewerTable.setFillParent(true)
        mapViewerTable.pad(20f)
        if(BitbashApplication.DEBUG) {
            mapViewerTable.debugAll()
        }

        val label = Label("MapViewer Mk III", UIUtils.SKIN)
        mapViewerTable.add(label).center()
        mapViewerTable.row()
        mapViewerTable.add(mapContent).fill().grow().center()
        layer.addActor(mapViewerTable)
    }

    fun updateMapViewer(map: Map, regions: HashSet<Vector2>) {
        //TODO implement a dirty region system to know whether this needs updating or not
        //  right now performance is terrible
        for(i in regions) {
            //mapContent.addActor(UIUtils.generateMapRegionOverview(map.getRegion(i)!!))
        }
    }
}