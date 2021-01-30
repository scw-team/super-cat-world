package de.ggj21.scw.actor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface GameActor {

    void update(float delta);

    void render(SpriteBatch batch);

}
