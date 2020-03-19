package ph.adamw.bitbash.scene.event

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import java.lang.RuntimeException

class KeyListener(private vararg val keys : Int) : InputListener() {
    private val keysDown : HashSet<Int> = HashSet()

    override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
        if(keycode in keys) {
            keysDown.remove(keycode)
            return true
        }

        return super.keyUp(event, keycode)
    }

    override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
        if(keycode in keys) {
            keysDown.add(keycode)
            return true
        }

        return super.keyDown(event, keycode)
    }

    fun isDown(int: Int) : Boolean {
        if(int !in keys) {
            throw RuntimeException("Unexpected input to isDown, key: '$int' is not being listened to by this listener.")
        }

        return keysDown.contains(int)
    }
}