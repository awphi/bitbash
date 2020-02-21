package ph.adamw.bitbash.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorGroupMapRegion
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.widget.WidgetWrapper
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.util.CameraUtils
import java.util.*
import kotlin.collections.HashMap

abstract class BitbashCoreScene : Scene() {
    private var lastActorGameObjHit : ActorGameObject? = null
    private val tempCoords = Vector2(0f, 0f)

    var mapState: MapState? = null

    val map : Map
        get() = mapState!!.map

    private val drawnRegions = HashMap<Vector2, ActorGroupMapRegion>()
    protected val activeRegionCoords = HashSet<Vector2>()

    private var GAME_OBJECT_LAYER : Group? = null
    private val PLAYER_LAYER : Group = Group()
    private val WIDGET_LAYER : Group = Group()
    private val TILE_LAYER : Group = Group()

    // Highest priority first
    private val OVERLAY_LAYERS = arrayOf(
            WIDGET_LAYER,
            TILE_LAYER
    )

    override fun load() {
        GAME_OBJECT_LAYER = GameManager.getStageLayer(0)

        // Lowest priority first
        GAME_OBJECT_LAYER!!.addActor(TILE_LAYER)
        GAME_OBJECT_LAYER!!.addActor(PLAYER_LAYER)
        GAME_OBJECT_LAYER!!.addActor(WIDGET_LAYER)

        GAME_OBJECT_LAYER!!.addListener(BitbashSceneListener())

        PLAYER_LAYER.addActor(mapState!!.player)
        CameraUtils.setCameraPos(GameManager.MAIN_CAMERA, mapState!!.player.x, mapState!!.player.y)

        GameManager.rayHandler.setAmbientLight(0f, 0f, 0f, 0.8f)
        GameManager.rayHandler.setBlurNum(3)
    }

    override fun postDraw() {
        GameManager.rayHandler.setCombinedMatrix(GameManager.MAIN_CAMERA as OrthographicCamera)
        GameManager.rayHandler.updateAndRender()
        updateActiveRegions()
    }

    override fun preDraw() {
        restageMap()
        CameraUtils.setCameraPos(GameManager.MAIN_CAMERA, mapState!!.player, Gdx.graphics.deltaTime)
        applyOutlines()
    }

    fun restageMap() {
        for (vec : Vector2 in activeRegionCoords) {
            val region = mapState!!.map.getRegion(vec)
            if (region != null && !isDrawn(vec)) {
                val drawnMapRegion = ActorGroupMapRegion.POOL.obtain()
                drawnMapRegion.region = region
                drawnMapRegion.loadToStage(TILE_LAYER, WIDGET_LAYER)
                drawnRegions[vec] = drawnMapRegion
            }
        }

        val it = drawnRegions.keys.iterator()
        while(it.hasNext()) {
            val i = it.next()
            if(!activeRegionCoords.contains(i)) {
                val drawnMapRegion = drawnRegions[i]
                drawnMapRegion?.removeFromStage()
                unloadRegion(drawnMapRegion?.region!!.coords)
                it.remove()
            }
        }
    }

    open fun unloadRegion(vec: Vector2) {}

    abstract fun refreshActiveRegions(regions: MutableSet<Vector2>)

    open fun updateActiveRegions() {
        refreshActiveRegions(activeRegionCoords)
    }

    fun isDrawn(vec: Vector2) : Boolean {
        return drawnRegions.containsKey(vec)
    }

    fun updateDrawnTile(rg: MapRegion, np: TilePosition, tile: TileHandler) {
        if(isDrawn(rg.coords)) {
            drawnRegions[rg.coords]!!.drawTile(np, tile)
        }
    }

    private fun applyOutlines() {
        tempCoords.set(Gdx.input.getX(0).toFloat(), Gdx.input.getY(0).toFloat())
        GameManager.STAGE.screenToStageCoordinates(tempCoords)

        var exit = false
        for(group in OVERLAY_LAYERS) {
            val hitActor = group.hit(tempCoords.x, tempCoords.y, true)
            hitActor?.let {
                if (hitActor is ActorGameObject) {
                    exit = true

                    if(hitActor != lastActorGameObjHit) {
                        lastActorGameObjHit?.setColor(1f, 1f, 1f, 1f)
                        hitActor.setColor(0.9f, 0.9f, 0.9f, 1f)
                        lastActorGameObjHit = hitActor
                    }
                }
            }

            if(exit) {
                return
            }
        }
    }

    fun addDrawnWidget(vec: Vector2, np: TilePosition, wrapper: WidgetWrapper) {
        if(isDrawn(vec)) {
            drawnRegions[vec]?.drawWidget(wrapper, np, WIDGET_LAYER)
        }
    }

    fun removeDrawnWidget(vec: Vector2, np: TilePosition) : Boolean {
        if(isDrawn(vec)) {
            return drawnRegions[vec]?.undrawWidget(np) ?: false
        }

        return false
    }
}