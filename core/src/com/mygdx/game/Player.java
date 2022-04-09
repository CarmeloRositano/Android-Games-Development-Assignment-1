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

    PlayerState currentPlayerState;

    Sprite playerSprite;

    Texture playerTexture;
    Vector2 playerDelta;
    Rectangle playerDeltaRectangle;

    //Player - Walking
    Texture playerWalkingTexture;
    private Animation<TextureRegion> playerWalkingAnimation;
    //Dying
    Texture playerDyingTexture;
    private Animation<TextureRegion> playerDyingAnimation;
    //Shooting
    Texture playerShootingTexture;
    ArrayList<PlayerProjectile> bullets;
    private Animation playerShootingAnimation;

    //Game Clock
    float dt;
    float stateTime;
    private TextureRegion currentFrame;


    public Player(ArrayList<PlayerProjectile> bullets) {
        currentPlayerState = PlayerState.RUNNING;

        shapeRenderer = new ShapeRenderer();

        this.bullets = bullets;

        playerSprite = new Sprite();
        playerSprite.setSize(256,256);
        playerDelta = new Vector2();
        playerDeltaRectangle = new Rectangle(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());

        //Player Walking Texture and Animation Build
        int FrameCol = 3;
        int FrameRow = 6;
        playerWalkingTexture = new Texture("player/moving.png");
        TextureRegion[][] walkTemp = TextureRegion.split(playerWalkingTexture, playerWalkingTexture.getWidth() / FrameCol,
                playerWalkingTexture.getHeight() / FrameRow);
        TextureRegion[] playerWalkingFrames = new TextureRegion[FrameCol * FrameRow];
        int index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                playerWalkingFrames[index++] = walkTemp[i][j];
            }
        }
        playerWalkingAnimation = new Animation(1f/30f, (Object[]) playerWalkingFrames);

        //Player Dying Texture and Animation Build
        FrameCol = 5;
        FrameRow = 4;
        playerDyingTexture = new Texture("player/dying.png");
        TextureRegion[][] dyingTemp = TextureRegion.split(playerDyingTexture, playerDyingTexture.getWidth() / FrameCol,
                playerDyingTexture.getHeight() / FrameRow);
        TextureRegion[] playerDyingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 18) {
                    playerDyingFrames[index++] = dyingTemp[i][j];
                }
            }
        }
        playerDyingAnimation = new Animation(1f/30f, (Object[]) playerDyingFrames);

        //player shooting Texture and Animation Build
        FrameCol = 3;
        FrameRow = 4;
        playerShootingTexture = new Texture("player/shooting.png");
        TextureRegion[][] shootingTemp = TextureRegion.split(playerShootingTexture, playerShootingTexture.getWidth() / FrameCol,
                playerShootingTexture.getHeight() / FrameRow);
        TextureRegion[] playerShootingFrames = new TextureRegion[(FrameCol * FrameRow) - 2];
        index = 0;
        for (int i = 0; i < FrameRow; i++) {
            for (int j = 0; j < FrameCol; j++) {
                if(index < 10) {
                    playerShootingFrames[index++] = shootingTemp[i][j];
                }
            }
        }
        playerShootingAnimation = new Animation (1f/30f, (Object[]) playerShootingFrames);

        updateCurrentPlayerState();

        isShooting = false;
        canJump = false;
        dt = 0.0f;
        stateTime = 0.0f;
    }

    //Updates the currentPlayerState to determine what animation that player sprite should be in
    public void updateCurrentPlayerState() {

        stateTime += Gdx.graphics.getDeltaTime();

        switch (currentPlayerState) {
            case RUNNING:
                currentFrame = (TextureRegion) playerWalkingAnimation.getKeyFrame(stateTime, true);
                playerSprite.setRegion(currentFrame);
                break;

            case DYING:
                currentFrame = (TextureRegion) playerDyingAnimation.getKeyFrame(stateTime, false);
                playerSprite.setRegion(currentFrame);
                if(playerDyingAnimation.isAnimationFinished(stateTime)) {
                    currentPlayerState = PlayerState.DEAD;
                }
                break;

            case DEAD:
                MovementSpeed = ConstantSpeed = 0;
                break;

            case SHOOTING:
                currentFrame = (TextureRegion) playerShootingAnimation.getKeyFrame(stateTime, true);
                playerSprite.setRegion(currentFrame);
                if(playerShootingAnimation.isAnimationFinished(stateTime)) {
                    currentPlayerState = PlayerState.RUNNING;
                    isShooting = false;
                };
                break;

        }
        playerSprite.setRegion(currentFrame);
    }

    //Moves the player
    public void movePlayer(int x, int y, TiledMapTileLayer collisionLayer, Camera camera) {

        //If player is within viewport
        if(currentPlayerState == PlayerState.DYING || currentPlayerState == PlayerState.DEAD) return;
        if (playerSprite.getX() < camera.position.x - camera.viewportWidth * 0.5f) {
            playerSprite.setPosition(camera.position.x - camera.viewportWidth * 0.5f, playerSprite.getY());
            playerDelta.x = 0f;
        } else if (playerSprite.getX() > (camera.position.x + camera.viewportWidth * 0.5f) - playerSprite.getWidth()) {
            playerSprite.setPosition((camera.position.x + camera.viewportWidth * 0.5f) - playerSprite.getWidth(), playerSprite.getY());
            playerDelta.x = 0f;
        } else {
            this.playerDelta.x = x * MovementSpeed * dt;
            playerDeltaRectangle.x += (x * MovementSpeed * dt);
        }

        if(collidesBottom(collisionLayer)) {
            //Player jump
            if(y == 1 && currentPlayerState != PlayerState.DEAD && currentPlayerState != PlayerState.DYING) {
                //Mario Style arc jump
                stateTime = 0f;
                this.playerDelta.y = (playerSprite.getY() - this.playerDelta.y * dt) / 2;
            } else {
                this.playerDelta.y = y * MovementSpeed * dt;
            }
        //Player is in the air (Applies gravity)
        } else {
            this.playerDelta.y = (this.playerDelta.y - GRAVITY * dt);
        }

        //Make sure player does not fall into ground
        if (playerSprite.getY() < 61) {
            playerSprite.setPosition(playerSprite.getX(), 61);
            //TODO Fix issue where player character would fall into ground after jump. Now has hard coded possition (61)
        }

        playerSprite.translate(this.playerDelta.x, this.playerDelta.y);
    }
    private boolean isCellBlocked(float x, float y, TiledMapTileLayer collisionLayer) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null;
    }

    public boolean collidesBottom(TiledMapTileLayer collisionLayer) {
        for(float step = 0; step < playerSprite.getWidth(); step += collisionLayer.getTileWidth() / 2f) {
            if(isCellBlocked(playerSprite.getX() + step, playerSprite.getY(), collisionLayer)) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getHitBox() {
        return new Rectangle(playerSprite.getX() + playerSprite.getWidth() * 0.3f,
                playerSprite.getY(),
                playerSprite.getWidth() * 0.54f,
                playerSprite.getHeight() * 0.8f);
    }

    public void shoot() {
        if(isShooting) return;
        if(bullets.size() == 4) return;
        bullets.add(new PlayerProjectile(playerSprite.getX() + playerSprite.getWidth() * 0.8f, (playerSprite.getY()) + (playerSprite.getHeight() * 0.42f)));
        stateTime = 0f;
        currentPlayerState = PlayerState.SHOOTING;
        isShooting = true;
    }

    public void setDying() {
        currentPlayerState = PlayerState.DYING;
        stateTime = 0f;
    }

    public void setAlive() {
        currentPlayerState = PlayerState.RUNNING;
        stateTime = 0f;
    }

    public void newGame() {
        MovementSpeed = 200.0f;
        ConstantSpeed = 150.0f;
    }

    public void dispose() {
        playerWalkingTexture.dispose();
        playerDyingTexture.dispose();
        playerTexture.dispose();
        playerShootingTexture.dispose();
        shapeRenderer.dispose();
    }

    //Getters and Setters
    public static float getConstantSpeed() {
        return ConstantSpeed;
    }

}
