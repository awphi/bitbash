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

class MapRegion(val coords: Vector2) : Serializable {
    val widgets = HashMap<TilePosition, ActorWidget>()

    val x : Float
        get() = coords.x

    val y : Float
        get() = coords.y

    @Transient
    val flags : HashSet<MapRegionFlag> = HashSet()

    // For use of serializer
    constructor() : this(DUMMY_COORDS)

    init {
        markDirty()
    }

    fun markDirty(vararg flag: MapRegionFlag) {
        flags.addAll(flag)
    }

    fun markDirty() {
        flags.clear()
        flags.addAll(MapRegionFlag.VALUES)
    }

    fun markUndirty(flag: MapRegionFlag) {
        flags.remove(flag)
    }

    fun isDirty(flag: MapRegionFlag) : Boolean {
        return flags.contains(flag)
    }

    val tiles = Array<Array<TileHandler>>(REGION_SIZE) {
        Array<TileHandler>(REGION_SIZE) {
            StoneBrickTileHandler
        }
    }

    fun setWidgetAt(np: TilePosition, widget: ActorWidget) {
        widgets[np] = widget
        BitbashPlayScene.addDrawnWidget(this.coords, widget)
    }

    fun localTileIndexToWorldTilePosition(i : Int, j : Int, np: Vector2) : Vector2 {
        np.set(i + x * REGION_SIZE.toFloat(), j + y * REGION_SIZE.toFloat())
        return np
    }

    fun localTileIndexToWorldTilePosition(i : Int, j : Int, np: TilePosition) : TilePosition {
        np.set(i + x * REGION_SIZE.toFloat(), j + y * REGION_SIZE.toFloat())
        return np
    }

    fun getWidgetAt(np: TilePosition): ActorWidget? {
        return widgets[np]
    }

    fun deleteWidgetAt(np: TilePosition, actorWidget: ActorWidget) : Boolean {
        val y = widgets.remove(np)
        BitbashPlayScene.removeDrawnWidget(coords, actorWidget)
        return y != null
    }

    fun getTile(np : TilePosition) : TileHandler {
        return tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)]
    }

    fun setTileAt(np: TilePosition, tile: TileHandler) {
        tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)] = tile
        markDirty()
        BitbashPlayScene.updateDrawnTile(this, np, tile)
    }

    fun unload(folder: FileHandle) {
        val handle = folder.child(Map.getRegionFileName(x.toInt(), y.toInt()))
        val bytes = BitbashApplication.IO.asByteArray(this)
        handle.writeBytes(bytes, false)
    }

    override fun toString(): String {
        return javaClass.simpleName + "[$x,$y]"
    }

    companion object {
        const val REGION_SIZE = 12
        private val DUMMY_COORDS = Vector2(0f, 0f)

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