package ui;

import static main.GameStates.GAMEOVER;
import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;

import helps.Constants.Towers;
import objects.Tower;
import scenes.Playing;

public class ActionBar extends Bar {

	private MyButton bMenu, bPause;
	private Playing playing;
	private MyButton[] towerButtons;
	private Tower selectedTower;
	private Tower displayedTower;
	private MyButton sellTower, upgradeTower;
	private DecimalFormat formatter;
	private int coin = 500;
	private boolean showTowerCost;
	private int towerType;
	private int lives = 20;

	public ActionBar(int x, int y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		formatter = new DecimalFormat("0.0");
		initButtons();
	}

	public void resetEverything() {
		lives = 20;
		towerType = 0;
		showTowerCost = false;
		coin = 500;
		selectedTower = null;
		displayedTower = null;
	}

	private void initButtons() {
		bMenu = new MyButton("Menu", 2, 642, 100, 30);
		bPause = new MyButton("Pause", 2, 642 + 32, 100, 30);
		towerButtons = new MyButton[3];

		int w = 36, h = 36;
		int xStart = 110, yStart = 642;
		int xOffset = (int) (w * 1.1f);

		for (int i = 0; i < towerButtons.length; i++) {
			towerButtons[i] = new MyButton("", xStart + xOffset * i, yStart, w, h, i);
		}
		sellTower = new MyButton("Sell", 522, 692, 40, 22);
		upgradeTower = new MyButton("Upgrade", 566, 692, 62, 22);
	}

	public void removeALife() {
		lives--;
		if (lives <= 0)
			SetGameState(GAMEOVER);
	}

	private void drawButtons(Graphics g) {
		bMenu.draw(g);
		bPause.draw(g);
		for (MyButton b : towerButtons) {
			g.setColor(Color.gray);
			g.fillRect(b.x, b.y, b.width, b.height);
			g.drawImage(playing.getTowerManager().getTowerImgs()[b.getId()], b.x, b.y, b.width, b.height, null);
			drawButtonFeedback(g, b);
		}
	}

	public void draw(Graphics g) {
		g.setColor(new Color(220, 123, 15));
		g.fillRect(x, y, width, height);
		drawButtons(g);
		drawDisplayedTower(g);
		drawWaveInfo(g);
		drawCoinInfo(g);
		if (showTowerCost)
			drawTowerCostInfo(g);
		if (playing.isGamePaused()) {
			g.setColor(Color.blue);
			g.drawString("Game is paused!", 375, 660 + 16 * 3);
		}
		// lives
		g.setColor(Color.black);
		g.drawString("Lives: " + lives, 110, 667 + 15 * 3);
	}

	private void drawTowerCostInfo(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(240, 642, 90, 36);
		g.setColor(Color.black);
		g.drawRect(240, 642, 90, 36);

		g.drawString("" + getTowerName() + "!", 246, 657);
		g.drawString("Cost: " + getTowerCost() + "c", 246, 657 + 16);

		// show if cannot buy
		if (!isThereEnoughCoin(towerType)) {
			g.setColor(Color.gray);
			g.fillRect(240, 680, 100, 20);
			g.setColor(Color.red);
			g.drawRect(240, 680, 100, 20);
			g.drawString("Cannot Buy!", 246, 662 + 16 * 2);
		}
	}

	private int getTowerCost() {
		return helps.Constants.Towers.GetTowerCost(towerType);
	}

	private String getTowerName() {
		return helps.Constants.Towers.GetName(towerType);
	}

	private void drawCoinInfo(Graphics g) {
		g.drawString("Coin: " + coin + "c", 110, 666 + 15 * 2);
	}

	private void drawWaveInfo(Graphics g) {
		g.setColor(Color.black);
		g.setFont(new Font("LucidaSans", Font.BOLD, 13));
		drawWaveTimerInfo(g);
		drawEnemiesLeftInfo(g);
		drawWavesLeftInfo(g);
	}

	private void drawWavesLeftInfo(Graphics g) {
		int current = playing.getWaveManager().getWaveIndex();
		int size = playing.getWaveManager().getWaves().size();
		g.drawString("Wave: " + (current + 1) + " / " + size, 375, 660 + 16);
	}

	private void drawEnemiesLeftInfo(Graphics g) {
		int remaining = playing.getEnemyManager().getCountOfLiveEnemies();
		g.drawString("Enemies Left: " + remaining, 375, 660 + 16 * 2);
	}

	private void drawWaveTimerInfo(Graphics g) {
		if (playing.getWaveManager().isWaveTimerStarted()) {
			float timeLeft = playing.getWaveManager().getTimeLeft();
			String formattedText = formatter.format(timeLeft);
			g.drawString("Time Left: " + formattedText, 375, 660);
		}
	}

	private void drawDisplayedTower(Graphics g) {
		if (displayedTower != null) {
			g.setColor(Color.gray);
			g.fillRect(513, 642, 124, 76);
			g.setColor(Color.black);
			g.drawRect(513, 642, 124, 76);
			g.drawRect(523, 652, 32, 32);
			g.drawImage(playing.getTowerManager().getTowerImgs()[displayedTower.getTowerType()], 523, 652, 32, 32,
					null);
			g.setColor(Color.black);
			g.setFont(new Font("LucidaSans", Font.BOLD, 11));
			g.drawString("" + Towers.GetName(displayedTower.getTowerType()), 563, 659);
			g.drawString("ID: " + displayedTower.getId(), 563, 671);
			g.drawString("Tier: " + displayedTower.getTier(), 563, 683);

			drawDisplayedTowerBorder(g);
			drawDisplayedTowerRange(g);
			g.setFont(new Font("LucidaSans", Font.BOLD, 11));
			sellTower.draw(g);
			drawButtonFeedback(g, sellTower);

			if (displayedTower.getTier() < 3 && coin >= getUpgradeCost(displayedTower)) {
				upgradeTower.draw(g);
				drawButtonFeedback(g, upgradeTower);
			}

			if (sellTower.isMouseOver()) {
				g.setColor(Color.green);
				g.drawString("+" + getSellAmount(displayedTower) + "c", 110 + 75, 666 + 15 * 2);
			} else if (upgradeTower.isMouseOver() && coin >= getUpgradeCost(displayedTower)) {
				g.setColor(Color.blue);
				g.drawString("-" + getUpgradeCost(displayedTower) + "c", 110 + 75, 666 + 15 * 2);
			}
		}
	}

	private int getUpgradeCost(Tower displayedTower) {
		return (int) (helps.Constants.Towers.GetTowerCost(displayedTower.getTowerType()) * 0.3f);
	}

	private int getSellAmount(Tower displayedTower) {
		int upgradeCost = (displayedTower.getTier() - 1) * getUpgradeCost(displayedTower);
		upgradeCost *= 0.5f;
		return helps.Constants.Towers.GetTowerCost(displayedTower.getTowerType()) / 2 + upgradeCost;
	}

	private void drawDisplayedTowerRange(Graphics g) {
		g.setColor(Color.white);
		g.drawOval(displayedTower.getX() + 16 - (int) (displayedTower.getRange() * 2) / 2,
				displayedTower.getY() + 16 - (int) (displayedTower.getRange() * 2) / 2,
				(int) displayedTower.getRange() * 2, (int) displayedTower.getRange() * 2);
	}

	private void drawDisplayedTowerBorder(Graphics g) {
		g.setColor(Color.CYAN);
		g.drawRect(displayedTower.getX(), displayedTower.getY(), 32, 32);
	}

	public void displayTower(Tower t) {
		displayedTower = t;
	}

	private void sellTowerClicked() {
		playing.removeTower(displayedTower);
		coin += helps.Constants.Towers.GetTowerCost(displayedTower.getTowerType()) / 2;
		int upgradeCost = (displayedTower.getTier() - 1) * getUpgradeCost(displayedTower);
		upgradeCost *= 0.5f;
		coin += upgradeCost;
		displayedTower = null;
	}

	private void upgradeTowerClicked() {
		playing.upgradeTower(displayedTower);
		coin -= getUpgradeCost(displayedTower);
	}

	private void togglePause() {
		playing.setGamePaused(!playing.isGamePaused());
		if (playing.isGamePaused())
			bPause.setText("Unpause");
		else
			bPause.setText("Pause");
	}

	public void mouseClicked(int x, int y) {
		if (bMenu.getBounds().contains(x, y))
			SetGameState(MENU);
		else if (bPause.getBounds().contains(x, y))
			togglePause();
		else {
			if (displayedTower != null) {
				if (sellTower.getBounds().contains(x, y)) {
					sellTowerClicked();
					return;
				} else if (upgradeTower.getBounds().contains(x, y) && displayedTower.getTier() < 3
						&& coin >= getUpgradeCost(displayedTower)) {
					upgradeTowerClicked();
					return;
				}
			}
			for (MyButton b : towerButtons) {
				if (b.getBounds().contains(x, y)) {
					if (!isThereEnoughCoin(b.getId()))
						return;
					selectedTower = new Tower(0, 0, -1, b.getId());
					playing.setSelectedTower(selectedTower);
					return;
				}
			}
		}
	}

	private boolean isThereEnoughCoin(int towerType) {
		return coin >= helps.Constants.Towers.GetTowerCost(towerType);
	}

	public void mouseMoved(int x, int y) {
		bMenu.setMouseOver(false);
		bPause.setMouseOver(false);
		showTowerCost = false;
		sellTower.setMouseOver(false);
		upgradeTower.setMouseOver(false);
		for (MyButton b : towerButtons)
			b.setMouseOver(false);
		if (bMenu.getBounds().contains(x, y))
			bMenu.setMouseOver(true);
		else if (bPause.getBounds().contains(x, y))
			bPause.setMouseOver(true);
		else {
			if (displayedTower != null) {
				if (sellTower.getBounds().contains(x, y)) {
					sellTower.setMouseOver(true);
					return;
				} else if (upgradeTower.getBounds().contains(x, y) && displayedTower.getTier() < 3) {
					upgradeTower.setMouseOver(true);
					return;
				}
			}
			for (MyButton b : towerButtons)
				if (b.getBounds().contains(x, y)) {
					b.setMouseOver(true);
					showTowerCost = true;
					towerType = b.getId();
					return;
				}
		}
	}

	public void mousePressed(int x, int y) {
		if (bMenu.getBounds().contains(x, y))
			bMenu.setMousePressed(true);
		else if (bPause.getBounds().contains(x, y))
			bPause.setMousePressed(true);
		else {
			if (displayedTower != null) {
				if (sellTower.getBounds().contains(x, y)) {
					sellTower.setMousePressed(true);
					return;
				} else if (upgradeTower.getBounds().contains(x, y) && displayedTower.getTier() < 3) {
					upgradeTower.setMousePressed(true);
					return;
				}
			}
			for (MyButton b : towerButtons)
				if (b.getBounds().contains(x, y)) {
					b.setMousePressed(true);
					return;
				}
		}
	}

	public void mouseReleased(int x, int y) {
		bMenu.resetBooleans();
		bPause.resetBooleans();
		for (MyButton b : towerButtons)
			b.resetBooleans();
		sellTower.resetBooleans();
		upgradeTower.resetBooleans();
	}

	public void payForTower(int towerType) {
		this.coin -= helps.Constants.Towers.GetTowerCost(towerType);
	}

	public void addCoin(int getReward) {
		this.coin += getReward;
	}

	public int getLives() {
		return lives;
	}

}
