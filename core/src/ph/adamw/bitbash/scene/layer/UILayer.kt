package ph.adamw.bitbash.scene.layer

import ph.adamw.bitbash.GameManager

class UILayer : Layer() {
    init {
        setSize(GameManager.MIN_WORLD_WIDTH, GameManager.MIN_WORLD_HEIGHT)
        GameManager.UI_LAYERS.add(this)
    }
}