package ph.adamw.bitbash.game.data.tile.handlers

import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene

object DirtTileHandler : TileHandler("dirt") {
    override val drawPriority: Int
        get() = 20
}