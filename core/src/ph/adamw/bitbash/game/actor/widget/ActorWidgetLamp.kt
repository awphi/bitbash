package ph.adamw.bitbash.game.actor.widget

import box2dLight.Light
import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.scenes.scene2d.Group
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorWidget
import ph.adamw.bitbash.game.data.PhysicsData

class ActorWidgetLamp : ActorWidget() {
    var light : Light? = null

    override val physicsData: PhysicsData?
        get() = Physics

    override val initialTexturePath: String
        get() = "widgets/lamp"

    override fun added() {
        super.added()
        light = PointLight(GameManager.rayHandler, 32, Color.WHITE, 16f, (x + width / 2f) / PhysicsData.PPM, (y + height - 4) / PhysicsData.PPM)
    }

    override fun removed() {
        light?.remove()
    }

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