package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.StoneBrickTileHandler
import ph.adamw.bitbash.scene.BitbashPlayScene
import java.io.Serializable
import kotlin.math.ceil

class MapRegion(val x: Int, val y: Int) : Serializable {
    val coords : Vector2 = Vector2(x.toFloat(), y.toFloat())
    val widgets = HashMap<TilePosition, HashSet<ActorWidget>>()

    @Transient
    var isDirty : Boolean = false

    constructor() : this(0, 0)

    val tiles = Array<Array<TileHandler>>(REGION_SIZE) {
        Array<TileHandler>(REGION_SIZE) {
            StoneBrickTileHandler
        }
    }

    fun addWidgetAt(np: TilePosition, widget: ActorWidget) : Boolean {
        if(!widgets.containsKey(np)) {
            widgets[np] = HashSet()
        }

        val added = widgets[np]!!.add(widget)

        if(added) {
            BitbashPlayScene.addDrawnWidget(this.coords, widget)
        }

        return added
    }

    fun localTileIndexToWorldTilePosition(i : Int, j : Int) : TilePosition {
        return localTileIndexToWorldTilePosition(i, j, TilePosition(0f, 0f))
    }

    fun localTileIndexToWorldTilePosition(i : Int, j : Int, np: TilePosition) : TilePosition {
        np.set(i + x * REGION_SIZE.toFloat(), j + y * REGION_SIZE.toFloat())
        return np
    }

    fun getWidgetsAt(np: TilePosition): HashSet<ActorWidget>? {
        return widgets[np]
    }

    fun deleteWidgetAt(np: TilePosition, actorWidget: ActorWidget) : Boolean {
        if(widgets.containsKey(np)) {
            return false
        }

        val y = widgets[np]!!.remove(actorWidget)
        BitbashPlayScene.removeDrawnWidget(coords, actorWidget)
        return y
    }

    fun getTile(np : TilePosition) : TileHandler {
        return tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)]
    }

    fun setTileAt(np: TilePosition, tile: TileHandler) {
        tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)] = tile
        isDirty = true
        BitbashPlayScene.updateDrawnTile(this, np, tile)
    }

    fun unload(folder: FileHandle) {
        val handle = folder.child(Map.getRegionFileName(x, y))
        val bytes = BitbashApplication.IO.asByteArray(this)
        handle.writeBytes(bytes, false)
    }

    override fun toString(): String {
        return javaClass.simpleName + "[$x,$y]"
    }

    companion object {
        const val REGION_SIZE = 12
        const val LIMIT = 1000000000

        fun resolveRegionBounds(regionCoords: Vector2): Rectangle {
            return Rectangle(regionCoords.x * REGION_SIZE.toFloat() * ActorTile.SIZE, regionCoords.y * REGION_SIZE.toFloat() * ActorTile.SIZE, REGION_SIZE * ActorTile.SIZE, REGION_SIZE * ActorTile.SIZE)
        }

        fun resolveRegionCoords(tilePosition: TilePosition): Vector2 {
            val currentX = ceil((tilePosition.x / REGION_SIZE.toFloat() - 1f).toDouble()).toInt().toFloat()
            val currentY = ceil((tilePosition.y / REGION_SIZE.toFloat() - 1f).toDouble()).toInt().toFloat()
            return Vector2(currentX, currentY)
        }
    }
}