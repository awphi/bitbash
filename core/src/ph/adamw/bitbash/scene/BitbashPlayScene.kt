package ph.adamw.bitbash.scene

import ph.adamw.bitbash.scene.ui.BitbashUILoader

object BitbashPlayScene : BitbashCoreScene() {
    override fun load() {
        super.load()
        BitbashUILoader.loadMapViewer(uiLayer!!)
    }

    override fun activeRegionsUpdated() {
        BitbashUILoader.updateMapViewer(map, activeRegionCoords)
    }
}
