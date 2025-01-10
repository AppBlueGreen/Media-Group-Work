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
    private int playerX = WIDTH / 2;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 20;

    // 弾と敵のリスト
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();

    // ゲームのタイマー
    private Timer timer;

    public ShootingGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);

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
            enemies.add(new Enemy((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT / 2)));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // プレイヤーを描画
        g.setColor(Color.BLUE);
        g.fillOval(playerX + PLAYER_WIDTH / 2 - 5, HEIGHT - PLAYER_HEIGHT - 20, 20, 20);

        // 弾を描画
        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        // 敵を描画
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g.fillOval(enemy.x, enemy.y, enemy.size, enemy.size);
            moveTowardsPlayer(enemy);
        }
    }

    private void moveTowardsPlayer(Enemy enemy) {
        // プレイヤーの中心座標
        double playerCenterX = playerX + PLAYER_WIDTH / 2;
        double playerCenterY = HEIGHT - PLAYER_HEIGHT - 10;
    
        // 敵とプレイヤーの距離の差
        double dx = playerCenterX - enemy.x;
        double dy = playerCenterY - enemy.y;
    
        double distance = Math.sqrt(dx * dx + dy * dy);
        double speed = 1.5; // 敵の移動速度
        if (distance > 0) {
            enemy.x += (int) (dx / distance * speed);
            enemy.y += (int) (dy / distance * speed);
        } else { // game over
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 弾を動かす
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.y -= 5;
            if (bullet.y < 0) {
                bulletIterator.remove();
            }
        }

        // 敵と弾の衝突判定
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            for (Bullet bullet : bullets) {
                if (new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height)
                        .intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    enemyIterator.remove();
                    bullets.remove(bullet);
                    break;
                }
            }
        }

        // 再描画
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= 10;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += 10;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 弾を発射
            bullets.add(new Bullet(playerX + PLAYER_WIDTH / 2 - 5, HEIGHT - PLAYER_HEIGHT - 20));
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

// 弾のクラス
class Bullet {
    int x, y, width = 10, height = 20;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// 敵のクラス
class Enemy {
    int x, y, size = 30;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
