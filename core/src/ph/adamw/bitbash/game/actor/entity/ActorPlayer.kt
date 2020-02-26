package ph.adamw.bitbash.game.actor.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.utils.Json
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition

class ActorPlayer : ActorEntity<ActorPlayer>(), Json.Serializable {
    override val initialTexturePath: String
        get() = "entities/player"

    override val actorName: String
        get() = "player"

    override fun actEntity(actorEntity: ActorPlayer, delta: Float, tilePosition: TilePosition) {
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
            actorEntity.body.setLinearVelocity(x, y)
        } else {
            actorEntity.body.setLinearVelocity(0f, 0f)
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
        private const val STEP = 100f
        private const val SPRINT_MODIFIER = 1.5f;
    }
}