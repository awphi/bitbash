package ph.adamw.bitbash.game.data

import com.badlogic.gdx.physics.box2d.Body
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene
import java.io.Serializable

abstract class ActorHandler<T>(val name : String) : Serializable {
    open val hasBody : Boolean
        get() = physicsData != null

    open fun mouseClicked(actor: T, button: Int, tilePosition: TilePosition, x: Float, y: Float) {}

    open fun mouseDragged(actor: T, button: Int, tilePosition: TilePosition, x: Float, y: Float) {}

    open fun addAdditionalFixtures(body: Body) {}

    abstract fun getTextureName() : String

    abstract val physicsData : PhysicsData?

    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is ActorHandler<*>) {
            return hashCode() == other.hashCode()
        }

        return false
    }
}