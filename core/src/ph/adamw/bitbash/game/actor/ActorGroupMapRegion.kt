package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashInfiniteScene
import ph.adamw.bitbash.scene.layer.Layer
import java.rmi.UnexpectedException
import java.util.*
import kotlin.collections.HashSet

class ActorGroupMapRegion : Pool.Poolable {
    private val drawnTiles : Array<Array<ActorTile>> = Array<Array<ActorTile>>(MapRegion.REGION_SIZE) {
        Array<ActorTile>(MapRegion.REGION_SIZE) {
            ActorTile()
        }
    }

    var region : MapRegion? = null

    override fun reset() {
        for(i in region!!.widgets.keys) {
            for(j in region!!.widgets[i]!!) {
                undrawWidget(j)
            }
        }

        for (i in drawnTiles.indices) {
            for (j in drawnTiles[i].indices) {
                drawnTiles[i][j].isVisible = false
            }
        }
    }

    fun loadToStage(tileGroup: Layer, widgetGroup: Layer) {
        if(region == null) {
            throw UnexpectedException("Attempted to call draw() on a drawn map region without a data region assigned!")
        }

        val np = TilePosition(0f, 0f)
        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                np.set(i + region!!.x * MapRegion.REGION_SIZE.toFloat(), j + region!!.y * MapRegion.REGION_SIZE.toFloat())
                val tile : TileHandler? = region!!.tiles[i][j]
                val widget = region!!.widgets[np]

                tile?.let {
                    drawnTiles[i][j].set(tile, np)
                    tileGroup.addActor(drawnTiles[i][j])
                    drawnTiles[i][j].isVisible = true
                }

                widget?.let {
                    for(wid in it) {
                        drawWidget(wid, widgetGroup)
                    }
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

    fun undrawWidget(actorWidget: ActorWidget) : Boolean {
        region!!.widgets[actorWidget.tilePosition]?.let {
            val y = it.remove(actorWidget)
            actorWidget.deleteBody()
            actorWidget.remove()

            return y
        }

        return false
    }

    companion object {
        val POOL : Pool<ActorGroupMapRegion> = Pools.get(ActorGroupMapRegion::class.java)!!
    }
}