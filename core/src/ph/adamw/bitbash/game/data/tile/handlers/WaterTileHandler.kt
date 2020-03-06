package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.graphics.Color
import ph.adamw.bitbash.game.data.tile.TileHandler

object WaterTileHandler : TileHandler("water") {
    override val drawPriority: Int
        get() = 0

    override val color: Color
        get() = Color(1f, 1f, 1f, 0.7f)

    override val edgePriority: Int
        get() = 0
}