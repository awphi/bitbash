package ph.adamw.bitbash.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonReader
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorMob
import ph.adamw.bitbash.game.data.entity.mob.MobHandler
import ph.adamw.bitbash.game.data.entity.mob.PlayerHandler
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

class MobMap : HashMap<MobHandler, LinkedList<ActorMob>>() {
    fun read(fileHandle: FileHandle) {
        val mobMapIn = BitbashApplication.IO.asObject(fileHandle.readBytes()) as SavedMobMap

        for(i in mobMapIn.keys) {
            if(!containsKey(i)) {
                this[i] = LinkedList()
            }

            for(string in mobMapIn[i]!!) {
                val mob = ActorMob(i)
                val reader = JsonReader()
                mob.read(BitbashApplication.JSON, reader.parse(string))
                this[i]!!.add(mob)
            }
        }
    }

    fun write(mobFile : FileHandle) {
        val mobMapOut = SavedMobMap()

        for(i in keys) {
            if(!mobMapOut.containsKey(i)) {
                mobMapOut[i] = LinkedList()
            }

            for(mob in this[i]!!) {
                mobMapOut[i]!!.add(BitbashApplication.JSON.toJson(mob))
            }
        }

        mobFile.writeBytes(BitbashApplication.IO.asByteArray(mobMapOut), false)
    }

    fun add(handler: MobHandler, vector2: Vector2) {
        if(!containsKey(handler)) {
            this[handler] = LinkedList()
        }

        val mob = ActorMob(handler)
        mob.setPositionWithBody(vector2)

        this[handler]!!.add(mob)
    }

    class SavedMobMap : HashMap<MobHandler, LinkedList<String>>(), Serializable
}