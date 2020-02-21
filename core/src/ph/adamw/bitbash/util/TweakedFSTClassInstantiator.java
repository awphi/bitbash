package ph.adamw.bitbash.util;

import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTDefaultClassInstantiator;
import org.nustaq.serialization.util.FSTUtil;
import sun.reflect.ReflectionFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Created by ruedi on 12.12.14.
 *  + Tweaked by awphi 07.09.19
 *
 * Valid for common x86 JDK's (not android)
 *
 */
public class TweakedFSTClassInstantiator extends FSTDefaultClassInstantiator {
	public Constructor findConstructorForSerializable(final Class clazz) {
		if (!Serializable.class.isAssignableFrom(clazz)) {
			// in case forceSerializable flag is present, just look for no-arg constructor
			return findConstructorForExternalize(clazz);
		}
		if ( FSTClazzInfo.BufferConstructorMeta) {
			Constructor constructor = constructorMap.get(clazz);
			if (constructor != null) {
				return constructor;
			}
		}


		Class currentClazz = clazz;
		while (!Serializable.class.isAssignableFrom(currentClazz)) {
			currentClazz = currentClazz.getSuperclass();
			if (currentClazz == null) {
				return null;
			}
		}

		try {
			Constructor c = currentClazz.getDeclaredConstructor((Class[]) null);
			int mods = c.getModifiers();
			if ((mods & Modifier.PRIVATE) != 0 ||
					((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0 &&
							!FSTUtil.isPackEq(clazz, currentClazz))) {
				return null;
			}
			c = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, c);
			c.setAccessible(true);

			if ( FSTClazzInfo.BufferConstructorMeta)
				constructorMap.put(clazz,c);
			return c;
		} catch (NoClassDefFoundError cle) {
			return null;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}


}
