package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object WaterTileHandler : TileHandler("water") {
    override val edgePriority: Int
        get() = -1
}