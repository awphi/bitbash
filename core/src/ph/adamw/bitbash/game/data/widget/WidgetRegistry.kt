package ph.adamw.bitbash.game.data.widget

import ph.adamw.bitbash.game.data.widget.handlers.LampWidgetHandler

object WidgetRegistry {
    val REGISTRY : com.badlogic.gdx.utils.Array<WidgetHandler> = com.badlogic.gdx.utils.Array.with(
            LampWidgetHandler
    )
}