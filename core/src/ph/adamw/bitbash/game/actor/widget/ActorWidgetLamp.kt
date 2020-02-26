package ph.adamw.bitbash.game.actor.widget

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.PhysicsData

class ActorWidgetLamp : ActorWidget<ActorWidgetLamp>() {
    override val physicsData: PhysicsData?
        get() = Physics

    override val initialTexturePath: String
        get() = "widgets/lamp"

    object Physics : PhysicsData() {
        override val principleWidth: Float
            get() = 24f
        override val principleHeight: Float
            get() = 8f
        override val origin: Vector2
            get() = Vector2(2f,  0f)
        override val principleFixtureType: Shape.Type
            get() = Shape.Type.Polygon
    }

    override val actorName: String
        get() = "lamp"
}