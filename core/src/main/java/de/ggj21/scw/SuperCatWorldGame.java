package de.ggj21.scw;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.ggj21.scw.screen.MainMenu;

public class SuperCatWorldGame extends Game {

    private Skin skin;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        setScreen(new MainMenu(this));
    }

    public Skin getSkin() {
        return skin;
    }
}
