package de.ggj21.scw.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.ggj21.scw.SuperCatWorld;

public class MainMenu extends ScreenAdapter {

    private final SuperCatWorld game;
    private SpriteBatch batch;
    private Stage stage;

    public MainMenu(SuperCatWorld game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        final Skin skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        stage = new Stage();
        final Label titleLabel = new Label("Super Cat World", skin, "title");
        titleLabel.setX(100);
        titleLabel.setY(100);
        stage.addActor(titleLabel);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                return true;
            }
        });
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
