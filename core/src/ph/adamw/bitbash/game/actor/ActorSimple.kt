package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.PhysicsData

open class ActorSimple(override val actorName: String) : ActorGameObject() {
    init {
        debug = false
    }

    override val physicsData: PhysicsData?
        get() = null
}