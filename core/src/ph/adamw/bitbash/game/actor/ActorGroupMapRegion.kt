package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.data.tile.TileHandler
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

    private val tempCoords : TilePosition = TilePosition(0f, 0f)

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

        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                region!!.localTileIndexToWorldTilePosition(i, j, tempCoords)
                unedgeTile(tempCoords)
            }
        }
    }

    fun edgeRegion(layer: Layer) {
        Gdx.app.log("EDGE-" + Thread.currentThread().id, "Re-applying edges to ${region!!}.")
        val temp = TilePosition(0f, 0f)

        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                region!!.localTileIndexToWorldTilePosition(i, j, temp)
                edgeTile(temp.copy(), layer)
            }
        }

        region!!.markUndirty(MapRegionFlag.NEEDS_EDGE)
    }

    private fun unedgeTile(np: TilePosition) {
        if(edgeMap.containsKey(np)) {
            val edges = edgeMap[np]!!
            Gdx.app.log("EDGE", "Properly unedged $np " + edges.size)

            for(i in edges) {
                ActorTileEdge.POOL.free(i)
            }

            edges.clear()
        }
    }

    private fun applyEdge(tileFrom: TileHandler, tileOnto: TileHandler?, temp: TilePosition, tp: TilePosition, edgeLocation : TileEdgeLocation, layer: Layer) {
        val edge = ActorTileEdge.POOL.obtain()
        edge.set(tileFrom, temp, edgeLocation)
        layer.addActor(edge)

        if(tileOnto != null && tileFrom.edgePriority < tileOnto.edgePriority) {
            edge.toFront()
        } else {
            edge.toBack()
        }

        if(!edgeMap.containsKey(tp)) {
            edgeMap[tp] = HashSet()
        }

        edgeMap[tp]!!.add(edge)
    }

    private fun edgeTile(originalPosition: TilePosition, layer: Layer) {
        //TODO fix unedging
        unedgeTile(originalPosition)

        val tile = region!!.getTile(originalPosition)
        val ep = tile.edgePriority

        val tempPosition = originalPosition.copy()
        val tempDirections = HashSet<Direction>()

        for(i in Direction.values()) {
            tempPosition.set(originalPosition).add(i.x, i.y)
            val tileOnto = BitbashPlayScene.map.getTileAt(tempPosition)
            if(tileOnto != null && (tileOnto.edgePriority > ep || (ep != 0 && tileOnto.edgePriority == 0))) {
                tempDirections.add(i)
                applyEdge(tile, tileOnto, tempPosition, originalPosition, TileEdgeLocation.from(i), layer)
            }
        }

        for(i in TileEdgeLocation.COMPOSITES) {
            if(tempDirections.containsAll(i.components!!)) {
                tempPosition.set(originalPosition).add(i.x, i.y)
                applyEdge(tile, BitbashPlayScene.map.getTileAt(tempPosition), tempPosition, originalPosition, i, layer)
            }
        }
    }

    fun loadToStage(tileGroup: Layer, widgetGroup: Layer) {
        if(region == null) {
            throw UnexpectedException("Attempted to call draw() on a drawn map region without a data region assigned!")
        }

        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                region!!.localTileIndexToWorldTilePosition(i, j, tempCoords)
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
        val edgeMap : HashMap<TilePosition, HashSet<ActorTileEdge>> = HashMap()
    }
}