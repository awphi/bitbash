package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.ActorHandler
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.handlers.PavementTileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene
import java.io.Serializable

abstract class TileHandler(name : String) : ActorHandler<ActorTile>(name) {
    override fun getTextureName() : String {
        return name
    }

    @Transient
    open val color : Color = Color(1f, 1f, 1f, 1f)

    abstract val edgePriority : Int

    override val physicsData: PhysicsData?
        get() = null

    override fun mouseClicked(actor: ActorTile, button: Int, tilePosition: TilePosition, x: Float, y: Float) {
        super.mouseClicked(actor, button, tilePosition, x, y)

        if(button == Input.Buttons.LEFT) {
            BitbashPlayScene.map.setTileAt(tilePosition, PavementTileHandler)
        } else if(button == Input.Buttons.RIGHT) {
            BitbashPlayScene.map.addWidgetAt(tilePosition, ActorWidgetLamp())
        }
    }
}