package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object StoneBrickTileHandler : TileHandler("stone_brick") {
    override val edgePriority: Int
        get() = 1
}