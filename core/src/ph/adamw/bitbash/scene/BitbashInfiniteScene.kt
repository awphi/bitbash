package ph.adamw.bitbash.scene

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.game.data.world.TilePosition
import kotlin.math.abs

object BitbashInfiniteScene : BitbashCoreScene() {
    private const val BUILD_DISTANCE = 1000f
    private val buildLimitRect = Rectangle()

    override fun pause() {
        mapState!!.save()
    }

    private fun setBuildRectangle() {
        buildLimitRect.set(mapState!!.player.x - BUILD_DISTANCE, mapState!!.player.y - BUILD_DISTANCE, BUILD_DISTANCE * 2, BUILD_DISTANCE * 2)
    }

    override fun unloadRegion(vec: Vector2) {
        mapState!!.map.unloadRegion(vec)
    }

    override fun updateActiveRegions() {
        super.updateActiveRegions()

        // Generates new regions
        for (vec in activeRegionCoords) {
            if (mapState!!.map.getRegion(vec) == null && abs(vec.x) <= MapRegion.LIMIT && abs(vec.y) <= MapRegion.LIMIT) {
                mapState!!.map.generateRegionAt(vec)
            }
        }
    }

    override fun refreshActiveRegions(regions: MutableSet<Vector2>) {
        regions.clear()
        setBuildRectangle()

        // get world pos of the top left corner of the renderRect
        val regionCoords = MapRegion.resolveRegionCoords(TilePosition.fromWorldPosition(Vector2(buildLimitRect.getX(), buildLimitRect.getY() + buildLimitRect.height)))
        val xCache = regionCoords.x

        var dropOff = 0

        do {
            val regionRect = MapRegion.resolveRegionBounds(regionCoords)
            // Move it along one region to the right
            regionCoords.x++

            if (regionRect.overlaps(buildLimitRect)) {
                regions.add(Vector2(regionCoords))
                dropOff = 0
            } else {
                // One over too many to the right so we switch back to column 0 and the next row
                regionCoords.x = xCache
                regionCoords.y--

                // Increase the drop off i.e. if the region rectangle has not overlapped the camera twice in a row, it has been covered so this loop with terminate
                dropOff++
            }

        } while (dropOff != 2)
    }
}
