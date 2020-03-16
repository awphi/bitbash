package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.Body
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.actor.widget.ActorWidgetLamp
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.handlers.PavementTileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashPlayScene

abstract class TileHandler(val name : String) {
    open val hasBody : Boolean
        get() = physicsData != null

    fun getTextureName() : String {
        return name
    }


    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other is TileHandler) {
            return hashCode() == other.hashCode()
        }

        return false
    }

    @Transient
    open val color : Color = Color(1f, 1f, 1f, 1f)

    abstract val drawPriority : Int

    val physicsData: PhysicsData?
        get() = null

    fun mouseClicked(actor: ActorTile, button: Int, tilePosition: TilePosition, x: Float, y: Float) {
        if(button == Input.Buttons.LEFT) {
            BitbashPlayScene.map.setTileAt(tilePosition, PavementTileHandler)
        } else if(button == Input.Buttons.RIGHT) {
            BitbashPlayScene.map.addWidgetAt(tilePosition, ActorWidgetLamp())
        }
    }
}