package ph.adamw.bitbash.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.math.Vector2
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.game.actor.ActorMob
import ph.adamw.bitbash.util.MobMap
import ph.adamw.bitbash.game.data.entity.mob.PlayerHandler
import ph.adamw.bitbash.game.data.world.Map
import ph.adamw.bitbash.game.data.world.MapRegion
import java.io.FileFilter


class MapState(val name: String, var map: Map, val mobMap : MobMap) {
    val handle : FileHandle = Gdx.files.local("$MAPS_DIR/$name")

    val player : ActorMob
        get() {
            return mobMap[PlayerHandler]!!.first
        }

    init {
        handle.mkdirs()
        map.setMapState(this)
    }

    fun save() {
        Gdx.app.log("CLOSE", "Saving game state: '$name'")
        map.unload()
        val mobFile = handle.child(MOB_DATA_FILE)
        val dataFile = handle.child(MAP_DATA_FILE)
        dataFile.writeBytes(BitbashApplication.IO.asByteArray(map), false)
        mobMap.write(mobFile)
        Gdx.app.log("CLOSE","Saved game state!")
    }

    companion object {
        const val MAPS_DIR = "maps"
        const val MAP_DATA_FILE = "data.bin"
        const val MOB_DATA_FILE = "mobs.bin"

        fun build(name: String, handle : FileHandle) : MapState {
            val m = BitbashApplication.IO.asObject(handle.child(MAP_DATA_FILE).readBytes()) as Map

            val gs = MapState(name, m, MobMap())

            gs.mobMap.read(handle.child(MOB_DATA_FILE))

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
            val state = MapState(name, Map(), MobMap())
            state.mobMap.add(PlayerHandler, Vector2(0f, 0f))
            return state
        }
    }
}

