package ph.adamw.bitbash.game.data.tile

import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.data.ActorHandler
import ph.adamw.bitbash.game.data.PhysicsData
import java.io.Serializable

abstract class TileHandler(name : String) : ActorHandler<ActorTile>(name), Serializable {
    override fun getTextureName() : String {
        return name
    }

    abstract val edgePriority : Int

    override val physicsData: PhysicsData?
        get() = null
}