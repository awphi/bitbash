package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.actor.entity.ActorShadow
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.Direction
import ph.adamw.bitbash.game.data.world.TilePosition

abstract class ActorEntity : ActorGameObject(), Json.Serializable {
    var facing : Direction = Direction.DOWN
        set(value) {
            val cache = field
            field = value

            if(cache != field) {
                facingChanged(cache)
            }
        }

    override fun parentChanged(old: Group?) {
        super.parentChanged(old)
        facingChanged(facing)

        if(this !is ActorShadow) {
            val shadow = ActorShadow.POOL.obtain()
            shadow.shadowing = this
            if (parent != null) {
                parent.addActor(shadow)
            }
        }
    }

    open fun facingChanged(oldFacing: Direction) {
        var st = initialTexturePath

        if(facing != Direction.DOWN) {
            st += "_${facing.name.toLowerCase()}"
        }

        setTexture(st)
    }

    open fun actEntity(delta: Float, tilePosition: TilePosition) {}

    abstract val initialTexturePath : String

    final override fun act(delta: Float) {
        super.act(delta)
        actEntity(delta, readOnlyTilePosition)
        if(hasBody) {
            val bx = (body.position.x * PhysicsData.PPM) - (physicsData!!.principleWidth / 2f)
            val by = (body.position.y * PhysicsData.PPM) - (physicsData!!.principleHeight / 2f)
            setPosition(bx, by)
        }
    }

    override fun read(json: Json?, jsonData: JsonValue?) {
        x = jsonData!!.getFloat("x")
        y = jsonData.getFloat("y")
        facing = Direction.values()[jsonData.getInt("facing")]
    }

    override fun write(json: Json?) {
        json!!.writeValue("x", x)
        json.writeValue("y", y)
        json.writeValue("facing", facing.ordinal)
    }
}