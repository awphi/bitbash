package ph.adamw.bitbash

import box2dLight.RayHandler
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
import ph.adamw.bitbash.scene.layer.UILayer
import java.util.*
import kotlin.collections.ArrayList

object GameManager {
    private var scene : Scene? =  null
    var physicsWorld = World(Vector2(0f, 0f), true)
    var rayHandler = RayHandler(physicsWorld)
    val debugRenderer = Box2DDebugRenderer()
    var lockInput = false

    const val MIN_WORLD_WIDTH = 640f
    const val MIN_WORLD_HEIGHT = 480f

    private val STAGE_LAYERS = TreeMap<Int, Layer>()
    val UI_LAYERS = ArrayList<Layer>()
    val UI_STAGE : Stage = Stage(ScreenViewport())
    val PLAY_STAGE: Stage = Stage(ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT), ShaderBatch(1000))
    val MAIN_CAMERA = PLAY_STAGE.camera

    fun loadScene(scene: Scene) : Scene {
        GameManager.scene = scene

        STAGE_LAYERS.clear()
        UI_LAYERS.clear()
        PLAY_STAGE.root.clearListeners()

        physicsWorld.dispose()
        rayHandler.dispose()

        physicsWorld = World(Vector2(0f, 0f), true)
        rayHandler = RayHandler(physicsWorld)

        scene.load()
        return scene
    }

    fun getScene() : Scene? {
        return scene
    }

    private fun addStageLayer(layer: Int, g: Layer) {
        g.touchable = Touchable.childrenOnly

        var flag = false

        for (i in STAGE_LAYERS.keys) {
            if (i > layer) {
                PLAY_STAGE.root.addActorBefore(STAGE_LAYERS[i], g)
                flag = true
                break
            }
        }

        if(!flag) {
            PLAY_STAGE.addActor(g)
        }

        STAGE_LAYERS[layer] = g
    }

    fun getStageLayer(layer: Int) : Layer {
        return getStageLayer(layer, Layer())
    }

    fun getUiLayer(layer: Int) : Layer {
        return getStageLayer(layer, UILayer())
    }

    fun getStageLayer(layer : Int, ly: Layer) : Layer {
        if(!STAGE_LAYERS.containsKey(layer)) {
            addStageLayer(layer, ly)
        }

        return STAGE_LAYERS[layer]!!
    }
}