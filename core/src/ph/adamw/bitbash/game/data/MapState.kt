package ph.adamw.bitbash.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.entity.ActorPlayer
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import java.io.FileFilter
import java.util.*


class MapState(val name: String, var map: Map, val player: ActorPlayer) {
    val handle : FileHandle = Gdx.files.local("$MAPS_DIR/$name")
    val mobs = LinkedList<ActorEntity>()

    init {
        handle.mkdirs()
        map.setMapState(this)
    }

    fun save() {
        Gdx.app.log("CLOSE", "Saving game state: '$name'")
        map.unload()
        val mobFile = handle.child(MOB_DATA_FILE)
        val dataFile = handle.child(MAP_DATA_FILE)
        val playerFile = handle.child(PLAYER_DATA_FILE)

        playerFile.writeBytes(BitbashApplication.IO.asByteArray(player), false)
        BitbashApplication.IO.objectOutput.buffer
        playerFile.writeBytes(BitbashApplication.IO.asByteArray(player), false)
        dataFile.writeBytes(BitbashApplication.IO.asByteArray(map), false)
        mobFile.writeBytes(BitbashApplication.IO.asByteArray(mobs), false)

        Gdx.app.log("CLOSE","Saved game state!")
    }

    companion object {
        const val MAPS_DIR = "maps"
        const val MAP_DATA_FILE = "data.bin"
        const val MOB_DATA_FILE = "mobs.bin"
        const val PLAYER_DATA_FILE = "player.bin"

        fun build(name: String, handle : FileHandle) : MapState {
            val m = BitbashApplication.IO.asObject(handle.child(MAP_DATA_FILE).readBytes()) as Map
            val p = BitbashApplication.IO.asObject(handle.child(PLAYER_DATA_FILE).readBytes()) as ActorPlayer
            val mobsIn = BitbashApplication.IO.asObject(handle.child(MOB_DATA_FILE).readBytes()) as LinkedList<*>

            val gs = MapState(name, m, p)

            for(i in mobsIn) {
                gs.mobs.add(i as ActorEntity)
            }

            handle.child("regions").list(FileFilter {
                return@FileFilter it.nameWithoutExtension.matches(Regex("rg-?[\\d]*_-?[\\d]*"))
            }).forEach {
                gs.map.addRegion(BitbashApplication.IO.asObject(it.readBytes()) as MapRegion)
            }

            return gs
        }

        internal fun load(name: String) : MapState {
            Gdx.app.log("LOAD", "Loading game state: '$name'")
            val handle = Gdx.files.local("$MAPS_DIR/$name")

            if(handle.child(MAP_DATA_FILE).exists()) {
                Gdx.app.log("LOAD", "Found game state '$name' - loading...")
                return build(name, handle)
            }

            Gdx.app.log("LOAD", "No saved game state: '$name'. Using a new game state...")
            return MapState(name, Map(), ActorPlayer())
        }
    }
}

