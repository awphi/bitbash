package ph.adamw.bitbash.game.actor.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import ph.adamw.bitbash.game.actor.ActorEntity
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition

class ActorShadow : ActorEntity(), Pool.Poolable {
    override val drawPriority: Int
        get() = 10

    override val actPriority: Int
        get() = DEFAULT_ACT_PRIORITY + 1

    var shadowing : ActorEntity? = null
        set(value) {
            field = value

            if(field != null) {
                isVisible = true
                setShadowSize()
            }
        }

    override fun getColor(): Color {
        return ActorShadow.color
    }

    override fun parentChanged(old: Group?) {
        touchable = Touchable.disabled
    }

    private fun setShadowSize() {
        val w = shadowing!!.width

        if(w > 24f) {
            setTexture("entities/shadow-large")
        } else if(w > 16f) {
            setTexture("entities/shadow-med")
        } else {
            setTexture("entities/shadow-small")
        }
    }

    override fun actEntity(delta: Float, tilePosition: TilePosition) {
        if(shadowing != null) {
            if(!shadowing!!.hasParent()) {
                shadowing = null
                POOL.free(this)
                return
            }

            setPositionWithBody(shadowing!!.x + (shadowing!!.width - width) / 2f, shadowing!!.y + 1f - height / 2f)
        } else if(isVisible) {
            isVisible = false
        }
    }

    override val initialTexturePath: String
        get() = "entities/shadow-med"

    override val actorName: String
        get() = "shadow"

    override val physicsData: PhysicsData?
        get() = null

    override fun reset() {
        shadowing = null
    }

    companion object {
        private val color = Color(1f, 1f, 1f, 0.6f)
        val POOL: Pool<ActorShadow> = Pools.get(ActorShadow::class.java)
    }
}