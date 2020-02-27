package ph.adamw.bitbash.game.data.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.scene.BitbashCoreScene
import ph.adamw.bitbash.util.SimplexNoise
import java.io.Serializable
import java.util.*
import kotlin.random.Random

class Map(private var seed: Long) : Serializable {
    @Transient
    private val regionMap = HashMap<Vector2, MapRegion>()

    @Transient
    private var regionsFolder : FileHandle? = null

    @delegate:Transient
    private val random by lazy {
        Random(seed)
    }

    @delegate:Transient
    private val biomeNoise by lazy {
        SimplexNoise(random)
    }

    constructor() : this(Random.nextLong())


    fun setMapState(mapState: MapState) {
        regionsFolder = mapState.handle.child("regions")
        regionsFolder!!.mkdirs()
    }

    fun generateRegionAt(vec2: Vector2) {
        Gdx.app.log("MAP","Generating region: $vec2")
        regionMap[Vector2(vec2.x, vec2.y)] = MapRegion.generate(vec2.x.toInt(), vec2.y.toInt())
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

    fun setWidgetAt(tilePosition: TilePosition, w : ActorWidget, scene: BitbashCoreScene) {
        w.tilePosition.set(tilePosition)
        Gdx.app.log("TE", tilePosition.toString())
        getRegionAt(tilePosition)?.setWidgetAt(tilePosition, w, scene)
    }

    fun getWidgetAt(tilePosition: TilePosition, scene: BitbashCoreScene): ActorWidget? {
        return getRegionAt(tilePosition)?.getWidgetAt(tilePosition)
    }

    fun deleteWidgetAt(tilePosition: TilePosition, scene: BitbashCoreScene) {
        getRegionAt(tilePosition)?.deleteWidgetAt(tilePosition, scene)
    }

    fun setTileAt(tilePosition: TilePosition, tile: TileHandler, scene: BitbashCoreScene) {
        getRegionAt(tilePosition)?.setTileAt(tilePosition, tile, scene)
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