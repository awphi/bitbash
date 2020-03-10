package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.Input
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene

object GrassTileHandler : TileHandler("grass") {
    override val drawPriority: Int
        get() = 10
}