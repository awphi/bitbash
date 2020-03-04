package ph.adamw.bitbash.scene.layer

import ph.adamw.bitbash.GameManager

class UILayer : Layer() {
    init {
        setSize(GameManager.UI_STAGE.width, GameManager.UI_STAGE.height)
    }
}