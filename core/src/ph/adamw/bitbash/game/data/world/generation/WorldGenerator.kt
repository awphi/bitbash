package ph.adamw.bitbash.game.data.world.generation

import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.SandstoneTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.WaterTileHandler
import kotlin.math.roundToInt

object WorldGenerator {
    val slices = arrayOf(
            WorldSlice(WaterTileHandler, 0, 3),
            WorldSlice(SandstoneTileHandler, 4, 5),
            WorldSlice(DirtTileHandler, 6, 6),
            WorldSlice(GrassTileHandler, 7, 10)
    )

    val map : HashMap<Int, TileHandler> = HashMap()

    init {
        for(i in slices) {
            for(j in i.lowerBound..i.upperBound) {
                map[j] = i.tileHandler
            }
        }
    }


    fun getTileFor(heightNoise : Double, temperatureNoise : Double) : TileHandler {
        val normalized = (heightNoise + 1f) / 2f
        val height : Int = (normalized * 10f).roundToInt()
        //TODO take into account temperature
        return map[height]!!
    }
}