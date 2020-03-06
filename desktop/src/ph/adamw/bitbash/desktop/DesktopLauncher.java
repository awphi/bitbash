package ph.adamw.bitbash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ph.adamw.bitbash.BitbashApplication;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;
		config.width = 1280;
		config.height = 960;
		new LwjglApplication(new BitbashApplication(), config);
	}
}
