package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.TileListener
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.MultiLayer

class ActorTile : ActorGameObject() {
    var handler : TileHandler = GrassTileHandler

    override val actorName: String
        get() = "tile_empty"

    override val drawPriority: Int
        get() = handler.drawPriority

    fun set(handler: TileHandler, tileLayer: MultiLayer, tilePosition: TilePosition) {
        clearListeners()
        setPosition(tilePosition.getWorldX(), tilePosition.getWorldY())
        setTexture(handler.getTextureName())

        color.set(handler.color)

        deleteBody()

        this.handler = handler

        if(handler.hasBody) {
            buildBody()
        }

        name = "tile_" + handler.name

        addListener(TileListener(this))
        tileLayer.addOrGetGroup(drawPriority).addActor(this)
        isVisible = true
    }



    override val physicsData: PhysicsData?
        get() {
            if(handler.hasBody) {
                return handler.physicsData
            }

            return null
        }

    companion object {
        const val SIZE = 32f
    }
}