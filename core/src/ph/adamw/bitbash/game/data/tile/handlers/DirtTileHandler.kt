package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object DirtTileHandler : TileHandler("dirt") {
    override val edgePriority: Int
        get() = 20
}