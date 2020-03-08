package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ph.adamw.bitbash.game.actor.ActorGameObject
import ph.adamw.bitbash.game.actor.ActorSimple
import ph.adamw.bitbash.game.data.world.MapRegion

object UIUtils {
    //TODO replace this placeholder skin
    val SKIN : Skin = Skin(Gdx.files.local("ui/neutralizer-ui.json"))

    private val TILE_TEX = ActorGameObject.getTexture("grass")
    private val TILE_TEXTURES : Pixmap

    init {
        TILE_TEX.texture.textureData.prepare()
        TILE_TEXTURES = TILE_TEX.texture.textureData.consumePixmap()
    }

    fun generateMapRegionOverview(mapRegion: MapRegion) : Pixmap {
        val pixmap = Pixmap(MapRegion.REGION_SIZE, MapRegion.REGION_SIZE, TILE_TEXTURES.format)
        pixmap.blending = Pixmap.Blending.None

        for(i in mapRegion.tiles.indices) {
            for(j in mapRegion.tiles[i].indices) {
                val tile = mapRegion.tiles[i][MapRegion.REGION_SIZE - (j + 1)]
                val tileTex = ActorGameObject.getTexture(tile.getTextureName())
                val cl = TILE_TEXTURES.getPixel(tileTex.regionX + 1, tileTex.regionY + 1) and Color.rgba8888(tile.color)
                pixmap.drawPixel(i, j, cl)
            }
        }

        return pixmap
    }

    fun dispose() {
        TILE_TEX.texture.textureData.disposePixmap()
    }
}