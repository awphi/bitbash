package ph.adamw.bitbash.scene

import ph.adamw.bitbash.GameManager

abstract class Scene {
    abstract fun preDraw()
    abstract fun postDraw()
    abstract fun pause()

    abstract fun load()

    open fun resize(width: Int, height: Int) {
        GameManager.STAGE.viewport.update(width, height, false)
    }
}