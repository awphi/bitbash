package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.Input
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene
import ph.adamw.bitbash.scene.BitbashInfiniteScene

object GrassTileHandler : TileHandler("grass") {
    override fun mouseClicked(actor: ActorTile, button: Int, tilePosition: TilePosition, x: Float, y: Float) {
        super.mouseClicked(actor, button, tilePosition, x, y)

        if(button == Input.Buttons.LEFT) {
            BitbashInfiniteScene.map.setTileAt(tilePosition, StoneBrickTileHandler)
        } else if(button == Input.Buttons.RIGHT) {
            BitbashInfiniteScene.map.addWidgetAt(tilePosition, ActorWidgetLamp())
        }
    }
}