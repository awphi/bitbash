package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.data.world.TilePosition
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

abstract class ActorWidget : ActorEntity() {
    val tilePosition: TilePosition = TilePosition(0f, 0f)

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

    override fun added() {
        super.added()
        setPositionWithBody(calculateX(), calculateY())
    }

    private fun calculateX() : Float {
        val r = ceil(width / ActorTile.SIZE)
        val c = ActorTile.SIZE * r - width

        return tilePosition.getWorldX() + c / 2f + offsetX
    }

    private fun calculateY() : Float {
        val r = ceil(height / ActorTile.SIZE)
        val c = ActorTile.SIZE * r - height

        return tilePosition.getWorldY() + c / 2f + offsetY
    }

    private fun calculateOffsetX(cx: Float) : Float {
        val a =  cx - (tilePosition.getWorldX() + ActorTile.SIZE / 2f)
        return min(max(a, -ActorTile.SIZE / 2f), ActorTile.SIZE / 2f)
    }

    private fun calculateOffsetY(cy: Float) : Float {
        val a = cy - (tilePosition.getWorldY() + ActorTile.SIZE / 2f)
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

    override fun read(json: Json?, jsonData: JsonValue?) {
        super.read(json, jsonData)
        tilePosition.set(jsonData!!.getFloat("initialX"), jsonData.getFloat("initialY"))
    }

    override fun write(json: Json?) {
        super.write(json)
        json!!.writeValue("initialX", tilePosition.x)
        json.writeValue("initialY", tilePosition.y)
    }
}