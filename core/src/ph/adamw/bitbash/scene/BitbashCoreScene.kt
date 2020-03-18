package ph.adamw.bitbash.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorGroupMapRegion
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.MapState
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.world.*
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.scene.layer.Layer
import ph.adamw.bitbash.scene.layer.MultiLayer
import ph.adamw.bitbash.scene.layer.SelfOrderedLayer
import ph.adamw.bitbash.util.CameraUtils
import java.util.*
import kotlin.collections.HashMap

abstract class BitbashCoreScene : Scene() {
    private val tempCoords = Vector2(0f, 0f)
    private val buildLimitRect = Rectangle()

    var mapState: MapState? = null

    val map : Map
        get() = mapState!!.map

    private val drawnRegions = HashMap<Vector2, ActorGroupMapRegion>()
    val activeRegionCoords = HashSet<Vector2>()

    private var gameObjectLayer : MultiLayer? = null
    private var entityLayer : SelfOrderedLayer? = null
    private var tileMultiLayer : MultiLayer? = null

    private val overlay : Image = Image(NinePatchDrawable(ActorGameObject.getPatch("border")))

    // Highest priority first
    @Suppress("UNCHECKED_CAST")
    private val overlayLayers : Sequence<Actor?> by lazy {
        sequenceOf(
                entityLayer,
                tileMultiLayer
        ) as Sequence<Actor?>
    }

    override fun load(playMultiLayer: MultiLayer, uiMultiLayer: MultiLayer) {
        gameObjectLayer = playMultiLayer.addMultiLayer(0)

        val overlayLayer = gameObjectLayer!!.addDefaultLayer(0)
        entityLayer = gameObjectLayer!!.addSelfOrderedLayer(1)
        tileMultiLayer = gameObjectLayer!!.addMultiLayer(2)

        overlay.color.mul(1f, 1f, 1f, 0.5f)
        overlay.touchable = Touchable.disabled
        overlayLayer.addActor(overlay)

        gameObjectLayer!!.addListener(BitbashGlobalListener())
        GameManager.PLAY_STAGE.keyboardFocus = mapState!!.player

        entityLayer!!.addActor(mapState!!.player)
        CameraUtils.setCameraPos(GameManager.WORLD_CAMERA, mapState!!.player.x, mapState!!.player.y)

        GameManager.rayHandler.setAmbientLight(0f, 0f, 0f, 0.8f)
        GameManager.rayHandler.setBlurNum(3)
    }

    override fun postDraw() {
        GameManager.rayHandler.setCombinedMatrix(GameManager.WORLD_CAMERA.combined.scl(PhysicsData.PPM))
        GameManager.rayHandler.updateAndRender()
        updateActiveRegions()
    }

    override fun preDraw() {
        super.preDraw()
        restageMap()
        CameraUtils.setCameraPos(GameManager.WORLD_CAMERA, mapState!!.player, Gdx.graphics.deltaTime)
        applyOutlines()
    }

    override fun pause() {
        mapState!!.save()
        super.pause()
    }

    private fun restageMap() {
        val it = drawnRegions.keys.iterator()
        while(it.hasNext()) {
            val i = it.next()
            if(!activeRegionCoords.contains(i)) {
                val drawnMapRegion = drawnRegions[i]
                drawnMapRegion?.removeFromStage()
                map.unloadRegion(drawnMapRegion?.region!!.coords)
                it.remove()
            }
        }

        for (vec : Vector2 in activeRegionCoords) {
            val region = mapState!!.map.getOrLoadRegion(vec)
            if (region != null && !isRegionDrawn(vec)) {
                val drawnMapRegion = ActorGroupMapRegion.POOL.obtain()
                drawnMapRegion.region = region
                drawnMapRegion.loadToStage(tileMultiLayer!!, entityLayer!!)
                drawnRegions[vec] = drawnMapRegion
            }
        }

        for(i in activeRegionCoords) {
            val rg = map.getOrLoadRegion(i)
            if(rg != null && rg.isDirty(MapRegionFlag.NEEDS_EDGE)) {
                drawnRegions[i]!!.edgeRegion(tileMultiLayer!!)
            }
        }
    }

    fun refreshActiveRegions(regions: MutableSet<Vector2>) {
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

    private fun updateActiveRegions() {
        refreshActiveRegions(activeRegionCoords)

        // Generates new regions
        for (vec in activeRegionCoords) {
            if(map.getOrLoadRegion(vec) == null && !map.canRegionLoaded(vec)) {
                map.generateRegionAt(vec)
            }
        }

        activeRegionsUpdated()
    }

    open fun activeRegionsUpdated() {}

    private fun isRegionDrawn(vec: Vector2) : Boolean {
        return drawnRegions.containsKey(vec)
    }

    fun updateDrawnTile(rg: MapRegion, np: TilePosition, tile: TileHandler) {
        if(isRegionDrawn(rg.coords)) {
            drawnRegions[rg.coords]!!.drawTile(np, tileMultiLayer!!, tile)
        }
    }

    private fun applyOutlines() {
        tempCoords.set(Gdx.input.getX(0).toFloat(), Gdx.input.getY(0).toFloat())
        GameManager.PLAY_STAGE.screenToStageCoordinates(tempCoords)

        var exit = false
        for(layer in overlayLayers) {
            val hitActor = layer?.hit(tempCoords.x, tempCoords.y, true)
            hitActor?.let {
                if (hitActor is ActorGameObject) {
                    exit = true
                    overlay.setBounds(hitActor.x, hitActor.y, hitActor.width, hitActor.height)
                }
            }

            if(exit) {
                return
            }
        }
    }

    fun addDrawnWidget(vec: Vector2, widget: ActorWidget) {
        if(isRegionDrawn(vec)) {
            drawnRegions[vec]?.drawWidget(widget, entityLayer!!)
        }
    }

    fun removeDrawnWidget(vec: Vector2, actorWidget: ActorWidget) {
        if(isRegionDrawn(vec)) {
            drawnRegions[vec]?.undrawWidget(actorWidget)
        }
    }

    private fun setBuildRectangle() {
        buildLimitRect.set(mapState!!.player.x - BUILD_DISTANCE, mapState!!.player.y - BUILD_DISTANCE, BUILD_DISTANCE * 2, BUILD_DISTANCE * 2)
    }

    companion object {
        private val BUILD_DISTANCE : Float
            get() {
                return if(BitbashApplication.GEN_TEST) {
                    10000f
                } else {
                    2000f
                }
            }
    }
}