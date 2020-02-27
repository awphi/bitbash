package ph.adamw.bitbash.util

import com.badlogic.gdx.scenes.scene2d.Actor
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.ActorGameObject

object EntityComparator : Comparator<Actor> {
    override fun compare(o1: Actor?, o2: Actor?): Int {
        if(o1 == null || o2 == null) {
            return 0
        }

        var y1 = o1.y
        var y2 = o2.y

        var d1 = ActorEntity.DEFAULT_DRAW_PRIORITY
        var d2 = ActorEntity.DEFAULT_DRAW_PRIORITY

        if(o1 is ActorEntity) {
            d1 = o1.drawPriority
        }

        if(o2 is ActorEntity) {
            d2 = o2.drawPriority
        }

        if(o1 is ActorGameObject && o2 is ActorGameObject) {
            if(o1.hasBody) {
                y1 = o1.body.transform.position.y
            }

            if(o2.hasBody) {
                y2 = o2.body.transform.position.y
            }
        }

        if(d1 > d2) {
            return -1
        } else if(d2 > d1) {
            return 1
        } else {
            if (y1 > y2) {
                return -1
            } else if (y1 < y2) {
                return 1
            }
        }

        return 0
    }
}