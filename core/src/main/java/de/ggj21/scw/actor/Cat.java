package de.ggj21.scw.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cat implements GameActor {

    private final Vector2 position;
    private final Animation<TextureRegion> animation;
    private final float scale;

    float elapsedTime = 0;

    public Cat(final Vector2 startPosition, final float scale) {
        this.scale = scale;
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/cat.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 16, 16);
        TextureRegion[] animationFrames = frameSplit[0];
        animation = new Animation<TextureRegion>(0.35f, animationFrames);
        position = startPosition;
    }


    @Override
    public void update(float delta) {
        elapsedTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        final TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);
        batch.draw(currentFrame, position.x * scale, position.y * scale, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }
}
