package ph.adamw.bitbash.util.io

import org.nustaq.serialization.FSTBasicObjectSerializer
import org.nustaq.serialization.FSTClazzInfo
import org.nustaq.serialization.FSTObjectInput
import org.nustaq.serialization.FSTObjectOutput
import ph.adamw.bitbash.game.data.tile.TileHandler
import ph.adamw.bitbash.game.data.tile.TileRegistry

object TileHandlerFSTSerializer : FSTBasicObjectSerializer() {
    override fun writeObject(out: FSTObjectOutput?, toWrite: Any?, clzInfo: FSTClazzInfo?, referencedBy: FSTClazzInfo.FSTFieldInfo?, streamPosition: Int) {
        out!!.writeStringUTF((toWrite as TileHandler).name)
    }

    override fun instantiate(objectClass: Class<*>?, fstObjectInput: FSTObjectInput?, serializationInfo: FSTClazzInfo?, referencee: FSTClazzInfo.FSTFieldInfo?, streamPosition: Int): TileHandler? {
        val y = TileRegistry.get(fstObjectInput!!.readStringUTF())
        fstObjectInput.registerObject(y, streamPosition, serializationInfo, referencee)
        return y
    }
}