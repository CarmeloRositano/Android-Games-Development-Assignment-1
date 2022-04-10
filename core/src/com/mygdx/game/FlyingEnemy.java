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
    float stateTime;
    EnemyState enemyState;

    //Enemy Walking
    private final Texture walkingTexture = new Texture("air_enemy/moving.png"); //Set here to stop lag from loading texture from drive
    private final Animation<TextureRegion> walkingAnimation;

    //Enemy Dying
    private final Texture dyingTexture = new Texture("air_enemy/dying.png"); //Set here to stop lag from loading texture from drive
    private final Animation<TextureRegion> dyingAnimation;

    //Enemy Shooting
    private final Texture shootingTexture = new Texture("air_enemy/shooting.png"); //Set here to stop lag from loading texture from drive
    private final Animation<TextureRegion> shootingAnimation;
    boolean isShooting;
    boolean alreadyShot;

    //Bombs
    ArrayList<FlyingEnemyProjectile> bombs;

    /**
     * Creates a FlyingEnemy. sets its state to MOVING and initializes all variables.
     * Builds all animations that are used (Walking, Dying, and Shooting) and sets sprite position
     * @param x x coordinates for spawn
     * @param y y coordinates for spawn
     * @param bombs A ArrayList of bombs to be edited and viewed for when shooting
     */
    public FlyingEnemy(float x, float y, ArrayList<FlyingEnemyProjectile> bombs) {
        enemyState = EnemyState.MOVING;
        stateTime = 0.0f;

        sprite = new Sprite();
        delta = new Vector2();

        this.bombs = bombs;

        //Walking
        int frameCol = 6;
        int frameRow = 3;

        sprite.setSize(walkingTexture.getWidth() / (float) frameCol * 0.9f
                , walkingTexture.getHeight() / (float) frameRow * 0.9f);

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

    /**
     * Updates self depending on its current state as well as the game state. Updates the
     * animation so the enemy is in the correct animation. Times when the projectile to be synced
     * with the shooting animation.
     * @param gameMap game map to determine speed
     * @param gameState the state of the game to determine if enemy should be moving and how fast
     */
    public void updateCurrentState(TiledGameMap gameMap, MyGdxGame.GameState gameState) {
        if(gameState == MyGdxGame.GameState.PAUSED) return;
        stateTime += Gdx.graphics.getDeltaTime();

        switch (enemyState) {
            case MOVING:
                TextureRegion currentFrame = (TextureRegion) walkingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) dyingAnimation.getKeyFrame(stateTime, false);
                if(dyingAnimation.isAnimationFinished(stateTime)) {
                    enemyState = EnemyState.DEAD;
                    //make enemy move at the speed of the ground when dead
                    constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed)) * 0.05f) / Gdx.graphics.getDeltaTime();
                    break;
                }
                sprite.setRegion(currentFrame);
                break;

            case DEAD:
                //make enemy move at the speed of the ground when dead
                sprite.setPosition(sprite.getX() + Gdx.graphics.getWidth(), sprite.getY());
                constantSpeed = ((Player.getConstantSpeed() + (gameMap.timeElapsed)) * 0.05f) / Gdx.graphics.getDeltaTime();
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

    /**
     * Moves and determines when the player is underneath self allowing for a projectile to be shot
     * at the player. Moves downwards when in dying animation.
     * @param dt Delta Time to give smoother movement
     * @param player a player object to determine player hit box x, y for shooting and collision
     */
    public void move(float dt, Player player) {
        if(isShooting) return;
        if(getHitBox().getX() < player.getHitBox().getX()
                && getHitBox().getX() + getHitBox().getWidth() > player.getHitBox().getX() + player.getHitBox().getWidth()
                && player.currentState != Player.PlayerState.DEAD) {

            shoot();
            return;
        }
        this.delta.x = constantSpeed * dt;
        sprite.translate(delta.x, delta.y);
        if(enemyState == EnemyState.DYING) {
            sprite.translateY(-5f);
        }

    }

    /**
     * Sets self to shooting, which changes the animation to the shooting animation and setting
     * variables so enemy can only shoot one projectile per shooting animation.
     */
    public void shoot() {
        if(isShooting) return;
        stateTime = 0f;
        isShooting = true;
        enemyState = EnemyState.SHOOTING;
    }

    /**
     * Creates a custom hit box and returns it
     * @return  Rectangle that is the hit box
     */
    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() / 3,
                            sprite.getY(),
                            sprite.getWidth() / 3,
                            sprite.getHeight());
    }

    /**
     * Draws the sprite to screen on the provided Batch
     * @param batch The Batch to draw the text to screen
     */
    public void draw(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    /**
     * Sets the current state to dying
     */
    public void setDying() {
        enemyState = EnemyState.DYING;
        stateTime = 0f;
    }

    /**
     * Sets the current state to moving
     */
    public void setAlive() {
        enemyState = EnemyState.MOVING;
        stateTime = 0f;
        constantSpeed = 100f;
    }
    
    /**
     * Dispose of variables
     */
    public void dispose() {
        dyingTexture.dispose();
        walkingTexture.dispose();
    }

}
