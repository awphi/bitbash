package ph.adamw.bitbash.util

import com.badlogic.gdx.graphics.Camera
import ph.adamw.bitbash.game.actor.ActorPlayer

object CameraUtils {
    fun setCameraPos(camera: Camera, player: ActorPlayer, deltaTime: Float) {
        camera.position.x += (player.x - camera.position.x) * deltaTime
        camera.position.y += (player.y - camera.position.y) * deltaTime
        camera.update()
    }

    fun setCameraPos(camera: Camera, x: Float, y: Float) {
        camera.position.set(x, y, 0f)
        camera.update()
    }
}
