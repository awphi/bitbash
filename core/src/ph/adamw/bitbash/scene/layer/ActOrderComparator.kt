package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.scenes.scene2d.Actor
import ph.adamw.bitbash.game.actor.ActorGameObject

object ActOrderComparator: Comparator<Actor> {
    override fun compare(o1: Actor?, o2: Actor?): Int {
        var a1 = ActorGameObject.DEFAULT_ACT_PRIORITY
        var a2 = ActorGameObject.DEFAULT_ACT_PRIORITY

        if(o1 is ActorGameObject) {
            a1 = o1.actPriority
        }

        if(o2 is ActorGameObject) {
            a2 = o2.actPriority
        }

        return if(a1 >= a2) {
            1
        } else {
            -1
        }
    }
}