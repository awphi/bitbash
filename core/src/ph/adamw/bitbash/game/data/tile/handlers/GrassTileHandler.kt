package ph.adamw.bitbash.game.data.tile.handlers

import com.badlogic.gdx.Input
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene

object GrassTileHandler : TileHandler("grass") {
    override val hasBody: Boolean
        get() = false

    override fun mouseClicked(actor: ActorTile, button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {
        super.mouseClicked(actor, button, tilePosition, x, y, scene)

        if(button == Input.Buttons.LEFT) {
            scene.map.setTileAt(tilePosition, StoneBrickTileHandler, scene)
        } else if(button == Input.Buttons.RIGHT) {
            scene.map.setWidgetAt(tilePosition, ActorWidgetLamp(), scene)
        }
    }
}