package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.graphics.Color
import ph.adamw.bitbash.game.data.tile.TileHandler

object PavementTileHandler : TileHandler("pavement") {
    override val color: Color
        get() = Color(1f, 1f, 1f, 0.1f)

    override val edgePriority: Int
        get() = 10
}