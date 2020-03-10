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
import ph.adamw.bitbash.game.data.world.generation.WorldGenerator
import java.io.Serializable
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.HashSet

class Map(val seed: Long) : Serializable {
    val discoveredRegions : HashSet<Vector2> = HashSet()

    @Transient
    private val regionMap = HashMap<Vector2, MapRegion>()

    @Transient
    private var regionsFolder : FileHandle? = null

    @delegate:Transient
    private val worldGenerator : WorldGenerator by lazy {
        WorldGenerator(seed)
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
                    region.tiles[i][j] = worldGenerator.getTileFor(tempCoords)
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
        saveRegion(vec)
        regionMap.remove(vec)
        //TODO Mark for GC, pool idk?
    }

    //TODO thread?
    fun saveRegion(vec: Vector2) {
        Gdx.app.log("MAP", "Saving region: $vec")
        getOrLoadRegion(vec)?.save(regionsFolder!!)
    }

    //TODO thread?
    private fun loadRegionInternal(vec: Vector2) {
        Gdx.app.log("MAP","Loading region: $vec")
        val y = regionsFolder!!.child(getRegionFileName(vec.x.toInt(), vec.y.toInt()))
        val rg : MapRegion = BitbashApplication.IO.asObject(y.readBytes()) as MapRegion
        regionMap[vec] = rg
    }

    fun save() {
        val it = regionMap.keys.iterator()
        while(it.hasNext()) {
            saveRegion(it.next())
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