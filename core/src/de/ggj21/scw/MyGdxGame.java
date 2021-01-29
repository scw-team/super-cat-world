package de.ggj21.scw;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private Stage stage;
	private Skin skin;

	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
		stage = new Stage();
		final Label titleLabel = new Label("Super Cat World", skin, "title");
		titleLabel.setX(100);
		titleLabel.setY(100);
		stage.addActor(titleLabel);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.02734375f, 0.037109375f, 0.193359375f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		stage.dispose();
	}
}
