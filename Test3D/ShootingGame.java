package Test3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ShootingGame extends JPanel implements ActionListener, KeyListener {
    // ウィンドウのサイズ
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // プレイヤーの位置とサイズ
    private Player player;
    private Vec playerPos = new Vec(WIDTH / 2, HEIGHT - 40);
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 20;

    // 弾と敵のリスト
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Ray> walls1 = new ArrayList<>();

    // ゲームのタイマー
    private Timer timer;

    public ShootingGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

        walls1.add(new Ray(new Vec(350, 350), new Vec(500, 350).sub(new Vec(350, 350))));
        walls1.add(new Ray(new Vec(350, 350), new Vec(350, 500).sub(new Vec(350, 350))));

        this.player = new Player(playerPos, -Math.PI / 2);
        // タイマーとイベントリスナーの設定
        timer = new Timer(15, this);
        timer.start();
        this.setFocusable(true);
        this.addKeyListener(this);

        // 敵を生成
        spawnEnemies();
    }

    private void spawnEnemies() {
        for (int i = 0; i < 10; i++) {
            // Vec enemy = new Vec(Math.random() * WIDTH, Math.random() * HEIGHT / 2);
            enemies.add(new Enemy(new Vec(Math.random() * WIDTH, Math.random() * HEIGHT / 2)));
            // enemies.add(new Ray(enemy, enemy));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // プレイヤーを描画
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) player.getPos().getX(), (int) player.getPos().getY(), 20, 20);

        // 壁1を描画
        // g2d.setColor(Color.WHITE);
        // g2d.setStroke(new BasicStroke(3));
        // for (Ray wall : walls1) {
        //     g2d.drawLine((int) wall.getBegin().getX(), (int) wall.getBegin().getY(),
        //                  (int) wall.getEnd(1).getX(), (int) wall.getEnd(1).getY());
        // }
        // 弾を描画
        g2d.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g2d.fillRect((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height);
        }

        // 敵を描画
        g2d.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g2d.fillOval((int) enemy.pos.getX(), (int) enemy.pos.getY(), enemy.size, enemy.size);
            enemy.pos = enemy.pos.add(new Vec(0, 1)); // 敵を下に動かす
        }

        double fov = Math.PI / 2;
        double beamStep = fov / getWidth();
        for (double angle = player.getAngle() - fov / 2; angle < player.getAngle() + fov / 2; angle += beamStep) {
            draw3DWalls(g2d, player, angle, fov, enemies, bullets);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 弾を動かす
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.pos = bullet.pos.add(new Vec(0, -5));
            if (bullet.pos.getY() < 0) {
                bulletIterator.remove();
            }
        }

        // 敵と弾の衝突判定
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            for (Bullet bullet : bullets) {
                if (new Rectangle((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height)
                        .intersects(new Rectangle((int) enemy.pos.getX(), (int) enemy.pos.getY(), enemy.size, enemy.size))) {
                    enemyIterator.remove();
                    bullets.remove(bullet);
                    break;
                }
            }
        }

        // 再描画
        repaint();
    }

    private void draw3DWalls(Graphics2D g2d, Player player, double angle, double fov, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets) {
        ArrayList<WallHit> wallHits = new ArrayList<>();
    
        // for (Ray wall : walls1) {
        //     Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
        //     Vec hit = beam.intersection(wall);
        //     if (hit != null) {
        //         double wallDist = hit.sub(player.getPos()).mag();
        //         double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
        //         int brightness = (int) Math.max(0, Math.min(255, 255 - wallPerpDist * 10));
        //         // wallHits.add(new WallHit(hit, wallPerpDist, new Color(brightness, brightness, brightness), 1));
        //         wallHits.add(new WallHit(hit, wallPerpDist, Color.RED, 1));
        //     }
        // }
        for (Enemy enemy : enemies) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(enemy.pos, new Vec(10, 0));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, Color.RED, 2));
            }
        }

        for (Bullet bullet : bullets) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(bullet.pos, new Vec(0, 10));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, Color.YELLOW, 3));
            }
        }
    
        wallHits.sort((a, b) -> Double.compare(b.distance, a.distance));
    
        for (WallHit wallHit : wallHits) {
            int screenCenterY = HEIGHT / 2;
            int wallHeight = (int) Math.min(HEIGHT, 3000 / wallHit.distance);
            int wallY1 = screenCenterY + wallHeight / 2;
            int wallY2 = screenCenterY - wallHeight / 2;
            if(wallHit.wallNumber == 3){
                wallY1 = screenCenterY + wallHeight / 20;
                wallY2 = screenCenterY - wallHeight / 20;
            }
    
            g2d.setColor(wallHit.color);
            g2d.drawLine((int) (getWidth() / 2 + (angle - player.getAngle()) * getWidth() / fov), wallY1,
                         (int) (getWidth() / 2 + (angle - player.getAngle()) * getWidth() / fov), wallY2);
        }
    }
    

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerPos.getX() > 0) {
            player.setPos(new Vec(player.getPos().getX() - 10, player.getPos().getY()));
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerPos.getX() < WIDTH) {
            player.setPos(new Vec(player.getPos().getX() + 10, player.getPos().getY()));
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 弾を発射
            bullets.add(new Bullet(new Vec(player.getPos().getX() + 2, player.getPos().getY() - 10)));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        ShootingGame gamePanel = new ShootingGame();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

// Vec クラス
class Vec {
    private double x;
    private double y;

    Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vec add(Vec b) {
        return new Vec(this.x + b.x, this.y + b.y);
    }

    Vec sub(Vec b) {
        return new Vec(this.x - b.x, this.y - b.y);
    }

    Vec mult(double s) {
        return new Vec(this.x * s, this.y * s);
    }

    double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    double getX() {
        return this.x;
    }

    double getY() {
        return this.y;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }
}

// 弾のクラス
class Bullet {
    Vec pos;
    int width = 10;
    int height = 20;

    public Bullet(Vec pos) {
        this.pos = pos;
    }
}

class Player {
    private Vec pos;
    private double angle;

    Player(Vec pos, double angle) {
        this.pos = pos;
        this.angle = angle;
    }

    Vec getPos() {
        return this.pos;
    }

    void setPos(Vec pos) {
        this.pos = pos;
    }

    double getAngle() {
        return this.angle;
    }

    void setAngle(double angle) {
        this.angle = angle;
    }
}
// 敵のクラス
class Enemy {
    Vec pos;
    int size = 30;

    public Enemy(Vec pos) {
        this.pos = pos;
    }
}
class WallHit {
    Vec hitPoint;        // 壁との交差点
    double distance;     // プレイヤーから交差点までの距離
    Color color;         // 壁の色
    int wallNumber;      // 壁の番号

    public WallHit(Vec hitPoint, double distance, Color color, int wallNumber) {
        this.hitPoint = hitPoint;
        this.distance = distance;
        this.color = color;
        this.wallNumber = wallNumber;
    }
}
class Ray {
    private Vec pos;
    private Vec dir;

    Ray(Vec pos, Vec dir) {
        this.pos = pos;
        this.dir = dir;
    }

    Vec getBegin() {
        return this.pos;
    }

    Vec getEnd(double maxDistance) {
        return this.pos.add(this.dir.mult(maxDistance)); // maxDistanceに基づいて終点を計算
    }

    Vec intersection(Ray other) {
        double x1 = this.pos.getX(), y1 = this.pos.getY();
        double x2 = this.getEnd(1000).getX(), y2 = this.getEnd(1000).getY();
        double x3 = other.pos.getX(), y3 = other.pos.getY();
        double x4 = other.getEnd(1).getX(), y4 = other.getEnd(1).getY();

        
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(denom) < 1e-9) return null; // 平行の場合

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom;

        if (t >= 0 && u >= 0 && u <= 1) {
            return new Vec(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
        }
        return null;
    }
}
