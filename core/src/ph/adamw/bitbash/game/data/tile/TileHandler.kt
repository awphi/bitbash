package ph.adamw.bitbash.game.data.tile

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorTile
import ph.adamw.bitbash.game.data.ActorHandler
import ph.adamw.bitbash.game.data.PhysicsData
import java.io.Serializable

abstract class TileHandler(name : String) : ActorHandler<ActorTile>(name) {
    override fun getTextureName() : String {
        return name
    }

    @Transient
    open val color : Color = Color(1f, 1f, 1f, 1f)

    open val drawPriority : Int = -edgePriority

    abstract val edgePriority : Int

    override val physicsData: PhysicsData?
        get() = null
}