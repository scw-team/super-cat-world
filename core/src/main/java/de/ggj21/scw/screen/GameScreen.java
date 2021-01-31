package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.SuperCatWorldGame;
import de.ggj21.scw.world.GameWorld;

public class GameScreen extends ScreenAdapter {
    private final SuperCatWorldGame game;
    private GameWorld world;
    private SoundManager soundManager;
    private Stage stage;
    private GameWorld.LevelState state = GameWorld.LevelState.Running;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;


    public GameScreen(SuperCatWorldGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        final TiledMap map = new TmxMapLoader().load("levels/level2.tmx");
        this.soundManager = new SoundManager();
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.world = new GameWorld(map, soundManager, game.getViewport(), spriteBatch, shapeRenderer);
        this.stage = new Stage(game.getViewport(), spriteBatch);
        Gdx.input.setInputProcessor(new InputMultiplexer(world.getInputProcessor(), stage));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setProjectionMatrix(world.getCamera().combined);
        shapeRenderer.setProjectionMatrix(world.getCamera().combined);
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
        dialog.show(stage);
    }

    private void showVictory() {
        final Dialog dialog = new Dialog("You've found Pixel!", game.getSkin()) {
            @Override
            protected void result(Object object) {
                game.setScreen(new MainMenu(game));
            }
        };
        dialog.text("You've found your lost friend Pixel!");
        dialog.button("Leave");
        final int width = 400;
        final int height = 100;
        dialog.setWidth(width);
        dialog.setHeight(height);
        final float x = (GameWorld.getCameraWidth() - width) / 2f;
        final float y = (GameWorld.getCameraHeight() - height) / 2f;
        dialog.setX(x);
        dialog.setY(y);
        stage.addActor(dialog);
    }

    @Override
    public void hide() {
        world.dispose();
        soundManager.dispose();
        stage.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
