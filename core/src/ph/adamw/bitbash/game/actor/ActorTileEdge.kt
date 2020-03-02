package ph.adamw.bitbash.game.actor.widget

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TileEdgeLocation
import ph.adamw.bitbash.game.data.world.TilePosition

class ActorWidgetTileEdge : ActorWidget(), Pool.Poolable {
    override val initialTexturePath: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val actorName: String
        get() = "tile_null_edge"

    override val physicsData: PhysicsData?
        get() = null

    fun set(tileHandler: TileHandler, tilePosition: TilePosition, loc: TileEdgeLocation) {

    }

    override fun reset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val POOL : Pool<ActorWidgetTileEdge> = Pools.get(ActorWidgetTileEdge::class.java)
    }
}