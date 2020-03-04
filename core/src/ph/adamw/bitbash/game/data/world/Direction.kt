package ph.adamw.bitbash.game.data.world

enum class Direction(val x : Float, val y : Float) {
    UP(0f, 1f),
    DOWN(0f, -1f),
    LEFT(-1f, 0f),
    RIGHT(1f, 0f);
}