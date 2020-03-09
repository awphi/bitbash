package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.WaterTileHandler
import ph.adamw.bitbash.game.data.world.*
import ph.adamw.bitbash.scene.BitbashPlayScene
import ph.adamw.bitbash.scene.layer.Layer
import java.rmi.UnexpectedException

class ActorGroupMapRegion : Pool.Poolable {
    private val drawnTiles : Array<Array<ActorTile>> = Array<Array<ActorTile>>(MapRegion.REGION_SIZE) {
        Array<ActorTile>(MapRegion.REGION_SIZE) {
            ActorTile()
        }
    }

    private val edgeMap : HashMap<TilePosition, com.badlogic.gdx.utils.Array<ActorTileEdge>> = HashMap()
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

        for(i in edgeMap.keys) {
            edgeMap[i]?.let {
                ActorTileEdge.POOL.freeAll(it)
            }
        }

        edgeMap.clear()
    }

    fun edgeRegion(layer: Layer) {
        region!!.markUndirty(MapRegionFlag.NEEDS_EDGE)
        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                region!!.localTileIndexToWorldTilePosition(i, j, tempCoords)
                edgeTile(tempCoords, layer)
            }
        }
    }

    private fun unedgeTile(np: TilePosition) {
        edgeMap[np]?.let {
            ActorTileEdge.POOL.freeAll(it)
            it.clear()
        }
    }

    private fun applyEdge(tile: TileHandler, tileOnto: TileHandler?, x : Float, y : Float, np: TilePosition, i : TileEdgeLocation, layer: Layer) {
        val edge = ActorTileEdge.POOL.obtain()
        edge.set(tile, np, i)

        layer.addActor(edge)

        if(tileOnto != null && tile.edgePriority < tileOnto.edgePriority) {
            edge.toFront()
        } else {
            edge.toBack()
        }

        np.set(x, y)

        if(!edgeMap.containsKey(np)) {
            edgeMap[np] = com.badlogic.gdx.utils.Array()
        }

        edgeMap[np]!!.add(edge)
    }

    private fun edgeTile(tp: TilePosition, layer: Layer) {
        val np = TilePosition(tp.x, tp.y)
        unedgeTile(np)

        val x = np.x
        val y = np.y
        val tile = region!!.getTile(np)
        val ep = tile.edgePriority
        tempDirections.clear()

        for(i in Direction.values()) {
            np.set(x + i.x, y + i.y)
            val t = BitbashPlayScene.map.getTileAt(np)

            if(t != null && (t.edgePriority > ep || (ep != 0 && t.edgePriority == 0))) {
                tempDirections.add(i)
                applyEdge(tile, t, x, y, np, TileEdgeLocation.from(i), layer)
            }
        }

        //TODO work out sometimes why corner draws even tho the two edges that
        for(i in TileEdgeLocation.COMPOSITES) {
            if(tempDirections.containsAll(i.components!!, true)) {
                np.set(x + i.x, y + i.y)
                applyEdge(tile, BitbashPlayScene.map.getTileAt(np), x, y, np, i, layer)
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