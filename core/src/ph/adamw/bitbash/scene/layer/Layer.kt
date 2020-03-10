package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import java.util.*

//TODO potentially create a non-actorder layer class to use for tile layers to reduce performance overhead
open class Layer : Group(), Updatable {
    private val actOrder : TreeSet<Actor> = TreeSet(ActOrderComparator)

    override fun addActorAt(index: Int, actor: Actor?) {
        super.addActorAt(index, actor)
        if (actor != null) {
            actOrder.add(actor)
        }
    }

    override fun addActorAfter(actorAfter: Actor?, actor: Actor?) {
        super.addActorAfter(actorAfter, actor)
        if (actor != null && actorAfter?.parent == this) {
            actOrder.add(actor)
        }
    }

    override fun addActorBefore(actorBefore: Actor?, actor: Actor?) {
        super.addActorBefore(actorBefore, actor)
        if (actor != null && actorBefore?.parent == this) {
            actOrder.add(actor)
        }
    }

    override fun addActor(actor: Actor?) {
        super.addActor(actor)
        if (actor != null) {
            actOrder.add(actor)
        }
    }

    override fun act(delta: Float) {
        if (actions.size > 0) {
            if (stage != null && stage.actionsRequestRendering) Gdx.graphics.requestRendering()
            try {
                var i = 0
                while (i < actions.size) {
                    val action = actions[i]
                    if (action.act(delta) && i < actions.size) {
                        val current = actions[i]
                        val actionIndex = if (current === action) i else actions.indexOf(action, true)
                        if (actionIndex != -1) {
                            actions.removeIndex(actionIndex)
                            action.actor = null
                            i--
                        }
                    }
                    i++
                }
            } catch (ex: RuntimeException) {
                val context = toString()
                throw RuntimeException("Actor: " + context.substring(0, Math.min(context.length, 128)), ex)
            }
        }


        val it = actOrder.iterator()
        while (it.hasNext()) {
            it.next().act(delta)
        }
    }

    override fun removeActor(actor: Actor?): Boolean {
        val y =  super.removeActor(actor)

        if(y) {
            actOrder.remove(actor)
        }

        return y
    }

    override fun update(actor: Actor) {
        if(actOrder.remove(actor)) {
            actOrder.add(actor)
        }
    }
}