package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.PhysicsData

class ActorSimple(override val actorName: String) : ActorGameObject() {
    override val physicsData: PhysicsData?
        get() = null
}