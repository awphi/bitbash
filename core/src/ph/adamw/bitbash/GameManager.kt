package ph.adamw.bitbash

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ph.adamw.bitbash.draw.ShaderBatch
import ph.adamw.bitbash.scene.Scene
import ph.adamw.bitbash.scene.layer.Layer
import ph.adamw.bitbash.scene.layer.MultiLayer
import ph.adamw.bitbash.scene.layer.UILayer
import java.util.*

object GameManager {
    private var scene : Scene? =  null
    var physicsWorld = World(Vector2(0f, 0f), true)
    var rayHandler = RayHandler(physicsWorld)
    val debugRenderer = Box2DDebugRenderer()
    var lockInput = false

    const val MIN_WORLD_WIDTH = 640f
    const val MIN_WORLD_HEIGHT = 480f

    private val playMultiLayer = MultiLayer()
    val uiMultiLayer = MultiLayer()

    val UI_STAGE : Stage = Stage(ScreenViewport())
    val PLAY_STAGE: Stage = Stage(ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT), ShaderBatch(1000))

    val WORLD_CAMERA : OrthographicCamera = PLAY_STAGE.camera!! as OrthographicCamera

    fun loadScene(scene: Scene) : Scene {
        PLAY_STAGE.addActor(playMultiLayer)
        UI_STAGE.addActor(uiMultiLayer)

        playMultiLayer.clear()
        uiMultiLayer.clear()

        UI_STAGE.root.clearListeners()
        PLAY_STAGE.root.clearListeners()

        GameManager.scene = scene

        physicsWorld.dispose()
        rayHandler.dispose()

        physicsWorld = World(Vector2(0f, 0f), true)
        rayHandler = RayHandler(physicsWorld)

        scene.load(playMultiLayer, uiMultiLayer)
        return scene
    }

    fun getScene() : Scene? {
        return scene
    }
}