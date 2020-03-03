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
import ph.adamw.bitbash.game.data.tile.handlers.DirtTileHandler
import ph.adamw.bitbash.game.data.tile.handlers.GrassTileHandler
import ph.adamw.bitbash.util.OpenSimplexNoise
import java.io.Serializable
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Map(val seed: Long) : Serializable {
    private val featureSize : Double = 80.0

    @Transient
    private val regionMap = HashMap<Vector2, MapRegion>()

    @Transient
    private var regionsFolder : FileHandle? = null

    @Transient
    private val heightNoise : OpenSimplexNoise = OpenSimplexNoise(seed)

    @Transient
    private val tempCoords : TilePosition = TilePosition(0f, 0f)

    constructor(st: String) : this(st.hashCode().toLong())

    constructor() : this(ThreadLocalRandom.current().nextLong())

    fun setMapState(mapState: MapState) {
        regionsFolder = mapState.handle.child("regions")
        regionsFolder!!.mkdirs()
    }

    fun generateRegionAt(vec2: Vector2) {
        Gdx.app.log("MAP","Generating region: $vec2")

        val region = MapRegion(vec2.x.toInt(), vec2.y.toInt())

        for (i in region.tiles.indices) {
            for (j in region.tiles[i].indices) {
                region.localTileIndexToWorldTilePosition(i, j, tempCoords)
                val noiseVal = heightNoise.eval(tempCoords.x / featureSize, tempCoords.y / featureSize, 0.0)

                //TODO proper world generation
                if(noiseVal > 0) {
                    region.tiles[i][j] = GrassTileHandler
                } else if(noiseVal <= 0) {
                    region.tiles[i][j] = DirtTileHandler
                }
            }
        }

        regionMap[Vector2(vec2.x, vec2.y)] = region
    }

    fun getRegionAt(np: TilePosition) : MapRegion? {
        return getRegion(getRegionCoordsAt(np))
    }

    fun getRegion(vec: Vector2): MapRegion? {
        val v = Vector2(vec.x, vec.y)
        if(regionMap[v] == null && regionsFolder!!.child(getRegionFileName(vec.x.toInt(), vec.y.toInt())).exists()) {
            loadRegion(vec)
        }

        return regionMap[v]
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

    private fun unloadRegionInternal(vec: Vector2) {
        Gdx.app.log("MAP", "Unloading region: $vec")
        getRegion(vec)?.unload(regionsFolder!!)
    }

    fun loadRegion(vec: Vector2) {
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
        getRegionAt(tilePosition)?.addWidgetAt(tilePosition, w)
    }

    fun getWidgetsAt(tilePosition: TilePosition): HashSet<ActorWidget>? {
        return getRegionAt(tilePosition)?.getWidgetsAt(tilePosition)
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
        fun getRegionFileName(x: Int, y: Int) : String {
            return "rg${x}_${y}.bin"
        }

        const val LIMIT : Int = (MapRegion.REGION_SIZE / 10) * MapRegion.LIMIT
    }
}