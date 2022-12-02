package managers;

import static helps.Constants.Projectiles.ARROW;
import static helps.Constants.Projectiles.BOMB;
import static helps.Constants.Projectiles.CHAINS;
import static helps.Constants.Towers.ARCHER;
import static helps.Constants.Towers.CANON;
import static helps.Constants.Towers.WIZARD;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import enemies.Enemy;
import helps.LoadSave;
import objects.Projectile;
import objects.Tower;
import scenes.Playing;

public class ProjectileManager {

	private Playing playing;
	private ArrayList<Projectile> projectiles = new ArrayList<>();
	private ArrayList<Explosion> explosions = new ArrayList<>();
	private BufferedImage[] projImgs, exploImgs;
	private int projId = 0;

	// temporary variables
//	private boolean callTrue;
//	private long lastCall;

	public ProjectileManager(Playing playing) {
		this.playing = playing;
		importImgs();
	}

	private void importImgs() {
		BufferedImage atlas = LoadSave.getSpriteAtlas();
		projImgs = new BufferedImage[3];
		for (int i = 0; i < 3; i++) {
			projImgs[i] = atlas.getSubimage((7 + i) * 32, 32, 32, 32);
		}
		importExplosion(atlas);
	}

	private void importExplosion(BufferedImage atlas) {
		exploImgs = new BufferedImage[7];
		for (int i = 0; i < 7; i++) {
			exploImgs[i] = atlas.getSubimage(i * 32, 64, 32, 32);
		}
	}

	public void newProjectile(Tower t, Enemy e) {
		int type = getProjectileType(t);
		int dx = (int) (t.getX() - e.getX());
		int dy = (int) (t.getY() - e.getY());
		int totDist = Math.abs(dx) + Math.abs(dy);
		float xPer = Math.abs(dx) / (float) totDist;
		float xSpeed = xPer * helps.Constants.Projectiles.GetSpeed(type);
		float ySpeed = helps.Constants.Projectiles.GetSpeed(type) - xSpeed;

		if (t.getX() > e.getX())
			xSpeed *= -1;
		if (t.getY() > e.getY())
			ySpeed *= -1;

		float rotate = 0;
		if (type == ARROW) {
			float arcVal = (float) Math.atan(dy / (float) dx);
			rotate = (float) Math.toDegrees(arcVal);

			if (dx < 0)
				rotate += 180;
		}
		for (Projectile p : projectiles)
			if (!p.isActive() && p.getProjectileType() == type) {
				p.reuse(t.getX() + 16, t.getY() + 16, xSpeed, ySpeed, t.getDmg(), rotate);
				return;
			}
		projectiles
				.add(new Projectile(t.getX() + 16, t.getY() + 16, xSpeed, ySpeed, t.getDmg(), rotate, projId++, type));
	}

	public void update() {
		for (Projectile p : projectiles)
			if (p.isActive()) {
				p.move();
				if (isProjectileHittingEnemy(p)) {
					p.setActive(false);
					if (p.getProjectileType() == BOMB) {
						explosions.add(new Explosion(p.getPos()));
						explodeOnEnemies(p);
					}
				} else if (isProjectileOutsideBounds(p)) {
					p.setActive(false);
				}
			}
		for (Explosion e : explosions)
			if (e.getIndex() < 7)
				e.update();
	}

	private void explodeOnEnemies(Projectile p) {
		for (Enemy e : playing.getEnemyManager().getEnemies()) {
			if (e.isAlive()) {
				float radius = 40.0f;
				float dx = Math.abs(p.getPos().x - e.getX());
				float dy = Math.abs(p.getPos().y - e.getY());
				float realDist = (float) Math.hypot(dx, dy);
				if (realDist <= radius)
					e.hurt(p.getDamage());
			}
		}
	}

	private boolean isProjectileHittingEnemy(Projectile p) {
		for (Enemy e : playing.getEnemyManager().getEnemies()) {
			if (e.isAlive())
				if (e.getBounds().contains(p.getPos())) {
					e.hurt(p.getDamage());
					if (p.getProjectileType() == CHAINS)
						e.slow();
					return true;
				}
		}
		return false;
	}

	private boolean isProjectileOutsideBounds(Projectile p) {
		if (p.getPos().x >= 0 && p.getPos().x <= 640 && p.getPos().y >= 0 && p.getPos().y <= 720)
			return false;
		return true;
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for (Projectile p : projectiles)
			if (p.isActive()) {
				if (p.getProjectileType() == ARROW) {
					g2d.translate(p.getPos().x, p.getPos().y);
					g2d.rotate(Math.toRadians(p.getRotation()));
					g2d.drawImage(projImgs[p.getProjectileType()], -16, -16, null);
					g2d.rotate(-Math.toRadians(p.getRotation()));
					g2d.translate(-p.getPos().x, -p.getPos().y);
				} else {
					g2d.drawImage(projImgs[p.getProjectileType()], (int) p.getPos().x - 16, (int) p.getPos().y - 16,
							null);
				}
			}
		drawExplosions(g2d);
	}

	private void drawExplosions(Graphics2D g2d) {
		for (Explosion e : explosions)
			if (e.getIndex() < 7)
				g2d.drawImage(exploImgs[e.getIndex()], (int) e.getPos().x - 16, (int) e.getPos().y - 16, null);
	}

	private int getProjectileType(Tower t) {
		switch (t.getTowerType()) {
		case ARCHER:
			return ARROW;
		case CANON:
			return BOMB;
		case WIZARD:
			return CHAINS;
		}
		return 0;
	}

	public class Explosion {

		private Point2D.Float pos;
		private int exploTick = 0, exploIndex = 0;

		public Explosion(Point2D.Float pos) {
			this.pos = pos;

		}

		public void update() {
			exploTick++;
			if (exploTick >= 6) {
				exploTick = 0;
				exploIndex++;
			}
		}

		public int getIndex() {
			return exploIndex;
		}

		public Point2D.Float getPos() {
			return pos;
		}

	}

	public void reset() {
		projectiles.clear();
		explosions.clear();
		projId = 0;
	}

}
