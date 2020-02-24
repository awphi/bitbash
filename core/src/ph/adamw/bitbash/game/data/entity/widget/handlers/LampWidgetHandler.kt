package ph.adamw.bitbash.game.data.entity.widget.handlers

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.entity.widget.WidgetHandler

object LampWidgetHandler : WidgetHandler("lamp") {
    override val physicsData: PhysicsData?
        get() = Physics

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
}