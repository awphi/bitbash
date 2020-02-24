package ph.adamw.bitbash.game.data.entity.widget

import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.entity.EntityHandler

abstract class WidgetHandler(name: String) : EntityHandler<ActorWidget>(name) {
    override fun getTexturePath() : String {
        return "widgets/$name"
    }
}