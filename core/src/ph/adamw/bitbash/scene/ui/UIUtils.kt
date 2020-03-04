package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
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

    const val MAP_REGION_OVERVIEW_SCALE = 1f

    fun generateMapRegionOverview(mapRegion: MapRegion) : Actor {
        val baseTex = ActorGameObject.getTexture("grass")

        baseTex.texture.textureData.prepare()
        val tileTextures = baseTex.texture.textureData.consumePixmap()

        val pixmap = Pixmap(MapRegion.REGION_SIZE, MapRegion.REGION_SIZE, tileTextures.format)
        pixmap.blending = Pixmap.Blending.None

        for(i in mapRegion.tiles.indices) {
            for(j in mapRegion.tiles[i].indices) {
                val tile = mapRegion.tiles[i][MapRegion.REGION_SIZE - (j + 1)]
                val tileTex = ActorGameObject.getTexture(tile.getTextureName())
                val cl = tileTextures.getPixel(tileTex.regionX + 1, tileTex.regionY + 1)
                pixmap.drawPixel(i, j, cl)
            }
        }

        baseTex.texture.textureData.disposePixmap()

        val actor = ActorSimple("map_ui_element_${mapRegion.coords}")
        actor.texture = TextureRegion(Texture(pixmap))
        actor.setScale(MAP_REGION_OVERVIEW_SCALE)
        return actor
    }
}