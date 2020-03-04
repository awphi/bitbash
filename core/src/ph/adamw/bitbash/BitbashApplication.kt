package ph.adamw.bitbash

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.TimeUtils
import com.kotcrab.vis.ui.VisUI
import org.nustaq.serialization.FSTConfiguration
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.ui.UIUtils
import ph.adamw.bitbash.util.JsonFSTSerializer
import ph.adamw.bitbash.util.TweakedFSTClassInstantiator


class BitbashApplication : ApplicationAdapter() {
    private var lastTimeCounted: Long = 0
    private var sinceChange = 0f
    private var frameRate = 0f

    override fun create() {
        Gdx.input.inputProcessor = GameManager.PLAY_STAGE

        VisUI.load(UIUtils.SKIN)

        //TODO load main menu here instead
        BitbashPlayScene.mapState = MapState.load("Game3")
        GameManager.loadScene(BitbashPlayScene)
    }

    override fun dispose() {
        ActorGameObject.disposeTextureAtlas()
        GameManager.PLAY_STAGE.dispose()
        VisUI.dispose()
        GameManager.physicsWorld.dispose()
        GameManager.rayHandler.dispose()
    }

    override fun pause() {
        GameManager.getScene()?.pause()
    }

    fun updateFps() {
        val delta = TimeUtils.timeSinceMillis(lastTimeCounted)
        lastTimeCounted = TimeUtils.millis()

        sinceChange += delta
        if (sinceChange >= 1000) {
            sinceChange = 0f
            frameRate = Gdx.graphics.framesPerSecond.toFloat()
        }
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        updateFps()

        GameManager.getScene()?.preDraw()

        GameManager.physicsWorld.step(1/60f, 6, 2)
        GameManager.PLAY_STAGE.act(Gdx.graphics.deltaTime)

        GameManager.PLAY_STAGE.draw()
        GameManager.UI_STAGE.draw()
        Gdx.graphics.setTitle("bitbash - FPS: $frameRate")

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
            IO.registerClass(MapRegion::class.java)
            IO.registerClass(Map::class.java)
        }
    }
}
