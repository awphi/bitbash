package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.data.tile.TileHandler

object MarbleFloorTile : TileHandler("marble_floor") {
    override val hasBody: Boolean
        get() = false
}