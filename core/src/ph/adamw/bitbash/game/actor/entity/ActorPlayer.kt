package ph.adamw.bitbash.game.actor.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Json
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.Direction
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.event.KeyListener

class ActorPlayer : ActorEntity(), Json.Serializable {
    override val actPriority: Int
        get() = 0

    override val initialTexturePath: String
        get() = "entities/player"

    override val actorName: String
        get() = "player"

    private val listener = KeyListener(Input.Keys.W, Input.Keys.A, Input.Keys.S, Input.Keys.D, Input.Keys.SHIFT_LEFT)

    override fun added() {
        super.added()
        addListener(listener)
    }

    override fun removed() {
        super.removed()
        clearListeners()
    }

    override fun actEntity(delta: Float, tilePosition: TilePosition) {
        val step = STEP * if (listener.isDown(Input.Keys.SHIFT_LEFT)) SPRINT_MODIFIER else 1f

        var x = 0f
        var y = 0f

        if(listener.isDown(Input.Keys.W)) {
            y += 1f
            facing = Direction.UP
        }

        if(listener.isDown(Input.Keys.S)) {
            y -= 1f
            facing = Direction.DOWN
        }

        if(listener.isDown(Input.Keys.A)) {
            x -= 1f
            facing = Direction.LEFT
        }

        if(listener.isDown(Input.Keys.D)) {
            x += 1f
            facing = Direction.RIGHT
        }


        val a = step / SQRT_2

        if((x != 0f).xor(y != 0f)) {
            body.setLinearVelocity(x * step , y * step)
        } else {
            body.setLinearVelocity(x * a, y * a)
        }
    }

    override val physicsData: PhysicsData?
        get() = Physics

    object Physics : PhysicsData() {
        override val principleWidth: Float
            get() = 14f
        override val principleHeight: Float
            get() = 6f
        override val origin: Vector2
            get() = Vector2(6f, 0f)
        override val bodyType: BodyDef.BodyType
            get() = BodyDef.BodyType.DynamicBody

        override val principleFixtureType: Shape.Type
            get() = Shape.Type.Polygon
    }

    companion object {
        private const val STEP = 20f
        private const val SPRINT_MODIFIER = 1.2f
        private const val SQRT_2 = 1.41421356237309504880168872420969807856967187537694807317667973799f
    }
}