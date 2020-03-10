package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.graphics.Color
import ph.adamw.bitbash.game.data.tile.TileHandler

object PavementTileHandler : TileHandler("pavement") {
    override val drawPriority: Int
        get() = 9
}