package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.actor.entity.ActorShadow
import ph.adamw.bitbash.game.data.world.Direction
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.OrderedDrawLayer

abstract class ActorEntity : ActorGameObject(), Json.Serializable {
    open val drawPriority : Int = DEFAULT_DRAW_PRIORITY

    var facing : Direction = Direction.DOWN
        set(value) {
            val cache = field
            field = value

            if(cache != field) {
                facingChanged(cache)
            }
        }

    override fun parentChanged(old: Group?) {
        facingChanged(facing)

        val shadow = ActorShadow()
        shadow.shadowing = this
        parent.addActor(shadow)
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
        Gdx.app.log("ACT", this.toString())
        if(hasBody) {
            val bx = body.position.x - (physicsData!!.principleWidth / 2f)
            val by = body.position.y - (physicsData!!.principleHeight / 2f)
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

    override fun positionChanged() {
        super.positionChanged()
        if(parent != null) {
            (parent as OrderedDrawLayer).update(this)
        }
    }

    companion object {
        const val DEFAULT_DRAW_PRIORITY = 5
    }
}