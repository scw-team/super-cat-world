package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.world.CollisionHelperFactory;

public class Cat extends AbstractActor {

    public Cat(final Vector2 startPosition,
               final CollisionHelperFactory collisionHelperFactory,
               final float scale) {
        super(startPosition, collisionHelperFactory, 16, 16, scale);
    }

    @Override
    Animation<TextureRegion> getAnimation() {
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/cat.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 16, 16);
        TextureRegion[] animationFrames = frameSplit[0];
        return new Animation<TextureRegion>(0.35f, animationFrames);
    }
}
