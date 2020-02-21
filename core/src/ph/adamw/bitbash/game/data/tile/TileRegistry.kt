package ph.adamw.bitbash.game.data.tile

import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.MarbleFloorTile
import ph.adamw.bitbash.game.data.tile.handlers.StoneBrickTileHandler

object TileRegistry {
    val REGISTRY : com.badlogic.gdx.utils.Array<TileHandler> = com.badlogic.gdx.utils.Array.with(
            DirtTileHandler,
            GrassTileHandler,
            StoneBrickTileHandler,
            MarbleFloorTile
    )
}