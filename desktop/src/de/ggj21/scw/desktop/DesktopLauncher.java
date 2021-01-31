package de.ggj21.scw.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.ggj21.scw.SuperCatWorldGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tonno Cato");
        config.setWindowedMode(1920, 1088);
        new Lwjgl3Application(new SuperCatWorldGame(), config);
    }
}
