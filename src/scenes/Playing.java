package scenes;

import static helps.Constants.Tiles.GRASS_TILE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import enemies.Enemy;
import helps.LoadSave;
import main.Game;
import managers.EnemyManager;
import managers.ProjectileManager;
import managers.TowerManager;
import managers.WaveManager;
import objects.PathPoint;
import objects.Tower;
import ui.ActionBar;

public class Playing extends GameScene implements SceneMethods {

	private int[][] level;
	private ActionBar actionBar;
	private int mouseX, mouseY;
	private EnemyManager enemyManager;
	private TowerManager towerManager;
	private ProjectileManager projectileManager;
	private WaveManager waveManager;
	private PathPoint start, end;
	private Tower selectedTower;
	private int coinTick;
	private boolean gamePaused = false;

	public Playing(Game game) {
		super(game);
		loadDefaultLevel();
		actionBar = new ActionBar(0, 640, 640, 80, this);
		enemyManager = new EnemyManager(this, start, end);
		towerManager = new TowerManager(this);
		projectileManager = new ProjectileManager(this);
		waveManager = new WaveManager(this);
	}

	private void loadDefaultLevel() {
		level = LoadSave.GetLevelData();
		ArrayList<PathPoint> points = LoadSave.getLevelPathPoint();
		start = points.get(0);
		end = points.get(1);
	}

	public void setLevel(int[][] level) {
		this.level = level;
	}

	public void update() {
		if (!gamePaused) {
			updateTick();
			waveManager.update();

			// gold tick
			coinTick++;
			if (coinTick % (60 * 3) == 0)
				actionBar.addCoin(1);

			if (isAllEnemiesDead()) {
				if (isThereMoreWaves()) {
					waveManager.startWaveTimer();
					if (isWaveTimerOver()) {
						waveManager.increaseWaveIndex();
						enemyManager.getEnemies().clear();
						waveManager.resetEnemyIndex();
					}
				}
			}
			if (isTimeForNewEnemy())
				if (!waveManager.isWaveTimerOver())
					spawnEnemy();
			enemyManager.update();
			towerManager.update();
			projectileManager.update();
		}
	}

	private boolean isWaveTimerOver() {

		return waveManager.isWaveTimerOver();
	}

	private boolean isThereMoreWaves() {
		return waveManager.isThereMoreWaves();
	}

	private boolean isAllEnemiesDead() {
		if (waveManager.isThereMoreEnemiesInWave()) {
			return false;
		}
		for (Enemy e : enemyManager.getEnemies()) {
			if (e.isAlive())
				return false;
		}
		return true;
	}

	private boolean isTimeForNewEnemy() {
		if (waveManager.isTimeForNewEnemy()) {
			if (waveManager.isThereMoreEnemiesInWave())
				return true;
		}
		return false;
	}

	private void spawnEnemy() {
		enemyManager.spawnEnemy(waveManager.getNextEnemy());
	}

	public void setSelectedTower(Tower selectedTower) {
		this.selectedTower = selectedTower;
	}

	@Override
	public void render(Graphics g) {
		drawLevel(g);
		actionBar.draw(g);
		enemyManager.draw(g);
		towerManager.draw(g);
		projectileManager.draw(g);
		drawSelectedTower(g);
		drawHighlight(g);
	}

	private void drawHighlight(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawRect(mouseX, mouseY, 32, 32);

	}

	private void drawSelectedTower(Graphics g) {
		if (selectedTower != null)
			g.drawImage(towerManager.getTowerImgs()[selectedTower.getTowerType()], mouseX, mouseY, null);
	}

	private void drawLevel(Graphics g) {
		for (int y = 0; y < level.length; y++) {
			for (int x = 0; x < level[y].length; x++) {
				int id = level[y][x];
				if (isAnimation(id)) {
					g.drawImage(getSprite(id, animationIndex), x * 32, y * 32, null);
				} else
					g.drawImage(getSprite(id), x * 32, y * 32, null);
			}
		}
	}

	public int getTileType(int x, int y) {
		int xCord = x / 32;
		int yCord = y / 32;
		if (xCord < 0 || xCord > 19)
			return 0;
		if (yCord < 0 || yCord > 19)
			return 0;
		int id = level[y / 32][x / 32];
		return game.getTileManager().getTile(id).getTileType();
	}

	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			selectedTower = null;
		}
	}

	@Override
	public void mouseClicked(int x, int y) {
		if (y >= 640) {
			actionBar.mouseClicked(x, y);
		} else {
			if (selectedTower != null) {
				if (isTileGrass(mouseX, mouseY)) {
					if (getTowerAt(mouseX, mouseY) == null) {
						towerManager.addTower(selectedTower, mouseX, mouseY);
						subtractCost(selectedTower.getTowerType());
						selectedTower = null;
					}
				}
			} else {
				Tower t = getTowerAt(mouseX, mouseY);
				actionBar.displayTower(t);
			}
		}
	}

	private void subtractCost(int towerType) {
		actionBar.payForTower(towerType);
	}

	public void removeTower(Tower displayedTower) {
		towerManager.removeTower(displayedTower);
	}

	public void upgradeTower(Tower displayedTower) {
		towerManager.upgradeTower(displayedTower);
	}

	private Tower getTowerAt(int x, int y) {
		return towerManager.getTowerAt(x, y);
	}

	private boolean isTileGrass(int x, int y) {
		int id = level[y / 32][x / 32];
		int tileType = game.getTileManager().getTile(id).getTileType();
		return tileType == GRASS_TILE;
	}

	public void shootEnemy(Tower t, Enemy e) {
		projectileManager.newProjectile(t, e);
	}

	@Override
	public void mouseMoved(int x, int y) {
		if (y >= 640 /* && !gamePaused */) {
			actionBar.mouseMoved(x, y);
		} else {
			mouseX = (x / 32) * 32;
			mouseY = (y / 32) * 32;
		}
	}

	@Override
	public void mousePressed(int x, int y) {
		if (y >= 640 /* && !gamePaused */) {
			actionBar.mousePressed(x, y);
		}
	}

	@Override
	public void mouseReleased(int x, int y) {
		actionBar.mouseReleased(x, y);
	}

	@Override
	public void mouseDragged(int x, int y) {

	}

	public void rewardPlayer(int enemyType) {
		actionBar.addCoin(helps.Constants.Enemies.GetReward(enemyType));
	}

	public TowerManager getTowerManager() {
		return towerManager;
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}

	public WaveManager getWaveManager() {
		return waveManager;
	}

	public boolean isGamePaused() {
		return gamePaused;
	}

	public void removeALife() {
		actionBar.removeALife();
	}

	public void resetEverything() {
		actionBar.resetEverything();

		// managers
		enemyManager.reset();
		towerManager.reset();
		projectileManager.reset();
		waveManager.reset();

		mouseX = 0;
		mouseY = 0;
		selectedTower = null;
		coinTick = 0;
		gamePaused = false;
	}

}
