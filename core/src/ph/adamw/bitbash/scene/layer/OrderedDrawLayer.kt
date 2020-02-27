package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.scenes.scene2d.Actor
import ph.adamw.bitbash.game.actor.ActorEntity

class OrderedDrawLayer(val comparator: Comparator<Actor>) : Layer() {
    fun update(actor: Actor) {
        val idx = findIndex(actor)

        if(idx == actor.zIndex) {
            return
        }

        if(idx >= children.size) {
            actor.zIndex = Int.MAX_VALUE
        } else {
            actor.zIndex = 0.coerceAtLeast(idx - 1)
        }
    }

    override fun addActor(actor: Actor?) {
        addActorAt(findIndex(actor), actor)
    }

    private fun findIndex(actor: Actor?) : Int {
        for(idx in 0 until children.size) {
            val i = children[idx]

            if(i == actor) {
                continue
            }

            val c = comparator.compare(actor, i)
            if(c == 0 || c == -1) {
                return idx
            }
        }

        return children.size
    }

}