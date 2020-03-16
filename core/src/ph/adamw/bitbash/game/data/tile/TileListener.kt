package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ph.adamw.bitbash.game.actor.ActorTile

// TODO replace with an event system
class TileListener(private val actor: ActorTile) : ClickListener(-1) {
    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        actor.handler.mouseClicked(actor, button, actor.readOnlyTilePosition, x, y)
    }
}