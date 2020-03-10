package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import java.lang.RuntimeException
import java.util.*
import kotlin.Comparator

class MultiLayer : Actor(), Updatable {
    private val layers = TreeMap<Int, Actor>(Collections.reverseOrder())

    fun addYOrderedLayer(prio: Int) : YOrderedLayer {
        return addLayer(prio, YOrderedLayer()) as YOrderedLayer
    }

    fun addMultiLayer(prio: Int) : MultiLayer {
        return addLayer(prio, MultiLayer()) as MultiLayer
    }

    fun addUiLayer(prio: Int) : UILayer {
        return addLayer(prio, UILayer()) as UILayer
    }

    fun addDefaultLayer(prio: Int) : Layer {
        return addLayer(prio, Layer()) as Layer
    }

    fun addLayer(prio: Int, layer: Actor) : Actor {
        if(layers.containsKey(prio)) {
            throw RuntimeException("MultiLayer" + hashCode() +  " already contains a layer at " + prio + "!")
        }

        layers[prio] = layer
        return layers[prio]!!
    }

    override fun act(delta: Float) {
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

    override fun update(actor: Actor) {}
}