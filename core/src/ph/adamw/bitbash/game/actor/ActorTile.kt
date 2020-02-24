package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene

class ActorTile : ActorGameObject<TileHandler>(GrassTileHandler) {
    fun set(handler: TileHandler, tilePosition: TilePosition) {
        setPosition(tilePosition.getWorldX(), tilePosition.getWorldY())
        setTexture(handler.getTexturePath())

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

    override fun mouseClicked(button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {
        handler.mouseClicked(this, button, tilePosition, x, y, scene)
    }

    companion object {
        const val SIZE = 32f
    }
}