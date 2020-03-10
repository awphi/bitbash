package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

interface Updatable {
    fun update(actor: Actor)
}