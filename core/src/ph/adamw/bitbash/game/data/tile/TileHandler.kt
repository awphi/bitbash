package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.physics.box2d.Shape
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.physics.PhysicsData
import ph.adamw.bitbash.game.data.ActorHandler
import java.io.Serializable

abstract class TileHandler(name : String) : ActorHandler<ActorTile>(name), Serializable {
    override fun getTexturePath() : String {
        return name
    }

    abstract override val hasBody: Boolean

    override val physicsData: PhysicsData?
        get() = Physics

    object Physics : PhysicsData() {
        override val principleWidth: Float
            get() = ActorTile.SIZE

        override val principleHeight: Float
            get() = ActorTile.SIZE

        override val principleFixtureType: Shape.Type
            get() = Shape.Type.Polygon
    }
}