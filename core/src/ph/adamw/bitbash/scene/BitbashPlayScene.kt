package ph.adamw.bitbash.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.scene.layer.Layer
import ph.adamw.bitbash.scene.ui.BitbashUIManager

object BitbashPlayScene : BitbashCoreScene() {
    var devUi : Layer? = null
    var mapUi : Layer? = null

    val mapViewer : Stage = Stage(ExtendViewport(640f, 480f))

    override fun load() {
        super.load()
        mapViewer.root.debug()
        devUi = GameManager.getUiLayer(0)
        mapUi = GameManager.getUiLayer(1)
        BitbashUIManager.loadDevUIInto(devUi!!)
        BitbashUIManager.loadMapViewer(mapViewer)
        mapViewer.root.isVisible = false
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        mapViewer.viewport.update(width, height, true)
    }

    override fun postDraw() {
        super.postDraw()
        BitbashUIManager.updateMapViewerMarker(mapViewer)
        mapViewer.draw()

        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            mapViewer.root.isVisible = !mapViewer.root.isVisible
        }
    }

    override fun activeRegionsUpdated() {
        BitbashUIManager.updateMapViewer(map, activeRegionCoords, mapViewer)
    }
}
