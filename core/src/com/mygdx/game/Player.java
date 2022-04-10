package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {

    public enum PlayerState { RUNNING, DYING, DEAD, SHOOTING }

    private static float MovementSpeed = 200.0f;
    private static float ConstantSpeed = 150.0f;
    private static final float GRAVITY = 70f;

    ShapeRenderer shapeRenderer;

    boolean isShooting;
    boolean canJump;

    PlayerState currentState;

    Sprite sprite;
    Vector2 delta;

    //Player - Walking
    Texture walkingTexture;
    private Animation<TextureRegion> walkingAnimation;
    //Dying
    Texture dyingTexture;
    private Animation<TextureRegion> dyingAnimation;
    //Shooting
    Texture shootingTexture;
    private Animation shootingAnimation;
    ArrayList<PlayerProjectile> bullets;

    //Game Clock
    float dt;
    float stateTime;
    private TextureRegion currentFrame;


    public Player(ArrayList<PlayerProjectile> bullets) {
        currentState = PlayerState.RUNNING;

        shapeRenderer = new ShapeRenderer();

        this.bullets = bullets;

        sprite = new Sprite();
        sprite.setSize(256,256);
        delta = new Vector2();

        //Player Walking Texture and Animation Build
        int FrameCol = 3;
        int FrameRow = 6;
        walkingTexture = new Texture("player/moving.png");
        TextureRegion[][] walkTemp = TextureRegion.split(walkingTexture, walkingTexture.getWidth() / FrameCol,
                walkingTexture.getHeight() / FrameRow);
        TextureRegion[] walkingFrames = new TextureRegion[FrameCol * FrameRow];
        int index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                walkingFrames[index++] = walkTemp[i][j];
            }
        }
        walkingAnimation = new Animation(1f/30f, (Object[]) walkingFrames);

        //Player Dying Texture and Animation Build
        FrameCol = 5;
        FrameRow = 4;
        dyingTexture = new Texture("player/dying.png");
        TextureRegion[][] dyingTemp = TextureRegion.split(dyingTexture, dyingTexture.getWidth() / FrameCol,
                dyingTexture.getHeight() / FrameRow);
        TextureRegion[] dyingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 18) {
                    dyingFrames[index++] = dyingTemp[i][j];
                }
            }
        }
        dyingAnimation = new Animation(1f/30f, (Object[]) dyingFrames);

        //player shooting Texture and Animation Build
        FrameCol = 3;
        FrameRow = 4;
        shootingTexture = new Texture("player/shooting.png");
        TextureRegion[][] shootingTemp = TextureRegion.split(shootingTexture, shootingTexture.getWidth() / FrameCol,
                shootingTexture.getHeight() / FrameRow);
        TextureRegion[] playerShootingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 10) {
                    playerShootingFrames[index++] = shootingTemp[i][j];
                }
            }
        }
        shootingAnimation = new Animation (1f/30f, (Object[]) playerShootingFrames);

        updateCurrentState();

        isShooting = false;
        canJump = false;
        dt = 0.0f;
        stateTime = 0.0f;
    }

    //Updates the currentPlayerState to determine what animation that player sprite should be in
    public void updateCurrentState() {

        stateTime += Gdx.graphics.getDeltaTime();

        switch (currentState) {
            case RUNNING:
                currentFrame = (TextureRegion) walkingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) dyingAnimation.getKeyFrame(stateTime, false);
                sprite.setRegion(currentFrame);
                if(dyingAnimation.isAnimationFinished(stateTime)) {
                    currentState = PlayerState.DEAD;
                }
                break;

            case DEAD:
                MovementSpeed = ConstantSpeed = 0;
                break;

            case SHOOTING:
                currentFrame = (TextureRegion) shootingAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(currentFrame);
                if(shootingAnimation.isAnimationFinished(stateTime)) {
                    currentState = PlayerState.RUNNING;
                    isShooting = false;
                };
                break;

        }
        sprite.setRegion(currentFrame);
    }

    //Moves the player
    public void move(int x, int y, TiledMapTileLayer collisionLayer, Camera camera) {

        //If player is within viewport
        if (sprite.getX() < camera.position.x - camera.viewportWidth * 0.5f) {
            sprite.setPosition(camera.position.x - camera.viewportWidth * 0.5f, sprite.getY());
            delta.x = 0f;
        } else if (sprite.getX() > (camera.position.x + camera.viewportWidth * 0.5f) - sprite.getWidth()) {
            sprite.setPosition((camera.position.x + camera.viewportWidth * 0.5f) - sprite.getWidth(), sprite.getY());
            delta.x = 0f;
        } else {
            this.delta.x = x * MovementSpeed * dt;
        }

        if(collidesBottom(collisionLayer)) {
            //Player jump
            if(y == 1 && currentState != PlayerState.DEAD && currentState != PlayerState.DYING) {
                //Mario Style arc jump
                stateTime = 0f;
                this.delta.y = (sprite.getY() - this.delta.y * dt) / 2;
            } else {
                this.delta.y = y * MovementSpeed * dt;
            }
        //Player is in the air (Applies gravity)
        } else {
            this.delta.y = (this.delta.y - GRAVITY * dt);
        }

        //Make sure player does not fall into ground
        if (sprite.getY() < 61) {
            sprite.setPosition(sprite.getX(), 61);
            //TODO Fix issue where player character would fall into ground after jump. Now has hard coded possition (61)
        }

        sprite.translate(this.delta.x, this.delta.y);
    }
    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < sprite.getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            if(isCellBlocked(sprite.getX() + step, sprite.getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getHitBox() {
        return new Rectangle(sprite.getX() + sprite.getWidth() * 0.3f,
                sprite.getY(),
                sprite.getWidth() * 0.54f,
                sprite.getHeight() * 0.8f);
    }

    public void shoot() {
        if(isShooting) return;
        if(bullets.size() == 4) return;
        bullets.add(new PlayerProjectile(sprite.getX() + sprite.getWidth() * 0.8f, (sprite.getY()) + (sprite.getHeight() * 0.42f)));
        stateTime = 0f;
        currentState = PlayerState.SHOOTING;
        isShooting = true;
    }

    public void setDying() {
        currentState = PlayerState.DYING;
        stateTime = 0f;
    }

    public void setAlive() {
        currentState = PlayerState.RUNNING;
        stateTime = 0f;
    }

    public void newGame() {
        MovementSpeed = 200.0f;
        ConstantSpeed = 150.0f;
    }

    public void dispose() {
        walkingTexture.dispose();
        dyingTexture.dispose();
        shootingTexture.dispose();
        shapeRenderer.dispose();
    }

    //Getters and Setters
    public static float getConstantSpeed() {
        return ConstantSpeed;
    }

}
