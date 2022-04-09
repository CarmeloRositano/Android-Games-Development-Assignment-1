package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Random;


public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { MAIN_MENU, PLAYING, PAUSED, COMPLETE }

	GameState gameState;

	TiledGameMap gameMap;

	//Map and Rendering
	SpriteBatch batch;
	SpriteBatch uiBatch;
	OrthographicCamera camera;
	ShapeRenderer shapeRenderer;

	//Game world Objects
	MapLayer objectLayer;

	//Player
	Player player;

	//Enemy
	GroundEnemy groundEnemy;
	Random rand;

	//Bullet
	ArrayList<PlayerProjectile> bullets;

	//Background Textures
	Texture backgroundGround;

	//Storage class for collision
	Rectangle tileRectangle;

	//UI textures
	Texture buttonSquareTexture;
	Texture buttonSquareDownTexture;
	Texture buttonLongTexture;
	Texture buttonLongDownTexture;

	//UI Buttons
	Button moveLeftButton;
	Button moveRightButton;
	Button moveUpButton;
	Button shootButton;
	Button restartButton;
	Button startButton;
	Button exitButton;

	//Menu
	float menuDelay;

	//Text
	BitmapFont font;

	//Just use this to only restart when the restart button is released instead of immediately as it's pressed
	boolean restartActive;

	@Override
	public void create () {

		rand = new Random();

		shapeRenderer = new ShapeRenderer();

		//Camera
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920 * 0.8f, 1080 * 0.8f);
		camera.update();

		//Rendering
		bullets = new ArrayList<PlayerProjectile>();
		batch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		player = new Player(bullets);
		gameMap = new TiledGameMap();

		//Object Layer
		objectLayer = gameMap.tiledMap.getLayers().get("Objects");

		//UI Textures
		buttonSquareTexture = new Texture("GUI/buttonSquare_blue.png");
		buttonSquareDownTexture = new Texture("GUI/buttonSquare_beige_pressed.png");
		buttonLongTexture = new Texture("GUI/buttonLong_blue.png");
		buttonLongDownTexture = new Texture("GUI/buttonLong_beige_pressed.png");

		//Buttons
		moveLeftButton = new Button(0.0f, 0.0f, w * 0.2f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		moveRightButton = new Button(w * 0.2f , 0.0f, w * 0.2f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		moveUpButton = new Button(0.0f, h * 0.5f, w * 0.4f, h * 0.5f, buttonSquareTexture, buttonSquareDownTexture);
		shootButton = new Button(w * 0.6f, 0.0f, w * 0.4f, h * 1, buttonSquareTexture, buttonSquareDownTexture);
		restartButton = new Button(w * 0.05f, h * 0.6f, w * 0.425f, h * 0.2f, buttonLongTexture, buttonLongDownTexture);
		startButton = new Button(w * 0.05f, h * 0.6f, w * 0.425f, h * 0.2f, buttonLongTexture, buttonLongDownTexture);
		exitButton = new Button(w - (w * 0.425f) - (w * 0.05f), h * 0.6f, w * 0.425f, h * 0.2f, buttonLongTexture, buttonLongDownTexture);
		menuDelay = 0f;

		//Collision
		tileRectangle = new Rectangle();

		//Map Textures
		backgroundGround = new Texture("background/background_00.png");

		//Text
		font = new BitmapFont();

		newGame();
	}

	@Override
	public void render () {

		player.dt = Gdx.graphics.getDeltaTime();

		//Game World
		update();

		//Clear the screen every frame before drawing.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameMap.render(camera, gameState, player);

		//Player
		player.updateCurrentPlayerState();

		//Enemy
		groundEnemy.updateCurrentState(gameMap);

		//Apply camera and draw player
		batch.setProjectionMatrix(camera.combined);
		groundEnemy.draw(batch);
		batch.begin();
		player.playerSprite.draw(batch);
		batch.end();

		//Render Bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(batch);
		}

		//Draw UI
		uiBatch.begin();
		switch(gameState) {
			case MAIN_MENU:
				uiBatch.setColor(1,1,1,1);
				startButton.draw(uiBatch);
				startButton.addText("Start", uiBatch);
				exitButton.draw(uiBatch);
				exitButton.addText("Exit", uiBatch);
				break;
			case PLAYING:
				uiBatch.setColor(1, 1, 1, 0.3f);
				moveLeftButton.draw(uiBatch);
				moveRightButton.draw(uiBatch);
				moveUpButton.draw(uiBatch);
				shootButton.draw(uiBatch);
				break;
			case COMPLETE:
				uiBatch.setColor(1, 1, 1, 1);
				restartButton.draw(uiBatch);
				restartButton.addText("Restart", uiBatch);
				exitButton.draw(uiBatch);
				exitButton.addText("Exit", uiBatch);
				break;
		}
		uiBatch.end();

		ESPHitBoxView();

	}


	public void update() {

		//Touch Input Info
		boolean checkTouch = Gdx.input.isTouched();
		int touchX = Gdx.input.getX();
		int touchY = Gdx.input.getY();

		switch(gameState) {

			case MAIN_MENU:
				startButton.update(checkTouch, touchX, touchY);
				exitButton.update(checkTouch, touchX, touchY);

				if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || startButton.isDownPrev && !startButton.isDown) {
					gameState = GameState.PLAYING;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || exitButton.isDownPrev && !exitButton.isDown) {
					player.dispose();
					groundEnemy.dispose();
					for (int i = 0; i < bullets.size(); i++) {
						bullets.get(i).dispose();
						bullets.remove(i);
						i--;
					}
					gameMap.dispose();
					Gdx.app.exit();
				}
				break;

			case PLAYING:

				//Collision layer build
				MapLayer collisionLayer = gameMap.tiledMap.getLayers().get("collision");
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

				//Update Ground Enemy
				float tempMovement;
				if(player.currentPlayerState == Player.PlayerState.RUNNING) {
					tempMovement = player.dt;
					//Determine when to spawn enemy
					if(groundEnemy.groundEnemySprite.getX() < Gdx.graphics.getWidth() - groundEnemy.groundEnemySprite.getWidth()) {
						if(player.currentPlayerState == Player.PlayerState.DEAD) {
							groundEnemy.setDying();
						}
						groundEnemy.setAlive();
						groundEnemy.groundEnemySprite.setPosition((player.playerSprite.getX() + Gdx.graphics.getWidth() + groundEnemy.groundEnemySprite.getWidth()) + rand.nextInt(1000), 61);
					}
				} else {
					if(groundEnemy.groundEnemySprite.getX() < Gdx.graphics.getWidth() - groundEnemy.groundEnemySprite.getWidth()) {
						tempMovement = 0f;
					} else {
						tempMovement = player.dt * 4f;
					}
				}
				groundEnemy.groundEnemyMovement(tempMovement);


				//Update Player Bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).projectileMovement(player.dt);
					if(bullets.get(i).shouldRemove()) {
						bullets.remove(i);
						i--;
					}
				}


				//Collision Checks
				if (groundEnemy.groundEnemyState == GroundEnemy.GroundEnemyState.MOVING) {
					//Checking if bullets collide with enemy
					for (int i = 0; i < bullets.size(); i++) {
						if(bullets.get(i).getHitBox().overlaps(groundEnemy.getHitBox())) {
							groundEnemy.setDying();
							bullets.remove(i);
							i--;
						}
					}

					//Checking if enemy collides with player
					if(player.currentPlayerState == Player.PlayerState.RUNNING) {
						if(groundEnemy.getHitBox().overlaps(player.getHitBox())) {
							player.setDying();
							gameState = GameState.COMPLETE;
						}
					}
				}


				//Poll user for input
				moveLeftButton.update(checkTouch, touchX, touchY);
				moveRightButton.update(checkTouch, touchX, touchY);
				moveUpButton.update(checkTouch, touchX, touchY);
				shootButton.update(checkTouch, touchX, touchY);

				int moveX = 0;
				int moveY = 0;
				if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
					moveLeftButton.isDown = true;
					moveX -= 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
					moveRightButton.isDown = true;
					moveX += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.UP) || moveUpButton.isDown) {
					moveUpButton.isDown = true;
					moveY += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || shootButton.isDown) {
					player.shoot();
					shootButton.isDown = true;
					player.shoot();
				}

				//Character and Camera Movement
				player.movePlayer(moveX, moveY, tileLayer, camera);
				camera.update();

			case COMPLETE:

				//Update Ground Enemy
				if(player.currentPlayerState == Player.PlayerState.RUNNING) {
					tempMovement = player.dt;
					//Determine when to spawn enemy
					if(groundEnemy.groundEnemySprite.getX() < Gdx.graphics.getWidth() - groundEnemy.groundEnemySprite.getWidth()) {
						if(player.currentPlayerState == Player.PlayerState.DEAD) {
							groundEnemy.setDying();
						}
						groundEnemy.setAlive();
						groundEnemy.groundEnemySprite.setPosition((player.playerSprite.getX() + Gdx.graphics.getWidth() + groundEnemy.groundEnemySprite.getWidth()) + rand.nextInt(1000), 61);
					}
				} else {
					if(groundEnemy.groundEnemySprite.getX() < Gdx.graphics.getWidth() - groundEnemy.groundEnemySprite.getWidth()) {
						tempMovement = 0f;
					} else {
						tempMovement = player.dt * 4f;
					}
				}
				groundEnemy.groundEnemyMovement(tempMovement);


				//Update Player Bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).projectileMovement(player.dt);
					if(bullets.get(i).shouldRemove()) {
						bullets.remove(i);
						i--;
					}
				}

				//Buttons
				restartButton.update(checkTouch, touchX, touchY);
				exitButton.update(checkTouch, touchX, touchY);

				if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || restartButton.isDownPrev && !restartButton.isDown) {
					newGame();
					gameState = GameState.PLAYING;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || exitButton.isDownPrev && !exitButton.isDown) {
					player.dispose();
					groundEnemy.dispose();
					for (int i = 0; i < bullets.size(); i++) {
						bullets.get(i).dispose();
						bullets.remove(i);
						i--;
					}
					gameMap.dispose();
					Gdx.app.exit();
				}
				break;


		}

	}

	public void newGame() {
		gameState = GameState.MAIN_MENU;

		player.setAlive();
		player.newGame();
		player.dt = 0.0f;

		//Set player and camera starting location
		RectangleMapObject playerObject = (RectangleMapObject) objectLayer.getObjects().get("Player");
		player.playerSprite.setCenter(playerObject.getRectangle().x, (playerObject.getRectangle().y + (playerObject.getRectangle().getHeight() * 1.12f)));
		camera.position.x = player.playerSprite.getX() + player.playerSprite.getWidth()/2;
		camera.position.y = (1080 / 2) - 1080 * 0.1f ;

		//Enemy
		groundEnemy = new GroundEnemy(player.playerSprite.getX() + Gdx.graphics.getWidth() * 1.5f, player.playerSprite.getY());

		camera.update();

		restartActive = false;
	}

	private void ESPHitBoxView() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(0,1,0,1);
		shapeRenderer.rect(groundEnemy.getHitBox().getX(), groundEnemy.getHitBox().getY(),
				groundEnemy.getHitBox().getWidth(), groundEnemy.getHitBox().getHeight());
		shapeRenderer.rect(player.getHitBox().getX(), player.getHitBox().getY(),
				player.getHitBox().getWidth(), player.getHitBox().getHeight());
		for (int i = 0; i < bullets.size(); i++) {
			shapeRenderer.rect(bullets.get(i).getHitBox().getX(), bullets.get(i).getHitBox().getY(),
					bullets.get(i).getHitBox().getWidth(), bullets.get(i).getHitBox().getHeight());
		}
		shapeRenderer.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		player.playerTexture.dispose();
		buttonSquareTexture.dispose();
		buttonSquareDownTexture.dispose();
		buttonLongTexture.dispose();
		buttonLongDownTexture.dispose();
		backgroundGround.dispose();
	}
}
