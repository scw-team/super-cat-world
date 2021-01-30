package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
    private Stage stage;
    private GameWorld.LevelState state = GameWorld.LevelState.Running;


    public GameScreen(SuperCatWorldGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        final TiledMap map = new TmxMapLoader().load("levels/level1.tmx");
        this.soundManager = new SoundManager();
        this.world = new GameWorld(map, soundManager);
        this.stage = new Stage();
        Gdx.input.setInputProcessor(new InputMultiplexer(world.getInputProcessor(), stage));
    }

    @Override
    public void resize(int width, int height) {
        world.getViewport().update(width, height);
        stage.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // when there's a high delta (e.g. because of a render pause) we might fall through the ground
        // to fix this, the update is done in multiple increments in this case
        float appliedDelta = 0;
        do {
            float deltaToApply = Math.min(0.1f, delta);
            world.update(deltaToApply);
            appliedDelta += deltaToApply;
        } while (appliedDelta < delta);
        if (world.getState() != state) {
            if (world.getState() == GameWorld.LevelState.Lost) {
                showGameOver();
            } else if (world.getState() == GameWorld.LevelState.Won) {
                showVictory();
            }
            state = world.getState();
        }
        world.render();
        stage.act(delta);
        stage.draw();
    }

    private void showGameOver() {
        final Dialog dialog = new Dialog("Game Over", game.getSkin()) {
            @Override
            protected void result(Object object) {
                game.setScreen(new MainMenu(game));
            }
        };
        dialog.text("This journey was too dangerous for you :(");
        dialog.button("Leave");
        final int width = 400;
        final int height = 100;
        dialog.setWidth(width);
        dialog.setHeight(height);
        dialog.setX((Gdx.graphics.getWidth() - width) / 2f);
        dialog.setY((Gdx.graphics.getHeight() - height) / 2f);
        stage.addActor(dialog);
    }

    private void showVictory() {
        final Dialog dialog = new Dialog("You've found Pixel!", game.getSkin()) {
            @Override
            protected void result(Object object) {
                game.setScreen(new MainMenu(game));
            }
        };
        dialog.text("You've lost & found your beloved friend Pixel");
        dialog.button("Leave");
        final int width = 400;
        final int height = 100;
        dialog.setWidth(width);
        dialog.setHeight(height);
        dialog.setX((Gdx.graphics.getWidth() - width) / 2f);
        dialog.setY((Gdx.graphics.getHeight() - height) / 2f);
        stage.addActor(dialog);
    }

    @Override
    public void hide() {
        world.dispose();
        soundManager.dispose();
        stage.dispose();
    }
}
