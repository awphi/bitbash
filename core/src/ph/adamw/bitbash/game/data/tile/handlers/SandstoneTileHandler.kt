package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object SandstoneTileHandler : TileHandler("sandstone") {
    override val edgePriority: Int
        get() = 30
}