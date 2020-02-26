package ph.adamw.bitbash.util

import com.badlogic.gdx.utils.Json
import org.nustaq.serialization.*
import ph.adamw.bitbash.BitbashApplication

object JsonFSTSerializer : FSTBasicObjectSerializer() {
    override fun writeObject(out: FSTObjectOutput?, toWrite: Any?, clzInfo: FSTClazzInfo?, referencedBy: FSTClazzInfo.FSTFieldInfo?, streamPosition: Int) {
        val st = BitbashApplication.JSON.toJson(toWrite)
        out!!.writeStringUTF(st)
    }

    override fun alwaysCopy(): Boolean {
        return true
    }

    override fun readObject(into: FSTObjectInput?, toRead: Any?, clzInfo: FSTClazzInfo?, referencedBy: FSTClazzInfo.FSTFieldInfo?) {}

    override fun instantiate(objectClass: Class<*>?, fstObjectInput: FSTObjectInput?, serializationInfo: FSTClazzInfo?, referencee: FSTClazzInfo.FSTFieldInfo?, streamPosition: Int): Any? {
        val y = BitbashApplication.JSON.fromJson(serializationInfo!!.clazz, fstObjectInput!!.readStringUTF())
        fstObjectInput.registerObject(y, streamPosition, serializationInfo, referencee)
        return y
    }
}