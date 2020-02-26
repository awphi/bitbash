package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.scene.BitbashCoreScene
import java.io.Serializable
import kotlin.math.ceil

class MapRegion(val x: Int, val y: Int) : Serializable {
    val coords : Vector2 = Vector2(x.toFloat(), y.toFloat())
    val widgets = HashMap<TilePosition, ActorWidget<*>>()

    val tiles = Array<Array<TileHandler>>(REGION_SIZE) {
        Array<TileHandler>(REGION_SIZE) {
            DirtTileHandler
        }
    }

    fun setWidgetAt(np: TilePosition, widget: ActorWidget<*>, scene: BitbashCoreScene) {
        widgets[np] = widget
        scene.addDrawnWidget(this.coords, np, widget)
    }

    fun getWidgetAt(np: TilePosition): ActorWidget<*>? {
        return widgets[np]
    }

    fun deleteWidgetAt(np: TilePosition, scene: BitbashCoreScene) : Boolean {
        if(widgets.containsKey(np)) {
            return false
        }

        if(scene.removeDrawnWidget(this.coords, np)) {
            widgets.remove(np)
            return true
        }

        return false
    }

    fun getTile(np : TilePosition) : TileHandler {
        return tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)]
    }

    fun setTileAt(np: TilePosition, tile: TileHandler, scene: BitbashCoreScene) {
        tiles[Math.floorMod(MathUtils.floor(np.x), REGION_SIZE)][Math.floorMod(MathUtils.floor(np.y), REGION_SIZE)] = tile
        scene.updateDrawnTile(this, np, tile)
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

        //TODO world gen
        fun generate(x: Int, y: Int) : MapRegion {
            val region = MapRegion(x, y)

            for (i in region.tiles.indices) {
                for (j in region.tiles[i].indices) {
                    region.tiles[i][j] = GrassTileHandler
                }
            }

            return region
        }

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