package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.Direction
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.game.data.world.TileEdgeLocation
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.layer.Layer
import java.rmi.UnexpectedException

class ActorGroupMapRegion : Pool.Poolable {
    private val drawnTiles : Array<Array<ActorTile>> = Array<Array<ActorTile>>(MapRegion.REGION_SIZE) {
        Array<ActorTile>(MapRegion.REGION_SIZE) {
            ActorTile()
        }
    }

    private val edges : com.badlogic.gdx.utils.Array<ActorTileEdge> =  com.badlogic.gdx.utils.Array()
    private val tempDirections : com.badlogic.gdx.utils.Array<Direction> =  com.badlogic.gdx.utils.Array()
    private val tempCoords = TilePosition(0f, 0f)

    var region : MapRegion? = null

    override fun reset() {
        for(i in region!!.widgets.keys) {
            region!!.widgets[i]?.let { undrawWidget(it) }
        }

        for (i in drawnTiles.indices) {
            for (j in drawnTiles[i].indices) {
                drawnTiles[i][j].isVisible = false
            }
        }

        ActorTileEdge.POOL.freeAll(edges)
        edges.clear()
    }

    fun edgeRegion(layer: Layer) {
        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                tempCoords.set(i + region!!.x * MapRegion.REGION_SIZE.toFloat(), j + region!!.y * MapRegion.REGION_SIZE.toFloat())
                edgeTile(tempCoords, layer)
            }
        }
    }

    private fun applyEdge(tile: TileHandler, np: TilePosition, i : TileEdgeLocation, layer: Layer) {
        val edge = ActorTileEdge.POOL.obtain()
        edge.set(tile, np, i)
        layer.addActor(edge)
        edges.add(edge)
    }

    fun edgeTile(np: TilePosition, layer: Layer) {
        val x = np.x
        val y = np.y
        val tile = region!!.getTile(np)
        val ep = tile.edgePriority
        tempDirections.clear()

        for(i in Direction.values()) {
            np.set(x + i.x, y + i.y)
            val t = BitbashPlayScene.map.getTileAt(np)

            if(t != null && t.edgePriority < ep) {
                tempDirections.add(i)
                applyEdge(tile, np, TileEdgeLocation.from(i), layer)
            }
        }

        for(i in TileEdgeLocation.COMPOSITES) {
            if(tempDirections.containsAll(i.components!!, true)) {
                np.set(x + i.x, y + i.y)
                applyEdge(tile, np, i, layer)
            }
        }
    }

    fun loadToStage(tileGroup: Layer, widgetGroup: Layer) {
        if(region == null) {
            throw UnexpectedException("Attempted to call draw() on a drawn map region without a data region assigned!")
        }

        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                tempCoords.set(i + region!!.x * MapRegion.REGION_SIZE.toFloat(), j + region!!.y * MapRegion.REGION_SIZE.toFloat())
                val tile : TileHandler? = region!!.tiles[i][j]
                val widget = region!!.widgets[tempCoords]

                tile?.let {
                    drawnTiles[i][j].set(tile, tempCoords)
                    tileGroup.addActor(drawnTiles[i][j])
                    drawnTiles[i][j].isVisible = true
                }

                widget?.let {
                    drawWidget(widget, widgetGroup)
                }
            }
        }
    }

    fun drawWidget(widget: ActorWidget, g: Group) {
        g.addActor(widget)
    }

    fun removeFromStage() {
        POOL.free(this)
    }

    fun drawTile(np: TilePosition, tile: TileHandler) {
        getDrawnTile(np).set(tile, np)
    }

    private fun getDrawnTile(np: TilePosition) : ActorTile {
        return drawnTiles[Math.floorMod(MathUtils.floor(np.x), MapRegion.REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), MapRegion.REGION_SIZE)]
    }

    fun undrawWidget(actorWidget: ActorWidget) {
        actorWidget.deleteBody()
        actorWidget.remove()
    }

    companion object {
        val POOL : Pool<ActorGroupMapRegion> = Pools.get(ActorGroupMapRegion::class.java)!!
    }
}