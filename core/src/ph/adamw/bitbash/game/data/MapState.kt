package ph.adamw.bitbash.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorPlayer
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import java.io.FileFilter


class MapState(val name: String, var map: Map, var player: ActorPlayer) {
    val handle : FileHandle = Gdx.files.local("$MAPS_DIR/$name")

    init {
        handle.mkdirs()
        map.setMapState(this)
    }

    fun save() {
        Gdx.app.log("CLOSE", "Saving game state: '$name'")
        map.unload()
        val playerFile = handle.child(PLAYER_FILE)
        val dataFile = handle.child(MAP_DATA_FILE)
        JSON.toJson(player, playerFile)
        val bytes = BitbashApplication.IO.asByteArray(map)
        dataFile.writeBytes(bytes, false)
        Gdx.app.log("CLOSE","Saved game state!")
    }

    companion object {
        const val MAPS_DIR = "maps"
        const val PLAYER_FILE = "player.json"
        const val MAP_DATA_FILE = "data.bin"
        internal val JSON = Json()

        fun build(name: String, handle : FileHandle) : MapState {
            val m = BitbashApplication.IO.asObject(handle.child(MAP_DATA_FILE).readBytes()) as Map
            val p = JSON.fromJson(ActorPlayer::class.java, handle.child(PLAYER_FILE))

            val gs = MapState(name, m, p)

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

