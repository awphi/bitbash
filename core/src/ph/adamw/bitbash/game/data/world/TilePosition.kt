package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.NumberUtils
import ph.adamw.bitbash.game.actor.ActorTile

import java.io.Serializable
import kotlin.math.floor

/**
 * Tile-centric coordinate system, includes method to convert to and from world (real) position
 * i.e. [0, 0] and [0, 1] are two adjacent tile positions.
 */
class TilePosition(var x: Float, var y: Float) : Serializable {
    override fun toString(): String {
        return javaClass.simpleName + "[$x, $y]"
    }

    fun toWorldPosition(): Vector2 {
        return Vector2(getWorldX(), getWorldY())
    }

    fun getWorldX() : Float {
        return x * ActorTile.SIZE
    }

    fun getWorldY() : Float {
        return y * ActorTile.SIZE
    }

    fun add(x: Float, y: Float): TilePosition {
        this.x += x
        this.y += y
        return this
    }

    fun sub(x: Float, y: Float): TilePosition {
        this.x -= x
        this.y -= y
        return this
    }

    fun set(x: Float, y: Float) : TilePosition {
        this.x = x
        this.y = y
        return this
    }

    fun copy(): TilePosition {
        return TilePosition(x, y)
    }

    fun floor() : TilePosition {
        this.x = floor(x)
        this.y = floor(y)
        return this
    }

    fun set(tp: TilePosition) : TilePosition {
        this.x = tp.x
        this.y = tp.y
        return this
    }

    override fun equals(other: Any?): Boolean {
        if(other is TilePosition) {
            return other.x == x && other.y == y
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + NumberUtils.floatToIntBits(x)
        result = prime * result + NumberUtils.floatToIntBits(y)
        return result
    }

    companion object {
        fun fromWorldX(x: Float) : Float {
            return x / ActorTile.SIZE
        }

        fun fromWorldY(y: Float) : Float {
            return y / ActorTile.SIZE
        }

        fun fromWorldPosition(x: Float, y: Float, np: TilePosition): TilePosition {
            return np.set(fromWorldX(x), fromWorldY(y))
        }

        fun fromWorldPosition(x: Float, y: Float): TilePosition {
            return TilePosition(fromWorldX(x), fromWorldY(y))
        }

        fun fromWorldPosition(vector2: Vector2): TilePosition {
            return fromWorldPosition(vector2.x, vector2.y)
        }
    }
}
