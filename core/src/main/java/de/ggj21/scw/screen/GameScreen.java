package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.SuperCatWorldGame;
import de.ggj21.scw.world.GameWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameScreen extends ScreenAdapter {
    private static final Logger LOG = LogManager.getLogger(GameScreen.class);

    private final SuperCatWorldGame game;
    private GameWorld world;
    private SoundManager soundManager;


    public GameScreen(SuperCatWorldGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        final TiledMap map = new TmxMapLoader().load("levels/level1.tmx");
        this.soundManager = new SoundManager();
        this.world = new GameWorld(map, soundManager);
        Gdx.input.setInputProcessor(new InputMultiplexer(world.getInputProcessor(), Gdx.input.getInputProcessor()));
    }

    @Override
    public void resize(int width, int height) {
        world.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.render(delta);
    }

    @Override
    public void hide() {
        world.dispose();
        soundManager.dispose();
    }
}
