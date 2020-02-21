package ph.adamw.bitbash.game.data.widget

import java.io.Serializable

data class WidgetWrapper(var handler: WidgetHandler, var offsetX : Float, var offsetY : Float) : Serializable {
}