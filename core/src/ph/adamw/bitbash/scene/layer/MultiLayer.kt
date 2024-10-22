package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import ph.adamw.bitbash.BitbashApplication
import java.util.*

class MultiLayer : Group() {
    private val layers = TreeMap<Int, Group>(Collections.reverseOrder())
    private val debugColor : Color = nextDebugColor()

    init {
        name = "MultiLayer" + hashCode()

        if(BitbashApplication.DEBUG) {
            debug = true
        }
    }

    fun addSelfOrderedLayer(prio: Int) : SelfOrderedLayer {
        return addGroup(prio, SelfOrderedLayer()) as SelfOrderedLayer
    }

    fun addGroup(prio: Int) : Group {
        return addGroup(prio, Group())
    }

    fun addMultiLayer(prio: Int) : MultiLayer {
        return addGroup(prio, MultiLayer()) as MultiLayer
    }

    fun addUiLayer(prio: Int) : UILayer {
        return addGroup(prio, UILayer()) as UILayer
    }

    fun addOrGetGroup(prio: Int) : Group {
        if(!layers.containsKey(prio)) {
            addGroup(prio)
        }

        return layers[prio] as Group
    }

    fun addDefaultLayer(prio: Int) : Layer {
        return addGroup(prio, Layer()) as Layer
    }

    fun addGroup(prio: Int, layer: Group) : Group {
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

    operator fun iterator(): MutableIterator<MutableMap.MutableEntry<Int, Group>> {
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