package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.entity.widget.WidgetWrapper
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.game.data.world.TilePosition
import java.rmi.UnexpectedException
import java.util.*

class ActorGroupMapRegion : Pool.Poolable {
    private val drawnTiles : Array<Array<ActorTile>> = Array<Array<ActorTile>>(MapRegion.REGION_SIZE) {
        Array<ActorTile>(MapRegion.REGION_SIZE) {
            ActorTile()
        }
    }

    private val drawnWidgets = HashMap<TilePosition, ActorWidget>()

    var region : MapRegion? = null

    override fun reset() {
        for(i in drawnWidgets.keys) {
            drawnWidgets[i]!!.remove()
        }

        drawnWidgets.clear()

        for (i in drawnTiles.indices) {
            for (j in drawnTiles[i].indices) {
                drawnTiles[i][j].isVisible = false
            }
        }
    }

    fun loadToStage(tileGroup: Group, widgetGroup: Group) {
        if(region == null) {
            throw UnexpectedException("Attempted to call draw() on a drawn map region without a data region assigned!")
        }

        val np = TilePosition(0f, 0f)
        for (i in region!!.tiles.indices) {
            for (j in region!!.tiles[i].indices) {
                np.set(i + region!!.x * MapRegion.REGION_SIZE.toFloat(), j + region!!.y * MapRegion.REGION_SIZE.toFloat())
                val tile : TileHandler? = region!!.tiles[i][j]
                val widget : WidgetWrapper? = region!!.widgets[np]

                tile?.let {
                    drawnTiles[i][j].set(tile, np)
                    tileGroup.addActor(drawnTiles[i][j])
                    drawnTiles[i][j].isVisible = true
                }

                widget?.let {
                    drawWidget(widget, np.copy(), widgetGroup)
                }
            }
        }
    }

    fun drawWidget(widget: WidgetWrapper, np: TilePosition, g: Group) {
        val ac = ActorWidget(widget, np)
        drawnWidgets[np] = ac
        g.addActor(ac)
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

    fun undrawWidget(np: TilePosition) : Boolean {
        drawnWidgets[np]?.let {
            it.remove()
            drawnWidgets.remove(it.initialPos)
            it.deleteBody()

            return true
        }

        return false
    }

    companion object {
        val POOL : Pool<ActorGroupMapRegion> = Pools.get(ActorGroupMapRegion::class.java)!!
    }
}