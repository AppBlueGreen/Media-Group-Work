import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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

        int dy = player.y - y;

        if(dy < 200) {
            if (playerCenterX < enemyCenterX) {
                x -= 1;
            } else {
                x += 1;
            }
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

class Fellow {
    protected int x, y, size = 20;
    protected int difference;

    public Fellow(int x, int y, Player player) {
        this.x = x;
        this.y = y;
        difference = (int)Math.abs(player.x - x);
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(x, y, size, size);
    }

    public void moveFellow(Player player) {
        x = player.x + difference;
    }
}

class PlusWall {
    protected int width, x, y;
    protected int x1, x2;
    protected int plusNumber;

    public PlusWall(int WIDTH, int y) {
        width = WIDTH / 2;
        this.y = y;
        x = (int)(Math.random() * width) + 1;
        x1 = x;
        x2 = x + width;
        // plusNumber = (int)(Math.random() * 5) + 1;
        plusNumber = 1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawLine(x1, y, x2, y);
    }

    public void move() {
        y++;
    }
}

class MinusWall {
    protected int width, x, y, x1, x2;
    protected int minusNumber;

    public MinusWall(int WIDTH, int y) {
        width = WIDTH / 2;
        this.y = y;
        x = (int)(Math.random() * width) + 1;
        x1 = x;
        x2 = x + width;
        minusNumber = 1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(x1, y, x2, y);
    }

    public void move() {
        y++;
    }
}

////////////////////////////////////////////////
// Model (M)

class ShootingModel {
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Fellow> fellows = new ArrayList<>();
    private ArrayList<PlusWall> plusWalls = new ArrayList<>();
    private ArrayList<MinusWall> minusWalls = new ArrayList<>();
    private Player player;

    public static final int WIDTH = 400;
    public static final int HEIGHT = 800;

    public ShootingModel() {
        player = new Player(WIDTH / 2 - 10, HEIGHT - 100);
        spawnEnemies();
        spawnPlusWall();
        spawnMinusWall();
    }

    // 出現に関するメソッド
    public void spawnEnemies() {
        for (int i = 0; i < 10; i++) {
            enemies.add(new Enemy((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT / 2)));
        }
    }

    public void spawnPlusWall() {
        for (int i = 0; i < 2; i++) {
            plusWalls.add(new PlusWall(WIDTH, (int)(Math.random() * HEIGHT / 2)));
        }
    }

    public void spawnMinusWall() {
        for (int i = 0; i < 2; i++) {
            minusWalls.add(new MinusWall(WIDTH, (int)(Math.random() * HEIGHT / 2)));
        }
    }

    public void spawnFellows(int number) {
        for (int i = 0; i < number; i++) {

            int fellowX = getPlayer().x + (int)(Math.random() * 201) -100;
            int fellowY = getPlayer().y + (int)(Math.random() * 50) + 1;
            fellows.add(new Fellow(fellowX, fellowY, getPlayer()));
        }
    }

    // 値取得に関するメソッド
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Fellow> getFellows() {
        return fellows;
    }

    public ArrayList<PlusWall> getPlusWall() {
        return plusWalls;
    }

    public ArrayList<MinusWall> getMinusWall() {
        return minusWalls;
    }

    // 移動に関するメソッド
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

    public void movePlusWalls() {
        plusWalls.removeIf(plusWall -> {
            plusWall.move();
            return plusWall.y > HEIGHT;
        });
    }

    public void moveMinusWalls() {
        minusWalls.removeIf(minusWall -> {
            minusWall.move();
            return minusWall.y > HEIGHT;
        });
    }

    // 衝突判定に関するメソッド

    public void checkCollisions() { // 弾と敵の衝突判定
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

    public void checkPlusWallCollisions() { // playerとplusWallの衝突判定
        for (PlusWall plusWall : plusWalls) {
            if (new Rectangle(player.x, player.y, player.size, player.size).intersects(new Rectangle(plusWall.x1, plusWall.y, plusWall.x2 - plusWall.x1, 1))) {
                spawnFellows(plusWall.plusNumber);
                break;
            }
        }
    }

    public void checkMinusWallCollisions() { // playerとminusWallの衝突判定
        for (MinusWall minusWall : minusWalls) {
            if (new Rectangle(player.x, player.y, player.size, player.size).intersects(new Rectangle(minusWall.x1, minusWall.y, minusWall.x2 - minusWall.x1, 1))) {
                
                if (fellows.size() == 0)
                    break;
                fellows.remove(minusWall.minusNumber);
                break;
            }
        }
    }

    public void checkFellowCollisions() { // enemyとfellowの衝突判定
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            for (Fellow fellow : fellows) {
                if (new Rectangle(fellow.x, fellow.y, fellow.size, fellow.size)
                        .intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    enemyIterator.remove();
                    fellows.remove(fellow);
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

        for (PlusWall plusWall : model.getPlusWall()) {
            plusWall.draw(g);
        }

        for (MinusWall minusWall : model.getMinusWall()) {
            minusWall.draw(g);
        }

        for (Fellow fellow : model.getFellows()) {
            fellow.draw(g);
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
        model.movePlusWalls();
        model.moveMinusWalls();

        model.checkCollisions();
        model.checkPlusWallCollisions();
        model.checkFellowCollisions();
        model.checkMinusWallCollisions();

        view.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player player = model.getPlayer();
        ArrayList<Fellow> fellows = model.getFellows();

        if (e.getKeyCode() == KeyEvent.VK_LEFT && player.x > 0) {
            player.x -= 5;
            for (Fellow fellow : fellows) {
                fellow.moveFellow(player);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && player.x < ShootingModel.WIDTH - player.size) {
            player.x += 5;
            for (Fellow fellow : fellows) {
                fellow.moveFellow(player);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            model.getBullets().add(new Bullet(player.x + player.size / 2 - 2, player.y));
            for (Fellow fellow : fellows) {
                model.getBullets().add(new Bullet(fellow.x + fellow.size / 2 - 2, fellow.y));
            }
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