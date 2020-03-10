package ph.adamw.bitbash.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorGameObject

class BitbashSceneListener : ClickListener(-1) {
    var draggedOn : ActorGameObject? = null

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        Gdx.app.log("TEX", "HIT")
        val hit : Actor? = GameManager.PLAY_STAGE.hit(x, y, true)

        if(hit !is ActorGameObject || GameManager.lockInput) {
            return true
        }

        if(draggedOn == null) {
            draggedOn = hit
        }

        hit.mouseClicked(button, hit.readOnlyTilePosition, x, y)


        return super.touchDown(event, x, y, pointer, button)
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        super.touchUp(event, x, y, pointer, button)
        draggedOn = null
    }

    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        super.touchDragged(event, x, y, pointer)

        if(GameManager.lockInput || draggedOn == null) {
            return
        }

        draggedOn!!.mouseDragged(pressedButton, draggedOn!!.readOnlyTilePosition, x, y)
    }
}