package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.world.TileEdgeLocation
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.MultiLayer

class ActorTileEdge : ActorGameObject(), Pool.Poolable {
    private var handler : TileHandler = GrassTileHandler

    init {
        touchable = Touchable.disabled
    }

    override val drawPriority: Int
        get() = handler.drawPriority

    override val actPriority: Int
        get() = 1000

    override val actorName: String
        get() = "tile_empty_edge_upleft"

    override val physicsData: PhysicsData?
        get() = null

    fun set(handler: TileHandler, tileLayer : MultiLayer, tilePosition: TilePosition, loc: TileEdgeLocation) {
        this.handler = handler
        name = "tile_" + handler.name + "_edge_" + loc.toString().toLowerCase()
        setTexture("${handler.getTextureName()}_edge_${loc.toString().toLowerCase()}")
        setPosition(tilePosition.getWorldX(), tilePosition.getWorldY())
        align(loc)

        color.set(handler.color)

        tileLayer.addOrGetGroup(drawPriority).addActor(this)
        isVisible = true
    }

    private fun align(loc: TileEdgeLocation) {
        if(loc == TileEdgeLocation.LEFT || loc == TileEdgeLocation.UPLEFT || loc == TileEdgeLocation.DOWNLEFT) {
            x += ActorTile.SIZE - width
        }

        if(loc == TileEdgeLocation.DOWN || loc == TileEdgeLocation.DOWNLEFT || loc == TileEdgeLocation.DOWNRIGHT) {
            y += ActorTile.SIZE - height
        }
    }

    override fun reset() {
        isVisible = false
    }

    companion object {
        val POOL : Pool<ActorTileEdge> = Pools.get(ActorTileEdge::class.java)
    }
}