package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import java.lang.RuntimeException
import java.util.*

class MultiLayer : ILayer {
    private val layers = TreeMap<Int, ILayer>()

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

    fun addLayer(prio: Int, layer: ILayer) : ILayer {
        if(layers.containsKey(prio)) {
            throw RuntimeException("MultiLayer" + hashCode() +  " already contains a layer at " + prio + "!")
        }

        layers[prio] = layer
        return layers[prio]!!
    }

    fun getLayer(prio: Int) : ILayer? {
        return layers[prio]
    }

    fun removeLayer(prio: Int) {
        val t = layers.remove(prio)

        if(t is Layer) {
            t.clear()
        }
    }

    override fun draw(batch: Batch, parentAlpha : Float) {
        for(i in layers) {
            i.value.draw(batch, parentAlpha)
        }
    }

    override fun update(actor: Actor) {}
}