package ph.adamw.bitbash.game.data.widget.handlers

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ph.adamw.bitbash.game.actor.physics.PhysicsData
import ph.adamw.bitbash.game.data.widget.WidgetHandler

object LampWidgetHandler : WidgetHandler("lamp") {
    override val physicsData: PhysicsData?
        get() = Physics

    object Physics : PhysicsData() {
        override val principleWidth: Float
            get() = 22f
        override val principleHeight: Float
            get() = 6f
        override val origin: Vector2
            get() = Vector2(4f, 2f)
        override val principleFixtureType: Shape.Type
            get() = Shape.Type.Polygon
    }
}