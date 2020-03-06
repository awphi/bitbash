package ph.adamw.bitbash.game.data.world.generation

import ph.adamw.bitbash.game.data.tile.TileHandler

data class WorldSlice(val tileHandler: TileHandler, val lowerBound : Int, val upperBound : Int)