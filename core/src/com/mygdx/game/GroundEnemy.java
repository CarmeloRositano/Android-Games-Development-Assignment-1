package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GroundEnemy{

    public enum EnemyState { MOVING, DYING, DEAD }
    private float constantSpeed = 100f;

    Sprite sprite;
    Vector2 delta;
    private TextureRegion currentFrame;

    //Enemy Walking
    private final Texture walkingTexture = new Texture("ground_enemy/moving.png"); //Set here to stop lag from loading texture from drive
    private Animation walkingAnimation;

    //Enemy Dying
    private final Texture dyingTexture = new Texture("ground_enemy/dying.png"); //Set here to stop lag from loading texture from drive
    private Animation dyingAnimation;
    float stateTime;

    EnemyState enemyState;
    public GroundEnemy(float x, float y) {
        enemyState = EnemyState.MOVING;
        stateTime = 0.0f;

        sprite = new Sprite();
        delta = new Vector2();

        //Walking
        int frameCol = 6;
        int frameRow = 3;

        sprite.setSize(walkingTexture.getWidth() / frameCol * 0.7f
                , walkingTexture.getHeight() / frameRow * 0.7f);

        TextureRegion[][] walkTemp = TextureRegion.split(walkingTexture, walkingTexture.getWidth() / frameCol,
                walkingTexture.getHeight() / frameRow);
        TextureRegion[] walkingFrames = new TextureRegion[frameCol * frameRow];
        int index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                walkingFrames[index++] = walkTemp[i][j];
            }
        }
        walkingAnimation = new Animation(1f/30f, (Object[]) walkingFrames);
        //Dying
        frameCol = 6;
        frameRow = 5;
        TextureRegion[][] dyingTemp = TextureRegion.split(dyingTexture, dyingTexture.getWidth() / frameCol,
                dyingTexture.getHeight() / frameRow);
        TextureRegion[] dyingFrames = new TextureRegion[(frameCol * frameRow) - 4];
       index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                if(index < 26)
                dyingFrames[index++] = dyingTemp[i][j];
            }
        }
        dyingAnimation = new Animation(1f/30f, (Object[]) dyingFrames);

        sprite.setPosition(x, y);

    }

    public void updateCurrentState(TiledGameMap gameMap) {

        stateTime += Gdx.graphics.getDeltaTime();

        switch (enemyState) {
            case MOVING:
                currentFrame = (TextureRegion) walkingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) dyingAnimation.getKeyFrame(stateTime, false);
                if(dyingAnimation.isAnimationFinished(stateTime)) {
                    enemyState = EnemyState.DEAD;
                    //make enemy move at the speed of the ground when dead
                    constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed * 1f)) * 0.05f) / Gdx.graphics.getDeltaTime();
                    break;
                }
                sprite.setRegion(currentFrame);
                break;

            case DEAD:
                //make enemy move at the speed of the ground when dead
                constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed * 1f)) * 0.05f) / Gdx.graphics.getDeltaTime();
                break;
        }
    }

    public void Move(float dt) {
        this.delta.x = -constantSpeed * dt;
        sprite.translate(delta.x, delta.y);
        if(enemyState == EnemyState.DYING) {
            sprite.translateY(-0.8f);
        }

    }

    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() * 0.15f,
                sprite.getY(),
                sprite.getWidth() * 0.7f,
                sprite.getHeight() * 0.6f);
    }


    public void draw(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    public void setDying() {
        enemyState = EnemyState.DYING;
        stateTime = 0f;
    }

    public void setAlive() {
        enemyState = EnemyState.MOVING;
        stateTime = 0f;
        constantSpeed = 100f;
    }

    public void dispose() {
        dyingTexture.dispose();
        walkingTexture.dispose();
    }

}
