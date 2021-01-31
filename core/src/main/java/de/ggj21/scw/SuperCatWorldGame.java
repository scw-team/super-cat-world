package de.ggj21.scw;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.ggj21.scw.screen.MainMenu;
import de.ggj21.scw.world.GameWorld;

public class SuperCatWorldGame extends Game {

    private Skin skin;
    private Viewport viewport;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);
        skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        viewport = new FitViewport(GameWorld.getCameraWidth(), GameWorld.getCameraHeight());
        setScreen(new MainMenu(this));
    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        super.resize(width, height);
    }

    public Skin getSkin() {
        return skin;
    }
}
