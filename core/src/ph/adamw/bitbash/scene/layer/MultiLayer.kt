package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import ph.adamw.bitbash.BitbashApplication
import java.util.*

class MultiLayer : Group(), Updatable {
    private val debugColor : Color = nextDebugColor()

    init {
        name = "MultiLayer" + hashCode()

        if(BitbashApplication.DEBUG) {
            debug = true
        }
    }

    private val layers = TreeMap<Int, Actor>(Collections.reverseOrder())

    fun addSelfOrderedLayer(prio: Int) : SelfOrderedLayer {
        return addLayer(prio, SelfOrderedLayer()) as SelfOrderedLayer
    }

    fun addMultiLayer(prio: Int) : MultiLayer {
        return addLayer(prio, MultiLayer()) as MultiLayer
    }

    fun addUiLayer(prio: Int) : UILayer {
        return addLayer(prio, UILayer()) as UILayer
    }

    fun addOrGetDefaultLayer(prio: Int) : Layer {
        if(!layers.containsKey(prio)) {
            addDefaultLayer(prio)
        }

        return layers[prio] as Layer
    }

    fun addDefaultLayer(prio: Int) : Layer {
        return addLayer(prio, Layer()) as Layer
    }

    fun addLayer(prio: Int, layer: Actor) : Actor {
        if(layers.containsKey(prio)) {
            throw RuntimeException("MultiLayer" + hashCode() +  " already contains a layer at " + prio + "!")
        }

        addActor(layer)
        layers[prio] = layer
        return layers[prio]!!
    }

    override fun act(delta: Float) {
        super.act(delta)

        for(i in layers) {
            i.value.act(delta)
        }
    }

    fun removeLayer(prio: Int) {
        val t = layers.remove(prio)

        if(t is Layer) {
            t.clear()
        }
    }

    override fun draw(batch: Batch, parentAlpha : Float) {
        for(i in layers) {
            if(i.value.isVisible && i.value.color.a > 0f) {
                i.value.draw(batch, parentAlpha)
            }
        }
    }

    operator fun iterator(): MutableIterator<MutableMap.MutableEntry<Int, Actor>> {
        return layers.iterator()
    }

    override fun clear() {
        for(i in layers.values) {
            i.clear()
        }

        layers.clear()
    }

    override fun drawDebug(shapes: ShapeRenderer?) {
        for(i in layers) {
            if(i.value.isVisible) {
                shapes!!.color = debugColor
                i.value.drawDebug(shapes)
            }
        }
    }

    override fun update(actor: Actor) {}

    companion object {
        private var idx = 0

        private val colors = arrayOf(
                Color.WHITE, // Play layer
                Color.WHITE, // UI layer
                Color.MAGENTA,
                Color.YELLOW,
                Color.CYAN,
                Color.RED,
                Color.LIME,
                Color.CORAL
        )

        private fun nextDebugColor() : Color {
            val t = colors[idx]
            idx = (idx + 1) % colors.size
            return t
        }
    }
}