import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

class Bullet {
    protected int x, y, width = 5, height = 10;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    public void move() {
        y -= 5;
    }
}

class Enemy {
    protected int x, y, size = 20;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size, size);
    }

    public void moveTowardsPlayer(Player player) {
        int playerCenterX = player.x + player.size / 2;
        int playerCenterY = player.y + player.size / 2;

        int enemyCenterX = x + size / 2;
        int enemyCenterY = y + size / 2;

        if (playerCenterX < enemyCenterX) {
            x -= 1;
        } else {
            x += 1;
        }
        if (playerCenterY < enemyCenterY) {
            y -= 1;
        } else {
            y += 1;
        }
    }
}

class Player {
    protected int x, y, size = 20;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, size, size);
    }
}

////////////////////////////////////////////////
// Model (M)

class ShootingModel {
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Player player;

    public static final int WIDTH = 400;
    public static final int HEIGHT = 800;

    public ShootingModel() {
        player = new Player(WIDTH / 2 - 10, HEIGHT - 40);
        spawnEnemies();
    }

    public void spawnEnemies() {
        for (int i = 0; i < 10; i++) {
            enemies.add(new Enemy((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT / 2)));
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Player getPlayer() {
        return player;
    }

    public void moveBullets() {
        bullets.removeIf(bullet -> {
            bullet.move();
            return bullet.y < 0;
        });
    }

    public void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveTowardsPlayer(player);
        }
    }

    public void checkCollisions() {
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
    }
}

////////////////////////////////////////////////
// View (V)

class ViewPanel extends JPanel {
    private ShootingModel model;

    public ViewPanel(ShootingModel model) {
        this.model = model;
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        model.getPlayer().draw(g);

        for (Bullet bullet : model.getBullets()) {
            bullet.draw(g);
        }

        for (Enemy enemy : model.getEnemies()) {
            enemy.draw(g);
        }
    }
}

////////////////////////////////////////////////
// Controller (C)

class Controller implements KeyListener, ActionListener {
    private ShootingModel model;
    private ViewPanel view;
    private javax.swing.Timer timer;

    public Controller(ShootingModel model, ViewPanel view) {
        this.model = model;
        this.view = view;
        this.timer = new javax.swing.Timer(20, this);
        this.timer.start();
        view.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        model.moveBullets();
        model.updateEnemies();
        model.checkCollisions();
        view.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player player = model.getPlayer();
        if (e.getKeyCode() == KeyEvent.VK_LEFT && player.x > 0) {
            player.x -= 10;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && player.x < ShootingModel.WIDTH - player.size) {
            player.x += 10;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            model.getBullets().add(new Bullet(player.x + player.size / 2 - 2, player.y));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

//////////////////////////////////////////////////
// main class

public class ShootingGame {
    public static void main(String[] args) {
        ShootingModel model = new ShootingModel();
        ViewPanel view = new ViewPanel(model);

        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(ShootingModel.WIDTH, ShootingModel.HEIGHT);
        frame.add(view);
        frame.setVisible(true);

        new Controller(model, view);
    }
}
