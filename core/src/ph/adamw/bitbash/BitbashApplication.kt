package ph.adamw.bitbash

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Json
import com.kotcrab.vis.ui.VisUI
import org.nustaq.serialization.FSTConfiguration
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.ui.BitbashUIManager
import ph.adamw.bitbash.scene.ui.UIUtils
import ph.adamw.bitbash.util.io.JsonFSTSerializer
import ph.adamw.bitbash.util.io.TileHandlerFSTSerializer
import ph.adamw.bitbash.util.io.TweakedFSTClassInstantiator


class BitbashApplication : ApplicationAdapter() {
    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer(GameManager.UI_STAGE, GameManager.PLAY_STAGE)

        VisUI.load(UIUtils.SKIN)

        //TODO load main menu here instead
        BitbashPlayScene.mapState = MapState.load("Game3")
        GameManager.loadScene(BitbashPlayScene)
    }

    override fun dispose() {
        ActorGameObject.disposeTextureAtlas()
        GameManager.PLAY_STAGE.dispose()
        UIUtils.dispose()
        VisUI.dispose()
        Map.threadPool.shutdown()
        GameManager.physicsWorld.dispose()
        GameManager.rayHandler.dispose()
        GameManager.getScene()?.dispose()
        BitbashUIManager.dispose()
    }

    override fun pause() {
        GameManager.getScene()?.pause()
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        GameManager.getScene()?.preDraw()

        GameManager.physicsWorld.step(1/60f, 6, 2)
        GameManager.PLAY_STAGE.act(Gdx.graphics.deltaTime)

        GameManager.PLAY_STAGE.draw()
        GameManager.UI_STAGE.draw()
        Gdx.graphics.setTitle("bitbash - FPS: ${GameManager.getScene()?.frameRate}")

        if(DEBUG) {
            GameManager.debugRenderer.render(GameManager.physicsWorld, GameManager.WORLD_CAMERA.combined.scl(PhysicsData.PPM))
        }
        GameManager.getScene()?.postDraw()
    }

    override fun resize(width: Int, height: Int) {
        GameManager.getScene()?.resize(width, height)
    }

    companion object {
        val DEBUG = System.getProperty("debug") == "true"
        val GEN_TEST = System.getProperty("gentest") == "true"
        val IO : FSTConfiguration = FSTConfiguration.createDefaultConfiguration()
        val JSON = Json()

        init {
            IO.setInstantiator(TweakedFSTClassInstantiator())
            IO.registerSerializer(ActorEntity::class.java, JsonFSTSerializer, true)
            IO.registerSerializer(TileHandler::class.java, TileHandlerFSTSerializer, true)
            IO.registerClass(MapRegion::class.java)
            IO.registerClass(Map::class.java)
        }
    }
}
