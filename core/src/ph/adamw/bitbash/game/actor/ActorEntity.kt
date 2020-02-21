package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.data.world.TilePosition

abstract class ActorEntity(name: String) : ActorGameObject(name), Json.Serializable {
    abstract fun actEntity(delta: Float, tilePosition: TilePosition)

    override fun act(delta: Float) {
        super.act(delta)
        actEntity(delta, readOnlyTilePosition)
        if(hasBody) {
            setPosition(body.position.x - (physicsData!!.principleWidth / 2f), body.position.y - (physicsData!!.principleHeight / 2f))
        }
    }

    override fun write(json: Json?) {
        val j = json!!
        j.writeValue("x", x)
        j.writeValue("y", y)
    }

    override fun read(json: Json?, jsonData: JsonValue?) {
        val j = jsonData!!
        x = j.getFloat("x")
        y = j.getFloat("y")
    }
}