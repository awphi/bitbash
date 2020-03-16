package ph.adamw.bitbash.scene

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ph.adamw.bitbash.scene.ui.BitbashUIManager

class BitbashGlobalListener : InputListener() {
    override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
        if(keycode == Input.Keys.T) {
            if(keycode == Input.Keys.T) {
                BitbashUIManager.openConsole()
            }

            return true
        } else if(keycode == Input.Keys.M) {
            BitbashPlayScene.toggleMap()

            return true
        }

        return super.keyUp(event, keycode)
    }
}