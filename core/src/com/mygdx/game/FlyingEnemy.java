package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class FlyingEnemy {

    public enum EnemyState { MOVING, DYING, DEAD, SHOOTING }
    private float constantSpeed = 100f;

    Sprite sprite;
    Vector2 delta;
    private TextureRegion currentFrame;

    //Enemy Walking
    private final Texture walkingTexture = new Texture("air_enemy/moving.png"); //Set here to stop lag from loading texture from drive
    private Animation walkingAnimation;

    //Enemy Dying
    private final Texture dyingTexture = new Texture("air_enemy/dying.png"); //Set here to stop lag from loading texture from drive
    private Animation dyingAnimation;

    //Enemy Shooting
    private final Texture shootingTexture = new Texture("air_enemy/shooting.png"); //Set here to stop lag from loading texture from drive
    private Animation shootingAnimation;
    boolean isShooting;
    boolean alreadyShot;

    //Bombs
    ArrayList<FlyingEnemyProjectile> bombs;

    float stateTime;

    EnemyState enemyState;

    public FlyingEnemy(float x, float y, ArrayList<FlyingEnemyProjectile> bombs) {
        enemyState = EnemyState.MOVING;
        stateTime = 0.0f;

        sprite = new Sprite();
        delta = new Vector2();

        this.bombs = bombs;

        //Walking
        int frameCol = 6;
        int frameRow = 3;

        sprite.setSize(walkingTexture.getWidth() / frameCol * 0.9f
                , walkingTexture.getHeight() / frameRow * 0.9f);

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
        frameCol = 4;
        frameRow = 9;
        TextureRegion[][] dyingTemp = TextureRegion.split(dyingTexture, dyingTexture.getWidth() / frameCol,
                dyingTexture.getHeight() / frameRow);
        TextureRegion[] dyingFrames = new TextureRegion[(frameCol * frameRow) - 1];
        index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                if(index < 35)
                    dyingFrames[index++] = dyingTemp[i][j];
            }
        }
        dyingAnimation = new Animation(1f/30f, (Object[]) dyingFrames);

        frameCol = 6;
        frameRow = 6;
        TextureRegion[][] shootingTemp = TextureRegion.split(shootingTexture, shootingTexture.getWidth() / frameCol,
                shootingTexture.getHeight() / frameRow);
        TextureRegion[] shootingFrames = new TextureRegion[(frameCol * frameRow) - 1];
        index = 0;
        for (int i = 0; i < frameRow; i++) {
            for (int j = 0; j < frameCol; j++) {
                if (index < 35)
                    shootingFrames[index++] = shootingTemp[i][j];
            }
        }
        shootingAnimation = new Animation(1f/30f, (Object[]) shootingFrames);

        isShooting = false;
        alreadyShot = false;

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

            case SHOOTING:
                currentFrame = (TextureRegion) shootingAnimation.getKeyFrame(stateTime, false);
                if(shootingAnimation.isAnimationFinished(stateTime)) {
                    enemyState = EnemyState.MOVING;
                    isShooting = false;
                    alreadyShot = false;
                    break;
                }
                sprite.setRegion(currentFrame);
                //Deploys bomb at frame 12 of shooting animation
                if(shootingAnimation.getKeyFrameIndex(stateTime) == 9 && !alreadyShot) {
                    alreadyShot = true;
                    bombs.add(new FlyingEnemyProjectile(getHitBox().getX() + getHitBox().getWidth() / 2,
                            getHitBox().getY()));
                }
                break;
        }
    }

    public void move(float dt, Player player) {
        if(isShooting) return;
        if(getHitBox().getX() < player.getHitBox().getX()
                && getHitBox().getX() + getHitBox().getWidth() > player.getHitBox().getX() + player.getHitBox().getWidth()) {

            shoot();
            return;
        }
        this.delta.x = constantSpeed * dt;
        sprite.translate(delta.x, delta.y);
        if(enemyState == EnemyState.DYING) {
            sprite.translateY(-5f);
        }

    }

    public void shoot() {
        if(isShooting) return;
        stateTime = 0f;
        isShooting = true;
        enemyState = EnemyState.SHOOTING;
    }

    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() / 3,
                            sprite.getY(),
                            sprite.getWidth() / 3,
                            sprite.getHeight());
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
