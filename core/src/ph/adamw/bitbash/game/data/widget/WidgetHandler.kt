package ph.adamw.bitbash.game.data.widget

import com.badlogic.gdx.physics.box2d.Body
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.ActorHandler
import ph.adamw.bitbash.game.data.world.TilePosition

abstract class WidgetHandler(name: String) : ActorHandler<ActorWidget>(name) {
    override fun getTexturePath() : String {
        return "widgets/$name"
    }

    open fun addAdditionalFixtures(body: Body) {}
    open fun act(delta: Float, tilePosition: TilePosition) {}
}