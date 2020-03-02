package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.math.Vector2

enum class TileEdgeLocation(val vec: Vector2, val direction: Direction) {
    UPLEFT(Vector2(-1f, 1f)),
    UP(Vector2(0f, 1f)),
    UPRIGHT(Vector2(1f, 1f)),
    LEFT(Vector2(-1f, 0f)),
    RIGHT(Vector2(1f, 0f)),
    DOWNLEFT(Vector2(-1f, -1f)),
    DOWN(Vector2(0f, -1f)),
    DOWNRIGHT(Vector2(1f, -1f));

    fun align(x : Float, y : Float) {

    }
}