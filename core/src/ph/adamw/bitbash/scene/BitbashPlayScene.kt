package ph.adamw.bitbash.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.Layer
import ph.adamw.bitbash.scene.layer.MultiLayer
import ph.adamw.bitbash.scene.ui.BitbashUIManager

object BitbashPlayScene : BitbashCoreScene() {
    private var devUi : Layer? = null
    private var mapUi : Layer? = null

    private val mapViewer : Stage = Stage(ExtendViewport(GameManager.MIN_WORLD_WIDTH, GameManager.MIN_WORLD_HEIGHT))

    override fun load(playMultiLayer: MultiLayer, uiMultiLayer: MultiLayer) {
        super.load(playMultiLayer, uiMultiLayer)
        mapViewer.root.debug()
        devUi = uiMultiLayer.addUiLayer(0)
        mapUi = uiMultiLayer.addUiLayer(1)
        BitbashUIManager.loadDevUIInto(devUi!!)
        BitbashUIManager.loadMapViewer(mapUi!!, mapViewer, map, map.discoveredRegions)
        Gdx.app.log("TEST", "HELLO")
        mapViewer.root.isVisible = false
        mapUi!!.isVisible = false
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        mapViewer.viewport.update(width, height, true)
    }

    override fun postDraw() {
        super.postDraw()

        BitbashUIManager.updateMapViewerMarker(mapViewer)?.let {
            if(ScissorStack.pushScissors(it)) {
                mapViewer.draw()
                mapViewer.batch.flush()
                ScissorStack.popScissors()
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            mapViewer.root.isVisible = !mapViewer.root.isVisible
            mapUi!!.isVisible = !mapUi!!.isVisible
        }

        BitbashUIManager.fpsLabel.setText(GameManager.getScene()?.frameRate.toString() + " fps")
        BitbashUIManager.velocityLabel.setText("Velocity: " + mapState?.player?.body?.linearVelocity.toString())
        BitbashUIManager.positionLabel.setText("Position: " + mapState?.player?.readOnlyTilePosition.toString())
    }

    override fun activeRegionsUpdated() {
        BitbashUIManager.updateMapViewer(map, activeRegionCoords, mapViewer)
    }
}
