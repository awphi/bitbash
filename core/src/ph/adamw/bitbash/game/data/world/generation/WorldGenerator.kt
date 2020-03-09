package ph.adamw.bitbash.game.data.world.generation

import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.SandstoneTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.WaterTileHandler
import ph.adamw.bitbash.util.SimplexNoise
import java.lang.Math.pow
import kotlin.math.pow
import kotlin.math.roundToInt

class WorldGenerator(private val seed : Long) {
    private val moistureNoise = SimplexNoise(seed + 16)
    private val heightNoise = SimplexNoise(seed)

    private val slices = arrayOf(
            WorldSlice(WaterTileHandler, 0, 55),
            WorldSlice(SandstoneTileHandler, 56, 65),
            WorldSlice(DirtTileHandler, 66, 75),
            WorldSlice(GrassTileHandler, 76, 100)
    )

    private val heightMap : HashMap<Int, TileHandler> = HashMap()

    init {
        for(i in slices) {
            for(j in i.lowerBound..i.upperBound) {
                heightMap[j] = i.tileHandler
            }
        }
    }

    fun getTileFor(np: Vector2) : TileHandler {
        // Octaves, 1 + 0.5 + 0.25 = 1.75
        val e: Double = 1 * heightNoise.noise(np.x / HEIGHT_SIZE, np.y / HEIGHT_SIZE) +
                        0.5 * heightNoise.noise(2 * (np.x / HEIGHT_SIZE), 2 * (np.y / HEIGHT_SIZE)) +
                        0.25 * heightNoise.noise(4 * (np.x / HEIGHT_SIZE), 4 * (np.y / HEIGHT_SIZE))

        //TODO use moisture
        //val moistureVal = moistureNoise.noise(np.x / MOISTURE_SIZE, np.y / MOISTURE_SIZE)

        // e / sum of octaves then bound between 0 and 1
        val normalized : Double = ((e / 1.75) + 1f) / 2f
        val height : Int = (normalized.pow(HEIGHT_SHIFT) * 100).roundToInt()
        return heightMap[height]!!
    }

    companion object {
        private const val HEIGHT_SHIFT : Double = 0.53
        private const val MOISTURE_SIZE : Double = 120.0
        private const val HEIGHT_SIZE : Double = 80.0
    }
}