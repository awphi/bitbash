package ph.adamw.bitbash.game.data.tile

import ph.adamw.bitbash.game.data.tile.handlers.*

object TileRegistry {
    fun get(name: String) : TileHandler? {
        return registry[name]
    }

    private val registry = mapOf(
            Pair(DirtTileHandler.name, DirtTileHandler),
            Pair(GrassTileHandler.name, GrassTileHandler),
            Pair(SandstoneTileHandler.name, SandstoneTileHandler),
            Pair(WaterTileHandler.name, WaterTileHandler),
            Pair(StoneBrickTileHandler.name, StoneBrickTileHandler),
            Pair(PavementTileHandler.name, PavementTileHandler)
    )
}