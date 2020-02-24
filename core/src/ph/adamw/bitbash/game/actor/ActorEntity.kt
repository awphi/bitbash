package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.entity.EntityHandler
import ph.adamw.bitbash.game.data.entity.mob.PlayerHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.SortedLayer

abstract class ActorEntity<T, B : EntityHandler<T>>(handler: B) : ActorGameObject<B>(handler) {
    init {
        setTexture(handler.getTexturePath())
    }

    open fun actEntity(actorEntity: T, delta: Float, tilePosition: TilePosition) {
        handler.act(this as T, delta, tilePosition)
    }

    override fun act(delta: Float) {
        super.act(delta)
        actEntity(this as T, delta, readOnlyTilePosition)
        if(hasBody) {
            val bx = body.position.x - (physicsData!!.principleWidth / 2f)
            val by = body.position.y - (physicsData!!.principleHeight / 2f)
            setPosition(bx, by)
        }
    }

    override fun positionChanged() {
        super.positionChanged()
        if(parent != null) {
            (parent as SortedLayer).update(this)
        }
    }
}