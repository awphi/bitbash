package ph.adamw.bitbash.game.data

import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.physics.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene
import java.io.Serializable

abstract class ActorHandler<T : ActorGameObject>(val name : String) : Serializable {
    open val hasBody : Boolean
        get() = physicsData != null

    open fun mouseClicked(actor: T, button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {}

    open fun mouseDragged(actor: T, button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {}

    abstract fun getTexturePath() : String

    abstract val physicsData : PhysicsData?
}