package ph.adamw.bitbash

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Json
import com.kotcrab.vis.ui.VisUI
import org.nustaq.serialization.FSTConfiguration
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.entity.ActorPlayer
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.scene.BitbashInfiniteScene
import ph.adamw.bitbash.scene.ui.UIConstants
import ph.adamw.bitbash.util.JsonFSTSerializer
import ph.adamw.bitbash.util.TweakedFSTClassInstantiator


class BitbashApplication : ApplicationAdapter() {
    override fun create() {
        Gdx.input.inputProcessor = GameManager.STAGE

        VisUI.load(UIConstants.SKIN)

        //TODO load main menu here instead
        BitbashInfiniteScene.mapState = MapState.load("Game3")
        GameManager.loadScene(BitbashInfiniteScene)
    }

    override fun dispose() {
        ActorGameObject.disposeTextureAtlas()
        GameManager.STAGE.dispose()
        VisUI.dispose()
        GameManager.physicsWorld.dispose()
        GameManager.rayHandler.dispose()
    }

    override fun pause() {
        GameManager.getScene()?.pause()
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        GameManager.getScene()?.preDraw()
        for(i in GameManager.UI_LAYERS) {
            i.setPosition(GameManager.MAIN_CAMERA.position.x - (GameManager.STAGE.width / 2f),
                    GameManager.MAIN_CAMERA.position.y - (GameManager.STAGE.height / 2f))
            i.setSize(GameManager.STAGE.width, GameManager.STAGE.height)
        }

        GameManager.physicsWorld.step(1/60f, 6, 2)
        GameManager.STAGE.act(Gdx.graphics.deltaTime)

        GameManager.STAGE.draw()

        if(DEBUG) {
            GameManager.debugRenderer.render(GameManager.physicsWorld, GameManager.MAIN_CAMERA.combined)
        }
        GameManager.getScene()?.postDraw()
    }

    override fun resize(width: Int, height: Int) {
        GameManager.getScene()?.resize(width, height)
    }

    companion object {
        val DEBUG = System.getProperty("debug") == "true"
        val IO : FSTConfiguration = FSTConfiguration.createDefaultConfiguration()
        val JSON = Json()

        init {
            IO.setInstantiator(TweakedFSTClassInstantiator())
            IO.registerSerializer(ActorEntity::class.java, JsonFSTSerializer, true)
            IO.registerClass(MapRegion::class.java)
            IO.registerClass(Map::class.java)
        }
    }
}
