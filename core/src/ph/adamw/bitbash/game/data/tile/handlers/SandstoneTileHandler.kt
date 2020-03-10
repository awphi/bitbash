package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object SandstoneTileHandler : TileHandler("sandstone") {
    override val drawPriority: Int
        get() = 30
}