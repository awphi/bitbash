package ph.adamw.bitbash.game.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import ph.adamw.bitbash.BitbashApplication
import ph.adamw.bitbash.GameManager
import ph.adamw.bitbash.draw.ShaderBatch
import ph.adamw.bitbash.game.data.PhysicsData
import ph.adamw.bitbash.game.data.world.TilePosition
import ph.adamw.bitbash.scene.BitbashCoreScene
import ph.adamw.bitbash.scene.layer.OrderedDrawLayer

/**
 * The actor of a game object in the current instance of game - not saved, only the gameObject itself is saved. This
 * acts as a means to interact with the data object via mouse/key inputs (e.g. moving the player by applying force to
 * the actor's physics body affecting the position of the player data object) and drawing/animating the data object.
 */

abstract class ActorGameObject : Actor() {
    /**
     * Lower value = acts first, higher value = acts last
     */
    open val actPriority = DEFAULT_ACT_PRIORITY

    /**
     * Lower value = draws on top, higher value = draws further back (if equal it's based on y-coords)
     */
    open val drawPriority : Int = DEFAULT_DRAW_PRIORITY

    val readOnlyTilePosition: TilePosition = TilePosition.fromWorldPosition(x, y)

    init {
        this.name = actorName
        if(BitbashApplication.DEBUG && this !is ActorTile) {
            debug = true
        }
    }

    override fun setParent(parent: Group?) {
        val old = this.parent
        super.setParent(parent)
        parentChanged(old)
    }

    open fun parentChanged(old: Group?) {}

    protected abstract val actorName : String

    // Animation vars
    private val animations = HashMap<String, Animation<TextureAtlas.AtlasRegion>>()
    private var animActive : String? = null
    private var animStateTime: Float = 0f
    private var animLoop : Boolean = false
    private var animPause : Boolean = false

    private var bodyInternal : Body? = null

    abstract val physicsData: PhysicsData?

    val hasBody : Boolean
        get() = physicsData != null

    val body : Body by lazy {
        if(hasBody) {
            buildBody()
        }

        bodyInternal!!
    }

    //TODO instead of using the body test if the pixel hovered is transparent
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        val touchability = touchable && this.isTouchable || !touchable
        if(hasBody && isVisible && touchability) {
            val nx = x + this.x
            val ny = y + this.y

            for(i in body.fixtureList) {
                if(i.testPoint(nx, ny)) {
                    return this
                }
            }

            return null
        }

        return super.hit(x, y, touchable)
    }

    var texture : TextureAtlas.AtlasRegion = PLACEHOLDER
        set(value) {
            field = value
            width = texture.regionWidth.toFloat()
            height = texture.regionHeight.toFloat()
        }

    open fun addAdditionalFixtures(body: Body) {}

    fun buildBody() {
        val bodyDef = BodyDef()
        bodyDef.type = physicsData!!.bodyType
        bodyDef.position.set(x + width / 2f, y + height / 2f)
        bodyInternal = GameManager.physicsWorld.createBody(bodyDef)
        physicsData!!.addPrincipleFixture(bodyInternal!!, physicsData!!.origin)
        addAdditionalFixtures(bodyInternal!!)
    }

    fun deleteBody() {
        if(hasBody && bodyInternal != null) {
            GameManager.physicsWorld.destroyBody(bodyInternal!!)
        }
    }

    fun setTexture(name: String) {
        texture = getTexture(name)
    }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center)
    }

    override fun positionChanged() {
        readOnlyTilePosition.set(TilePosition.fromWorldX(x), TilePosition.fromWorldY(y))

        if(parent is OrderedDrawLayer) {
            (parent as OrderedDrawLayer).update(this)
        }

        super.positionChanged()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val shaderBatch = batch as ShaderBatch
        shaderBatch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        if(animActive == null || animations[animActive!!] == null) {
            shaderBatch.draw(texture, x, y, width * scaleX, height * scaleY)
        } else {

            if (!animPause) {
                animStateTime += Gdx.graphics.deltaTime
            }

            texture = animations[animActive!!]!!.getKeyFrame(animStateTime, animLoop)
            shaderBatch.draw(texture, x, y, width * scaleX, height * scaleY)

            if (animations[animActive!!]!!.isAnimationFinished(animStateTime)) {
                stopCurrentAnimation()
            }
        }
    }

    override fun toString(): String {
        return javaClass.simpleName + "[actor=${super.toString()}, sprite=$texture, pos=($x, $y)]"
    }

    fun stopCurrentAnimation() {
        animActive = null
        animStateTime = 0f
        animLoop = false
        animPause = false
    }

    fun pauseAnimation() {
        animPause = true
    }

    fun resumeAnimation() {
        animPause = false
    }

    fun isAnimated() : Boolean {
        return animActive != null && !animPause
    }

    fun setPositionWithBody(vec: Vector2) {
        setPositionWithBody(vec.x , vec.y)
    }

    fun setPositionWithBody(x: Float, y: Float) {
        if(hasBody) {
            body.setTransform(x + physicsData!!.principleWidth / 2f, y + physicsData!!.principleHeight / 2f, 0f)
        }

        setPosition(x, y)
    }

    fun playAnimation(name: String, loop: Boolean) {
        animActive = name
        animStateTime = 0f
        animLoop = loop
        animPause = false
    }

    fun addAnimation(name: String, frames: Array<TextureAtlas.AtlasRegion>, frameInterval: Float) {
        animations[name] = Animation(frameInterval, *frames)
    }

    open fun mouseClicked(button: Int, tilePosition: TilePosition, x: Float, y: Float) {}

    open fun mouseDragged(button: Int, tilePosition: TilePosition, x: Float, y: Float) {}

    companion object {
        private val tilesAtlas = TextureAtlas("textures-packed/tiles.atlas")
        private val objectsAtlas = TextureAtlas("textures-packed/objects.atlas")

        const val DEFAULT_ACT_PRIORITY : Int = 5
        const val DEFAULT_DRAW_PRIORITY : Int = 5

        private val atlasCache = HashMap<String, TextureAtlas.AtlasRegion>()
        val PLACEHOLDER = getTexture("placeholder")

        fun disposeTextureAtlas() {
            tilesAtlas.dispose()
            objectsAtlas.dispose()
        }

        fun getTexture(name: String) : TextureAtlas.AtlasRegion {
            if(atlasCache.containsKey(name)) {
                return atlasCache[name]!!
            }

            var tex = tilesAtlas.findRegion(name)

            if(tex == null) {
                tex = objectsAtlas.findRegion(name)
            }

            if(tex != null) {
                atlasCache[name] = tex
            }

            return tex ?: PLACEHOLDER
        }
    }
}