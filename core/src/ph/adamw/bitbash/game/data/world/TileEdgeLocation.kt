package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.math.Vector2

enum class TileEdgeLocation(val x : Float, val y : Float, val components : Set<Direction>?) {
    UPLEFT(Vector2(-1f, 1f), setOf(Direction.UP, Direction.LEFT)),
    UP(Vector2(0f, 1f), null),
    UPRIGHT(Vector2(1f, 1f), setOf(Direction.UP, Direction.RIGHT)),
    LEFT(Vector2(-1f, 0f), null),
    RIGHT(Vector2(1f, 0f), null),
    DOWNLEFT(Vector2(-1f, -1f), setOf(Direction.DOWN, Direction.LEFT)),
    DOWN(Vector2(0f, -1f), null),
    DOWNRIGHT(Vector2(1f, -1f), setOf(Direction.DOWN, Direction.RIGHT));

    constructor(vector2: Vector2, components : Set<Direction>?) : this(vector2.x, vector2.y, components)

    companion object {
        val COMPOSITES = arrayOf(UPLEFT, UPRIGHT, DOWNRIGHT, DOWNLEFT)

        fun from(direction: Direction): TileEdgeLocation {
            return when (direction) {
                Direction.UP -> UP
                Direction.DOWN -> DOWN
                Direction.LEFT -> LEFT
                Direction.RIGHT -> RIGHT
            }
        }
    }
}