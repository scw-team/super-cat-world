package de.ggj21.scw.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.ggj21.scw.SuperCatWorldGame;

public class MainMenu extends ScreenAdapter {

    private final SuperCatWorldGame game;
    private SpriteBatch batch;
    private Stage stage;

    public MainMenu(SuperCatWorldGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(game.getViewport());
        final Label titleLabel = new Label("Tonno Cato", game.getSkin(), "title");
        titleLabel.setX(100);
        titleLabel.setY(100);
        stage.addActor(titleLabel);
        final TextButton startGame = new TextButton("Start game", game.getSkin());
        startGame.setX(500);
        startGame.setY(500);
        startGame.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                return true;
            }
        });
        stage.addActor(startGame);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02734375f, 0.037109375f, 0.193359375f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void hide() {
        batch.dispose();
        stage.dispose();
    }
}
