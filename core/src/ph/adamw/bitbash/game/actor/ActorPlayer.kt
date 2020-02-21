package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.physics.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition

class ActorPlayer : ActorEntity("player") {
    init {
        setTexture("player")
    }

    override fun actEntity(delta: Float, tilePosition: TilePosition) {
        val step = STEP * if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) SPRINT_MODIFIER else 1f

        var x = 0f
        var y = 0f

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += step
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= step
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= step
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += step
        }

        if(!GameManager.lockInput) {
            body.setLinearVelocity(x, y)
        } else {
            body.setLinearVelocity(0f, 0f)
        }
    }

    override val physicsData: PhysicsData?
        get() = Physics

    companion object {
        private const val STEP = 100f
        private const val SPRINT_MODIFIER = 1.5f;
    }

    object Physics : PhysicsData() {
        override val principleWidth: Float
            get() = 24f
        override val principleHeight: Float
            get() = 24f
        override val origin: Vector2
            get() = Vector2(1.5f, 2.5f)
        override val bodyType: BodyDef.BodyType
            get() = BodyDef.BodyType.DynamicBody

        override val principleFixtureType: Shape.Type
            get() = Shape.Type.Polygon
    }
}