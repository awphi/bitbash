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

object GameManager {
    private var scene : Scene? =  null
    var physicsWorld = World(Vector2(0f, 0f), true)
    var rayHandler = RayHandler(physicsWorld)
    val debugRenderer = Box2DDebugRenderer()
    var lockInput = false

    const val MIN_WORLD_WIDTH = 640f
    const val MIN_WORLD_HEIGHT = 480f

    private val PLAY_LAYERS = TreeMap<Int, Layer>()
    internal val UI_LAYERS = TreeMap<Int, Layer>()

    val UI_STAGE : Stage = Stage(ScreenViewport())
    val PLAY_STAGE: Stage = Stage(ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT), ShaderBatch(1000))

    val WORLD_CAMERA = PLAY_STAGE.camera

    fun loadScene(scene: Scene) : Scene {
        GameManager.scene = scene

        PLAY_LAYERS.clear()
        UI_STAGE.clear()
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

    private fun addLayerInternal(layer: Int, g: Layer, map: TreeMap<Int, Layer>, stage: Stage) {
        g.touchable = Touchable.childrenOnly

        var flag = false

        for (i in map.keys) {
            if (i > layer) {
                stage.root.addActorBefore(map[i], g)
                flag = true
                break
            }
        }

        if(!flag) {
            stage.addActor(g)
        }

        map[layer] = g
    }

    fun getWorldLayer(layer: Int) : Layer {
        return getLayer(layer, Layer(), PLAY_LAYERS, PLAY_STAGE)
    }

    fun getUiLayer(layer: Int) : Layer {
        return getLayer(layer, UILayer(), UI_LAYERS, UI_STAGE)
    }

    private fun getLayer(layer: Int, g: Layer, map: TreeMap<Int, Layer>, stage: Stage) : Layer {
        if(!map.containsKey(layer)) {
            addLayerInternal(layer, g, map, stage)
        }

        return map[layer]!!
    }
}