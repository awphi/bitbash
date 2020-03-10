package ph.adamw.bitbash.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.TimeUtils
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.scene.layer.MultiLayer
import ph.adamw.bitbash.scene.layer.UILayer

abstract class Scene {
    private var lastTimeCounted: Long = 0
    private var sinceChange = 0f
    var frameRate = 0f

    fun updateFps() {
        val delta = TimeUtils.timeSinceMillis(lastTimeCounted)
        lastTimeCounted = TimeUtils.millis()

        sinceChange += delta
        if (sinceChange >= 1000) {
            sinceChange = 0f
            frameRate = Gdx.graphics.framesPerSecond.toFloat()
        }
    }

    /**
     * Before anything has drawn
     */
    open fun preDraw() {
        updateFps()
    }

    /**
     * After everything has drawn
     */
    open fun postDraw() {}

    open fun dispose() {}

    open fun pause() {}

    abstract fun load(playMultiLayer: MultiLayer, uiMultiLayer: MultiLayer)

    open fun resize(width: Int, height: Int) {
        GameManager.PLAY_STAGE.viewport.update(width, height, false)
        GameManager.UI_STAGE.viewport.update(width, height, true)

        for(i in GameManager.uiMultiLayer) {
            if(i.value is UILayer) {
                (i.value as UILayer).setSize(width.toFloat(), height.toFloat())
            }
        }
    }
}