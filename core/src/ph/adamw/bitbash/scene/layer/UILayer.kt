package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.scenes.scene2d.Group
import ph.adamw.bitbash.GameManager

class UILayer : Group() {
    init {
        setSize(GameManager.UI_STAGE.width, GameManager.UI_STAGE.height)
    }
}