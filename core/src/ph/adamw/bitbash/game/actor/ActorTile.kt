package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.graphics.Color
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.OrderedDrawLayer

class ActorTile : ActorGameObject() {
    var handler : TileHandler = GrassTileHandler

    override val actorName: String
        get() = "tile_empty"

    override val drawPriority: Int
        get() = handler.edgePriority

    fun set(handler: TileHandler, tilePosition: TilePosition) {
        setPosition(tilePosition.getWorldX(), tilePosition.getWorldY())
        setTexture(handler.getTextureName())

        color.set(handler.color)

        deleteBody()

        this.handler = handler

        if(handler.hasBody) {
            buildBody()
        }

        name = "tile_" + handler.name

        isVisible = true
    }

    override val physicsData: PhysicsData?
        get() {
            if(handler.hasBody) {
                return handler.physicsData
            }

            return null
        }

    override fun mouseClicked(button: Int, tilePosition: TilePosition, x: Float, y: Float) {
        handler.mouseClicked(this, button, tilePosition, x, y)
    }

    companion object {
        const val SIZE = 32f
    }
}