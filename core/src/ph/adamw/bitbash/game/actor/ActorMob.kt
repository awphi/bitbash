package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.data.entity.mob.MobHandler

class ActorMob(handler: MobHandler) : ActorEntity<ActorMob, MobHandler>(handler), Json.Serializable {
    override fun write(json: Json?) {
        json!!.writeValue("x", x)
        json.writeValue("y", y)
        handler.write(json)
    }

    override fun read(json: Json?, jsonData: JsonValue?) {
        x = jsonData!!.getFloat("x")
        y = jsonData.getFloat("y")
        handler.read(json, jsonData)
    }
}