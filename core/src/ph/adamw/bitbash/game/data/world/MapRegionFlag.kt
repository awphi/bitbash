package ph.adamw.bitbash.game.data.world

enum class MapRegionFlag {
    NEEDS_EDGE,
    NEEDS_MINIMAP;

    companion object {
        val VALUES : Array<MapRegionFlag> = values()
    }
}