package ph.adamw.bitbash.scene

import ph.adamw.bitbash.scene.ui.BitbashUIManager

object BitbashPlayScene : BitbashCoreScene() {
    override fun load() {
        super.load()
        //BitbashUIManager.loadMapViewer(uiLayer!!)
    }

    override fun activeRegionsUpdated() {
        //BitbashUIManager.updateMapViewer(map, activeRegionCoords)
    }
}
