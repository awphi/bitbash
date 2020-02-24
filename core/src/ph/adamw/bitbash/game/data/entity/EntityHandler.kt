package ph.adamw.bitbash.game.data.entity

import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.data.ActorHandler
import ph.adamw.bitbash.game.data.world.TilePosition

abstract class EntityHandler<T>(name: String) : ActorHandler<T>(name) {
    open fun act(actorEntity: T, delta: Float, tilePosition: TilePosition) {}
}