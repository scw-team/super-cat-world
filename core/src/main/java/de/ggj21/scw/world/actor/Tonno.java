package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelperFactory;
import de.ggj21.scw.world.GameWorld;
import de.ggj21.scw.world.actor.effect.StatusEffect;

public class Tonno extends AbstractActor {

    private static final float RESPAWN_CD = 5f;

    private final Animation<TextureRegion> idleAnimation;

    private float eatenForSeconds = 0f;

    public Tonno(final Vector2 startPosition,
                 final CollisionHelperFactory collisionHelperFactory,
                 SoundManager soundManager, final float worldScale) {
        super(startPosition, collisionHelperFactory, soundManager,
                65, 58, 0, 0,
                false, worldScale, GameWorld.VIEWPORT_SCALE / 2f);
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/tonno.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 65, 58);
        TextureRegion[] animationFrames = frameSplit[0];
        idleAnimation = new Animation<>(0.35f, animationFrames);
    }

    @Override
    Animation<TextureRegion> getActiveAnimation() {
        return idleAnimation;
    }

    @Override
    public void interactWith(GameActor otherActor) {
        if (!isDead()) {
            otherActor.addStatusEffect(StatusEffect.tonno());
            positionAndCondition.getSoundManager().playSound(SoundManager.Sounds.Yum);
            eatenForSeconds = 0;
            kill();
        }
    }

    @Override
    public void update(float delta) {
        if (positionAndCondition.isDead()) {
            eatenForSeconds += delta;
            if (eatenForSeconds >= RESPAWN_CD) {
                positionAndCondition.revive();
            }
        }
        super.update(delta);
    }
}
