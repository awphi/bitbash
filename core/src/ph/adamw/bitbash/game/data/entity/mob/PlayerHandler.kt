package ph.adamw.bitbash.game.data.entity.mob

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorMob
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition

object PlayerHandler : MobHandler("player") {
    private const val STEP = 100f
    private const val SPRINT_MODIFIER = 1.5f;

    override val physicsData: PhysicsData?
        get() = Physics

    override fun write(json: Json?) {}

    override fun read(json: Json?, jsonData: JsonValue?) {}

    override fun act(actorEntity: ActorMob, delta: Float, tilePosition: TilePosition) {
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
}