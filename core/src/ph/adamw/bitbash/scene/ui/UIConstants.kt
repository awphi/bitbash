package ph.adamw.bitbash.scene.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin

object UIConstants {
    private val fontGenerator = FreeTypeFontGenerator(Gdx.files.local("ui/fonts/PIXELADE.ttf"))
    val SKIN : Skin = Skin(Gdx.files.local("ui/neutralizer-ui.json"))
    val PIXEL_FONT_15 : BitmapFont

    init {
        val fontParams = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParams.size = 15
        PIXEL_FONT_15 = fontGenerator.generateFont(fontParams)
        PIXEL_FONT_15.setUseIntegerPositions(false)
    }
}