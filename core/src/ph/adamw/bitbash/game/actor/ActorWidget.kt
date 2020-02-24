package ph.adamw.bitbash.game.actor

import ph.adamw.bitbash.game.data.entity.widget.WidgetHandler
import ph.adamw.bitbash.game.data.entity.widget.WidgetWrapper
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene
import kotlin.math.*

//TODO pool these if performance gets bad
class ActorWidget(var wrapper: WidgetWrapper, val initialPos: TilePosition) : ActorEntity<ActorWidget, WidgetHandler>(wrapper.handler) {
    init {
        setPositionWithBody(calculateX(), calculateY())
    }

    private fun calculateX() : Float {
        var c = abs(ActorTile.SIZE - width)
        if(width > ActorTile.SIZE) {
            c = ActorTile.SIZE - c
        }

        return initialPos.getWorldX() + c / 2f + wrapper.offsetX
    }

    private fun calculateY() : Float {
        var c = abs(ActorTile.SIZE - height)
        if(height > ActorTile.SIZE) {
            c = ActorTile.SIZE - c
        }

        return initialPos.getWorldY() + c / 2f + wrapper.offsetY
    }

    private fun calculateOffsetX(cx: Float) : Float {
        val a =  cx - (initialPos.getWorldX() + ActorTile.SIZE / 2f)
        return min(max(a, -ActorTile.SIZE / 2f), ActorTile.SIZE / 2f)
    }

    private fun calculateOffsetY(cy: Float) : Float {
        val a = cy - (initialPos.getWorldY() + ActorTile.SIZE / 2f)
        return min(max(a, -ActorTile.SIZE / 2f), ActorTile.SIZE / 2f)
    }

    fun setWidgetPosition(x: Float, y: Float, snap: Boolean) {
        var ox = calculateOffsetX(x)
        var oy = calculateOffsetY(y)

        if(snap) {
            ox = 4f * round(ox / 4f)
            oy = 4f * round(oy / 4f)
        }

        wrapper.offsetX = ox
        wrapper.offsetY = oy

        setPositionWithBody(calculateX(), calculateY())
    }

    override fun actEntity(actorEntity: ActorWidget, delta: Float, tilePosition: TilePosition) {
        handler.act(this, delta, tilePosition)
    }

    override fun mouseClicked(button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {
        handler.mouseClicked(this, button, tilePosition, x, y, scene)
    }

    override fun mouseDragged(button: Int, tilePosition: TilePosition, x: Float, y: Float, scene: BitbashCoreScene) {
        handler.mouseDragged(this, button, tilePosition, x, y, scene)
    }
}