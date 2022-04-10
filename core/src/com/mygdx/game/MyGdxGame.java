package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

import java.util.ArrayList;
import java.util.Random;


public class MyGdxGame extends ApplicationAdapter {

	public enum GameState { MAIN_MENU, PLAYING, PAUSED, COMPLETE }

	GameState gameState;

	TiledGameMap gameMap;

	//Audio
	Music mainMenu;
	Music gamePlay;

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
	FlyingEnemy flyingEnemy;
	Random rand;

	//Bullet
	ArrayList<PlayerProjectile> bullets;

	//Flying Enemy Bombs
	ArrayList<FlyingEnemyProjectile> bombs;

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

		//Music
		mainMenu = Gdx.audio.newMusic(Gdx.files.internal("sounds/menu.mp3"));
		mainMenu.setVolume(.2f);
		mainMenu.setLooping(true);
		gamePlay = Gdx.audio.newMusic(Gdx.files.internal("sounds/gameplay.mp3"));
		gamePlay.setVolume(.2f);
		gamePlay.setLooping(true);

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
		player.updateCurrentState();

		//Enemy
		groundEnemy.updateCurrentState(gameMap);
		flyingEnemy.updateCurrentState(gameMap);

		//Apply camera and draw player
		batch.setProjectionMatrix(camera.combined);
		groundEnemy.draw(batch);
		flyingEnemy.draw(batch);
		batch.begin();
		player.sprite.draw(batch);
		batch.end();

		//Render Bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(batch);
		}

		//Render Flying Enemy Bombs
		for(int i = 0; i < bombs.size(); i++) {
			bombs.get(i).draw(batch);
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
		//Collision layer build
		MapLayer collisionLayer = gameMap.tiledMap.getLayers().get("collision");
		TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

		switch(gameState) {

			case MAIN_MENU:
				mainMenu.play();
				startButton.update(checkTouch, touchX, touchY);
				exitButton.update(checkTouch, touchX, touchY);

				if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || startButton.isDownPrev && !startButton.isDown
					&& gameState == GameState.MAIN_MENU) {
					gameState = GameState.PLAYING;
					mainMenu.stop();
				}
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || exitButton.isDownPrev && !exitButton.isDown
						&& gameState == GameState.MAIN_MENU) {
					player.dispose();
					groundEnemy.dispose();
					flyingEnemy.dispose();
					for (int i = 0; i < bullets.size(); i++) {
						bullets.get(i).dispose();
						bullets.remove(i);
						i--;
					}
					for ( int i = 0; i < bombs.size(); i++) {
						bombs.get(i).dispose();
						bombs.remove(i);
						i--;
					}
					gameMap.dispose();
					Gdx.app.exit();
				}
				break;

			case PLAYING:

				//Play Music
				gamePlay.play();

				//Update Player Bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).move(Gdx.graphics.getDeltaTime());
					if(bullets.get(i).shouldRemove(tileLayer)) {
						bullets.remove(i);
						i--;
					}
				}

				//Update Flying Enemy Bombs
				for (int i = 0; i < bombs.size(); i++) {
					bombs.get(i).move(Gdx.graphics.getDeltaTime());
					if(bombs.get(i).shouldRemove(tileLayer)) {
						bombs.remove(i);
						i--;
					}
				}

				//Update Enemies - Ground Enemy
				//Determine when to spawn enemy
				if(groundEnemy.sprite.getX() < Gdx.graphics.getWidth() - groundEnemy.sprite.getWidth()) {
					groundEnemy.setAlive();
					groundEnemy.sprite.setPosition((player.sprite.getX() + Gdx.graphics.getWidth() + groundEnemy.sprite.getWidth()) + rand.nextInt(1000),
												   61);
				} else {
					groundEnemy.move(Gdx.graphics.getDeltaTime());
				}


				//Flying Enemy
				if(flyingEnemy.sprite.getX() > camera.position.x + flyingEnemy.sprite.getWidth() * 2f) {
					flyingEnemy.setAlive();
					flyingEnemy.sprite.setPosition((player.sprite.getX() - Gdx.graphics.getWidth() - flyingEnemy.sprite.getWidth() - rand.nextInt(1000)),
													61 + player.sprite.getHeight() * 2f);
				} else {
					flyingEnemy.move(Gdx.graphics.getDeltaTime(), player);
				}


				//Collision Checks - Ground Enemy
				if (groundEnemy.enemyState == GroundEnemy.EnemyState.MOVING) {
					//Checking if bullets collide with enemy
					for (int i = 0; i < bullets.size(); i++) {
						if(bullets.get(i).getHitBox().overlaps(groundEnemy.getHitBox())) {
							groundEnemy.setDying();
							bullets.remove(i);
							i--;
						}
					}
					//Checking if enemy collides with player
					if(player.currentState == Player.PlayerState.RUNNING) {
						if(groundEnemy.getHitBox().overlaps(player.getHitBox())) {
							player.setDying();
							gameState = GameState.COMPLETE;
						}
					}
				}

				//Flying Enemy
				if (flyingEnemy.enemyState == FlyingEnemy.EnemyState.MOVING) {
					//Checking if bullets collide with enemy
					for (int i = 0; i < bullets.size(); i++) {
						if(bullets.get(i).getHitBox().overlaps(flyingEnemy.getHitBox())) {
							flyingEnemy.setDying();
							bullets.remove(i);
							i--;
						}
					}
					//Checking if enemy collides with player
					if(player.currentState == Player.PlayerState.RUNNING) {
						if(flyingEnemy.getHitBox().overlaps(player.getHitBox())) {
							player.setDying();
							gameState = GameState.COMPLETE;
						}
					}
				}

				//Check bomb collision with player
				for (int i = 0; i < bombs.size(); i++) {
					if(bombs.get(i).getHitBox().overlaps(player.getHitBox())) {
						player.setDying();
						gameState = GameState.COMPLETE;
					}
				}

				//Poll user for input
				moveLeftButton.update(checkTouch, touchX, touchY);
				moveRightButton.update(checkTouch, touchX, touchY);
				moveUpButton.update(checkTouch, touchX, touchY);
				shootButton.update(checkTouch, touchX, touchY);

				int moveX = 0;
				int moveY = 0;
				if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown
						&& gameState == GameState.PLAYING) {
					moveLeftButton.isDown = true;
					moveX -= 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown
						&& gameState == GameState.PLAYING) {
					moveRightButton.isDown = true;
					moveX += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.UP) || moveUpButton.isDown
						&& gameState == GameState.PLAYING) {
					moveUpButton.isDown = true;
					moveY += 1;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || shootButton.isDown
						&& gameState == GameState.PLAYING) {
					player.shoot();
					shootButton.isDown = true;
					player.shoot();
				}

				//Character and Camera Movement
				player.move(moveX, moveY, tileLayer, camera);
				camera.update();
				break;

			case COMPLETE:

				//Applies gravity to player when they die
				player.move(0,0,tileLayer, camera);

				//Update Player Bullets
				for (int i = 0; i < bullets.size(); i++) {
					bullets.get(i).move(player.dt);
					if(bullets.get(i).shouldRemove(tileLayer)) {
						bullets.remove(i);
						i--;
					}
				}

				//Update Flying Enemy Bombs
				for (int i = 0; i < bombs.size(); i++) {
					bombs.get(i).move(Gdx.graphics.getDeltaTime());
					if(bombs.get(i).shouldRemove(tileLayer)) {
						bombs.remove(i);
						i--;
					}
				}

				//Update Ground Enemy
				if(player.currentState == Player.PlayerState.DEAD
				|| player.currentState == Player.PlayerState.DYING) {
					if (groundEnemy.sprite.getX() < Gdx.graphics.getWidth() - groundEnemy.sprite.getWidth()) {
						groundEnemy.move(0f);
					} else {
						groundEnemy.move(Gdx.graphics.getDeltaTime() * 4f);
					}
				}

				//Update flying enemy
				if(player.currentState == Player.PlayerState.DEAD
						|| player.currentState == Player.PlayerState.DYING) {
					if (flyingEnemy.sprite.getX() < Gdx.graphics.getWidth() - flyingEnemy.sprite.getWidth()) {
						flyingEnemy.move(0f, player);
					} else {
						flyingEnemy.move(Gdx.graphics.getDeltaTime() * 4f, player);
					}
				}

				//Buttons
				restartButton.update(checkTouch, touchX, touchY);
				exitButton.update(checkTouch, touchX, touchY);

				if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || restartButton.isDownPrev && !restartButton.isDown
						&& gameState == GameState.COMPLETE) {
					newGame();
					gameState = GameState.PLAYING;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || exitButton.isDownPrev && !exitButton.isDown
						&& gameState == GameState.COMPLETE) {
					player.dispose();
					flyingEnemy.dispose();
					groundEnemy.dispose();
					for (int i = 0; i < bullets.size(); i++) {
						bullets.get(i).dispose();
						bullets.remove(i);
						i--;
					}
					for ( int i = 0; i < bombs.size(); i++) {
						bombs.get(i).dispose();
						bombs.remove(i);
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
		gameMap.newGame();
		player.dt = 0.0f;

		//Set player and camera starting location
		RectangleMapObject playerObject = (RectangleMapObject) objectLayer.getObjects().get("Player");
		player.sprite.setCenter(playerObject.getRectangle().x, (playerObject.getRectangle().y + (playerObject.getRectangle().getHeight() * 1.12f)));
		camera.position.x = player.sprite.getX() + player.sprite.getWidth()/2;
		camera.position.y = (1080 / 2) - 1080 * 0.1f ;

		//Enemy
		groundEnemy = new GroundEnemy(player.sprite.getX() + Gdx.graphics.getWidth() * 1.5f, player.sprite.getY());
		bombs = new ArrayList<FlyingEnemyProjectile>();
		flyingEnemy = new FlyingEnemy(player.sprite.getX() + -Gdx.graphics.getWidth() * 2f,
				61 + player.sprite.getHeight() * 2f, bombs);

		camera.update();

		restartActive = false;
	}

	private void ESPHitBoxView() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(0,1,0,1);
		shapeRenderer.rect(flyingEnemy.getHitBox().getX(), flyingEnemy.getHitBox().getY(),
				flyingEnemy.getHitBox().getWidth(), flyingEnemy.getHitBox().getHeight());
		shapeRenderer.rect(groundEnemy.getHitBox().getX(), groundEnemy.getHitBox().getY(),
				groundEnemy.getHitBox().getWidth(), groundEnemy.getHitBox().getHeight());
		shapeRenderer.rect(player.getHitBox().getX(), player.getHitBox().getY(),
				player.getHitBox().getWidth(), player.getHitBox().getHeight());
		for (int i = 0; i < bullets.size(); i++) {
			shapeRenderer.rect(bullets.get(i).getHitBox().getX(), bullets.get(i).getHitBox().getY(),
					bullets.get(i).getHitBox().getWidth(), bullets.get(i).getHitBox().getHeight());
		}
		for ( int i = 0; i < bombs.size(); i++) {
			shapeRenderer.rect(bombs.get(i).getHitBox().getX(), bombs.get(i).getHitBox().getY(),
					bombs.get(i).getHitBox().getWidth(), bombs.get(i).getHitBox().getHeight());
		}
		shapeRenderer.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		buttonSquareTexture.dispose();
		buttonSquareDownTexture.dispose();
		buttonLongTexture.dispose();
		buttonLongDownTexture.dispose();
		backgroundGround.dispose();
	}
}
