package ph.adamw.bitbash.game.data.entity.mob

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.actor.ActorMob
import ph.adamw.bitbash.game.data.entity.EntityHandler

abstract class MobHandler(name: String) : EntityHandler<ActorMob>(name) {
    override fun getTexturePath(): String {
        return "entities/$name"
    }

    abstract fun write(json: Json?)

    abstract fun read(json: Json?, jsonData: JsonValue?)
}