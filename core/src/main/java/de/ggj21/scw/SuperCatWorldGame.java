package de.ggj21.scw;

import com.badlogic.gdx.Game;
import de.ggj21.scw.screen.MainMenu;

public class SuperCatWorldGame extends Game {


    @Override
    public void create() {
        setScreen(new MainMenu(this));
    }
}
