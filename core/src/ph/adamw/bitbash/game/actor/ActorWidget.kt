package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.game.data.world.TilePosition
import kotlin.math.*

//TODO pool these if performance gets bad
abstract class ActorWidget<Self : ActorWidget<Self>>() : ActorEntity<Self>() {
    val initialPos: TilePosition = TilePosition(0f, 0f)

    fun at(position: TilePosition) : ActorWidget<Self> {
        initialPos.set(position)
        return this
    }

    var offsetX = 0f
        set(value) {
            field = value
            setPositionWithBody(calculateX(), calculateY())
        }

    var offsetY = 0f
        set(value) {
            field = value
            setPositionWithBody(calculateX(), calculateY())
        }

    private fun calculateX() : Float {
        var c = abs(ActorTile.SIZE - width)
        if(width > ActorTile.SIZE) {
            c = ActorTile.SIZE - c
        }

        return initialPos.getWorldX() + c / 2f + offsetX
    }

    private fun calculateY() : Float {
        var c = abs(ActorTile.SIZE - height)
        if(height > ActorTile.SIZE) {
            c = ActorTile.SIZE - c
        }

        return initialPos.getWorldY() + c / 2f + offsetY
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

        offsetX = ox
        offsetY = oy

        setPositionWithBody(calculateX(), calculateY())
    }
}