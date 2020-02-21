package ph.adamw.bitbash.game.actor.physics

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import java.rmi.UnexpectedException

abstract class PhysicsData {
    open val bodyType = BodyDef.BodyType.KinematicBody

    abstract val principleWidth : Float
    abstract val principleHeight : Float
    abstract val principleFixtureType : Shape.Type

    /**
     * Override this if the hitbox needs to be offset
     */
    open val origin : Vector2 = Vector2(0f, 0f)

    fun addPrincipleFixture(body: Body, origin: Vector2) {
        val f = FixtureDef()

        var shape : Shape? = null
        if (principleFixtureType == Shape.Type.Polygon) {
            shape = PolygonShape()
            shape.setAsBox(principleWidth / 2f, principleHeight / 2f, origin, 0f)
        } else if (principleFixtureType == Shape.Type.Circle) {
            shape = CircleShape()
            shape.radius = principleWidth / 2f
            shape.position = origin
        }

        f.shape = shape

        if(f.shape != null) {
            body.createFixture(f)
        } else {
            throw UnexpectedException("Null shape thrown - only circles and boxes are supported!")
        }

        shape?.dispose()
    }
}