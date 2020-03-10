package ph.adamw.bitbash.scene.layer

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

interface ILayer {
    fun draw(batch: Batch, parentAlpha : Float)
    fun update(actor: Actor)
}