package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.Input
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene

object GrassTileHandler : TileHandler("grass") {
    override val edgePriority: Int
        get() = 1

    override fun mouseClicked(actor: ActorTile, button: Int, tilePosition: TilePosition, x: Float, y: Float) {
        super.mouseClicked(actor, button, tilePosition, x, y)

        if(button == Input.Buttons.LEFT) {
            BitbashPlayScene.map.setTileAt(tilePosition, StoneBrickTileHandler)
        } else if(button == Input.Buttons.RIGHT) {
            BitbashPlayScene.map.addWidgetAt(tilePosition, ActorWidgetLamp())
        }
    }
}