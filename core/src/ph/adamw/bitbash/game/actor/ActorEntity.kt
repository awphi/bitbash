package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.layer.SortedLayer
import java.io.Externalizable
import java.io.Serializable
import java.util.*

abstract class ActorEntity<Self : ActorEntity<Self>> : ActorGameObject(), Json.Serializable {
    init {
        setTexture(initialTexturePath)
    }

    open fun actEntity(actorEntity: Self, delta: Float, tilePosition: TilePosition) {}

    abstract val initialTexturePath : String

    override fun act(delta: Float) {
        super.act(delta)
        actEntity(this as Self, delta, readOnlyTilePosition)
        if(hasBody) {
            val bx = body.position.x - (physicsData!!.principleWidth / 2f)
            val by = body.position.y - (physicsData!!.principleHeight / 2f)
            setPosition(bx, by)
        }
    }

    override fun read(json: Json?, jsonData: JsonValue?) {
        x = jsonData!!.getFloat("x")
        y = jsonData.getFloat("y")
    }

    override fun write(json: Json?) {
        json!!.writeValue("x", x)
        json.writeValue("y", y)
    }

    override fun positionChanged() {
        super.positionChanged()
        if(parent != null) {
            (parent as SortedLayer).update(this)
        }
    }
}