package de.ggj21.scw.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.ggj21.scw.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Super Cat World");
		config.setWindowedMode(1920, 1080);
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
