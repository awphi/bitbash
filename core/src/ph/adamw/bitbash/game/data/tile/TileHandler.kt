package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.physics.box2d.Shape
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.ActorHandler
import java.io.Serializable

abstract class TileHandler(name : String) : ActorHandler<ActorTile>(name), Serializable {
    override fun getTexturePath() : String {
        return name
    }

    override val physicsData: PhysicsData?
        get() = null
}