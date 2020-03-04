package ph.adamw.bitbash.scene

import ph.adamw.bitbash.GameManager

abstract class Scene {
    /**
     * Before anything has drawn
     */
    open fun preDraw() {}

    /**
     * After everything has drawn
     */
    open fun postDraw() {}


    open fun pause() {}

    abstract fun load()

    open fun resize(width: Int, height: Int) {
        GameManager.PLAY_STAGE.viewport.update(width, height, false)
        GameManager.UI_STAGE.viewport.update(width, height, true)

        for(i in GameManager.UI_LAYERS) {
            i.value.setSize(width.toFloat(), height.toFloat())
        }
    }
}