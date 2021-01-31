package de.ggj21.scw.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import de.ggj21.scw.SuperCatWorldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
//		TexturePacker.Settings settings = new TexturePacker.Settings();
//		settings.maxWidth = 512;
//		settings.maxHeight = 512;
//		settings.paddingX = 1;
//		settings.paddingY = 1;
//		TexturePacker.process(settings, "images", "atlas", "game");

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Super Cat World");
		config.setWindowedMode(1920, 1088);
		new Lwjgl3Application(new SuperCatWorldGame(), config);
	}
}
