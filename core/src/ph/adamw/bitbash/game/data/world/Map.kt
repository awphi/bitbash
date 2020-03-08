@file:Suppress("CanBeParameter")

package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.WaterTileHandler
import ph.adamw.bitbash.game.data.world.generation.WorldGenerator
import ph.adamw.bitbash.util.SimplexNoise
import java.io.Serializable
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.HashSet

class Map(val seed: Long) : Serializable {
    private val heightFeatureSize : Double = 80.0
    private val temperatureFeatureSize : Double = 120.0

    val discoveredRegions : HashSet<Vector2> = HashSet()

    @Transient
    private val regionMap = HashMap<Vector2, MapRegion>()

    @Transient
    private var regionsFolder : FileHandle? = null

    @Transient
    private val tempCoords : Vector2 = Vector2()

    @delegate:Transient
    private val heightNoise : SimplexNoise by lazy {
        SimplexNoise(seed)
    }

    @delegate:Transient
    private val temperatureNoise : SimplexNoise by lazy {
        SimplexNoise(seed + 16)
    }

    constructor() : this(ThreadLocalRandom.current().nextLong())

    fun setMapState(mapState: MapState) {
        regionsFolder = mapState.handle.child("regions")
        regionsFolder!!.mkdirs()
    }

    fun generateRegionAt(vec2: Vector2) {
        val v = Vector2(vec2.x, vec2.y)
        val region = MapRegion(v)
        regionMap[v] = region
        discoveredRegions.add(v)

        threadPool.submit {
            Gdx.app.log("MAP","Generating new region: $vec2")
            val tempCoords = Vector2(0f, 0f)

            for (i in region.tiles.indices) {
                for (j in region.tiles[i].indices) {
                    region.localTileIndexToWorldTilePosition(i, j, tempCoords)
                    val heightNoiseVal = heightNoise.noise(tempCoords.x / heightFeatureSize, tempCoords.y / heightFeatureSize)
                    val temperatureNoiseVal = temperatureNoise.noise(tempCoords.x / temperatureFeatureSize, tempCoords.y / temperatureFeatureSize)

                    region.tiles[i][j] = WorldGenerator.getTileFor(heightNoiseVal, temperatureNoiseVal)
                }
            }

            region.markDirty()

            for(j in Direction.values()) {
                tempCoords.set(region.x + j.x, region.y + j.y)
                regionMap[tempCoords]?.markDirty(MapRegionFlag.NEEDS_EDGE)
            }

        }
    }

    private fun getRegionAt(np: TilePosition) : MapRegion? {
        return getOrLoadRegion(getRegionCoordsAt(np))
    }

    /**
     * Key method of the map, does what it says on the tin.
     * @return null if region couldn't be loaded or found in memory, the given MapRegion otherwise
     */
    fun getOrLoadRegion(vec: Vector2): MapRegion? {
        if(!isRegionLoaded(vec) && canRegionLoaded(vec)) {
            loadRegionInternal(vec)
        }

        return regionMap[vec]
    }

    fun canRegionLoaded(vec: Vector2) : Boolean {
        return regionsFolder!!.child(getRegionFileName(vec.x.toInt(), vec.y.toInt())).exists()
    }

    fun isRegionLoaded(vec: Vector2) : Boolean {
        return regionMap[vec] != null
    }

    fun getRegionCoordsAt(np: TilePosition): Vector2 {
        return Vector2(MathUtils.floor(np.x / MapRegion.REGION_SIZE).toFloat(), MathUtils.floor(np.y / MapRegion.REGION_SIZE).toFloat())
    }

    fun addRegion(mr: MapRegion) {
        regionMap[mr.coords] = mr
    }

    fun unloadRegion(vec: Vector2) {
        unloadRegionInternal(vec)
        regionMap.remove(vec)
    }

    //TODO thread?
    private fun unloadRegionInternal(vec: Vector2) {
        Gdx.app.log("MAP", "Unloading region: $vec")
        getOrLoadRegion(vec)?.unload(regionsFolder!!)
    }

    //TODO thread?
    private fun loadRegionInternal(vec: Vector2) {
        Gdx.app.log("MAP","Loading region: $vec")
        val y = regionsFolder!!.child(getRegionFileName(vec.x.toInt(), vec.y.toInt()))
        val rg : MapRegion = BitbashApplication.IO.asObject(y.readBytes()) as MapRegion
        regionMap[vec] = rg
    }

    fun unload() {
        val it = regionMap.keys.iterator()
        while(it.hasNext()) {
            unloadRegionInternal(it.next())
            it.remove()
        }
    }

    fun addWidgetAt(tilePosition: TilePosition, w : ActorWidget) {
        w.tilePosition.set(tilePosition)
        getRegionAt(tilePosition)?.setWidgetAt(tilePosition, w)
    }

    fun getWidgetsAt(tilePosition: TilePosition): ActorWidget? {
        return getRegionAt(tilePosition)?.getWidgetAt(tilePosition)
    }

    fun deleteWidgetAt(tilePosition: TilePosition, actorWidget: ActorWidget) {
        getRegionAt(tilePosition)?.deleteWidgetAt(tilePosition, actorWidget)
    }

    fun setTileAt(tilePosition: TilePosition, tile: TileHandler) {
        getRegionAt(tilePosition)?.setTileAt(tilePosition, tile)
    }

    fun getTileAt(np: TilePosition): TileHandler? {
        return getRegionAt(np)?.getTile(np)
    }

    companion object {
        val threadPool : ExecutorService = Executors.newCachedThreadPool()

        fun getRegionFileName(x: Int, y: Int) : String {
            return "rg${x}_${y}.bin"
        }
    }
}