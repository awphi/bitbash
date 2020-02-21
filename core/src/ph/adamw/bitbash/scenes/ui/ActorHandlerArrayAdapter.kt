package ph.adamw.bitbash.scenes.ui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.data.ActorHandler

class ActorHandlerArrayAdapter<T : ActorHandler<*>>(array: com.badlogic.gdx.utils.Array<T>) : ArrayAdapter<T, VisTable>(array) {
    init {
        selectionMode = SelectionMode.SINGLE
    }

    override fun createView(item: T?): VisTable {
        val table = VisTable()
        table.center()
        table.background(DESELECTED)
        table.add(VisLabel(item!!.name)).fillX().expandX().padRight(5f)
        val img = Image(ActorGameObject.getTexture(item.getTexturePath()).texture)
        table.add(img)

        return table
    }

    override fun selectView(view: VisTable?) {
        view!!.background(SELECTED)
    }

    override fun deselectView(view: VisTable?) {
        view!!.background(DESELECTED)
    }

    companion object {
        val SELECTED = TextureRegionDrawable(ActorGameObject.getTexture("selected"))
        val DESELECTED = TextureRegionDrawable(ActorGameObject.getTexture("deselected"))
    }
}