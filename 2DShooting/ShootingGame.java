import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
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
    protected int x, y, size;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) { }

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

class SmallEnemy extends Enemy {

    int HP = 3;

    public SmallEnemy(int x, int y) {
        super(x, y);
        size = 20;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval(x, y, size, size);
    }
}

class Boss extends Enemy{

    int HP = 30;

    public Boss (int x, int y) { 
        super(x, y);
        size = 30;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size, size);
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

class Wall {
    protected int width, y;
    protected int x1, x2;

    public Wall(int WIDTH, int x, int y) {
        width = WIDTH / 2;
        x1 = x;
        x2 = x1 + width;
        this.y = y;
    }

    public void draw(Graphics g) { }

    public void move() { y++;}
}



class PlusWall extends Wall{

    protected int plusNumber;

    public PlusWall(int WIDTH, int x, int y) {
        super(WIDTH, x, y);
        plusNumber = (int)(Math.random() * 5) + 1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(x1, y, x2, y);
    }
}

class MinusWall extends Wall{

    protected int minusNumber;

    public MinusWall(int WIDTH, int x, int y) {
        super(WIDTH, x, y);
        minusNumber = (int)(Math.random() * 10) + 1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawLine(x1, y, x2, y);
    }
}

class Vec {
   private double x;
   private double y;

   Vec(double var1, double var3) {
      this.x = var1;
      this.y = var3;
   }

   Vec add(Vec var1) {
      return new Vec(this.x + var1.x, this.y + var1.y);
   }

   Vec sub(Vec var1) {
      return new Vec(this.x - var1.x, this.y - var1.y);
   }

   Vec mult(double var1) {
      return new Vec(this.x * var1, this.y * var1);
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

   void setX(double var1) {
      this.x = var1;
   }

   void setY(double var1) {
      this.y = var1;
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

class Building {
    private double height;
    ArrayList<Ray> lines;
    Color color;

    public Building(double height, ArrayList<Ray> lines, Color color) {
        this.height = height;
        this.lines = lines;
        this.color = color;
    }

    public void draw(Graphics g) { }
}

class Building_1 extends Building{

    public Building_1(double height, ArrayList<Ray> lines, Color color) {
        super(height, lines, color);
    }

    @Override
    public void draw(Graphics g) {
        
    }
}

////////////////////////////////////////////////
// Model (M)

class ShootingModel {
    private ArrayList<Bullet> bullets = new ArrayList<>();

    private ArrayList<SmallEnemy> smallEnemies = new ArrayList<>();
    private ArrayList<Boss> bosses = new ArrayList<>();

    private ArrayList<Fellow> fellows = new ArrayList<>();
    private ArrayList<PlusWall> plusWalls = new ArrayList<>();
    private ArrayList<MinusWall> minusWalls = new ArrayList<>();
    private Player player;

    int score = 0;

    public static final int WIDTH = 400;
    public static final int HEIGHT = 800;

    public ShootingModel() {
        player = new Player(WIDTH / 2 - 10, HEIGHT - 100);
        spawnEnemies();
        plusWalls.add(new PlusWall(WIDTH, 0, HEIGHT/2));
        plusWalls.add(new PlusWall(WIDTH, WIDTH/2, HEIGHT/2));
    }

    // 出現に関するメソッド
    public void spawnBoss() {
        bosses.add(new Boss(WIDTH/2, 0));
    }

    public void spawnEnemies() {
        for (int i = 0; i < 10; i++) {
            smallEnemies.add(new SmallEnemy((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT / 4)));
        }
    }

    public void spawnWall() {

        double random = Math.random();

        if(random < 0.500) {
            plusWalls.add(new PlusWall(WIDTH, 0, 0));
            minusWalls.add(new MinusWall(WIDTH, WIDTH/2 +5, 0));
        } else {
            plusWalls.add(new PlusWall(WIDTH, WIDTH/2 +5, 0));
            minusWalls.add(new MinusWall(WIDTH, 0, 0));       
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

    public ArrayList<SmallEnemy> getSmallEnemies() {
        return smallEnemies;
    }

    public ArrayList<Boss> getBoss() {
        return bosses;
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
        for (SmallEnemy smallEnemy : smallEnemies) {
            smallEnemy.moveTowardsPlayer(player);
        }
    }

    public void updateBoss() {
        for (Boss boss : bosses) {
            boss.moveTowardsPlayer(player);
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
        Iterator<SmallEnemy> smallEnemyIterator = smallEnemies.iterator();
        while (smallEnemyIterator.hasNext()) {
            SmallEnemy smallEnemy = smallEnemyIterator.next();
            for (Bullet bullet : bullets) {
                if (new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height)
                        .intersects(new Rectangle(smallEnemy.x, smallEnemy.y, smallEnemy.size, smallEnemy.size))) {

                    smallEnemy.HP--;
                    bullets.remove(bullet);
                    
                    if(smallEnemy.HP == 0) {
                        smallEnemyIterator.remove();
                        score++;

                        if(score == 10) {
                            spawnBoss();
                        }
                    }

                    break;
                }
            }
        }
    }


    public void checkBossllision() { // 弾とボスキャラの衝突判定
        Iterator<Boss> bossIterator = bosses.iterator();
        while (bossIterator.hasNext()) {
            Boss boss = bossIterator.next();
            for (Bullet bullet : bullets) {
                if(new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height).intersects(new Rectangle(boss.x, boss.y, boss.size, boss.size))) {

                    boss.HP--;
                    bullets.remove(bullet);
                    
                    if(boss.HP == 0) {
                        bossIterator.remove(); 
                        bosses.remove(boss);  
                    }
                          
                         

                    break;
                }
            }
        }
    }

    public void checkPlusWallCollisions() { // playerとplusWallの衝突判定
        for (PlusWall plusWall : plusWalls) {
            if (new Rectangle(player.x, player.y, player.size, 1).intersects(new Rectangle(plusWall.x1, plusWall.y, plusWall.x2 - plusWall.x1, 1))) {
                spawnFellows(plusWall.plusNumber);
                break;
            }
        }
    }

    public void checkMinusWallCollisions() { // playerとminusWallの衝突判定
        for (MinusWall minusWall : minusWalls) {
            if (new Rectangle(player.x, player.y, player.size, 1).intersects(new Rectangle(minusWall.x1, minusWall.y, minusWall.x2 - minusWall.x1, 1))) {

                int removeNumber = minusWall.minusNumber;
                int fellowNumber = fellows.size();

                if(removeNumber < fellowNumber) fellows.remove(removeNumber);
                else if(fellowNumber < removeNumber) fellows.remove(fellowNumber);

                break;
            }
        }
    }

    public void checkFellowCollisions() { // enemyとfellowの衝突判定
        Iterator<SmallEnemy> smallEnemyIterator = smallEnemies.iterator();
        while (smallEnemyIterator.hasNext()) {
            Enemy smallEnemy = smallEnemyIterator.next();
            for (Fellow fellow : fellows) {
                if (new Rectangle(fellow.x, fellow.y, fellow.size, fellow.size)
                        .intersects(new Rectangle(smallEnemy.x, smallEnemy.y, smallEnemy.size, smallEnemy.size))) {
                    smallEnemyIterator.remove();
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

        for (Enemy smallEnemy : model.getSmallEnemies()) {
            smallEnemy.draw(g);
        }

        for (Boss boss : model.getBoss()) {
            boss.draw(g);
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
    private javax.swing.Timer timer, firingTimer, spawnTimer;

    private boolean canFiringEvent = true;

    public Controller(ShootingModel model, ViewPanel view) {
        this.model = model;
        this.view = view;
        this.timer = new javax.swing.Timer(20, this);
        this.timer.start();
        view.addKeyListener(this);

        firingTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canFiringEvent = true;
            }
        });
        firingTimer.start();

        spawnTimer = new javax.swing.Timer(7000, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                model.spawnEnemies();
                model.spawnWall();
            }
        });
        spawnTimer.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        model.moveBullets();
        model.updateEnemies();
        model.updateBoss();
        model.movePlusWalls();
        model.moveMinusWalls();

        model.checkCollisions();
        model.checkBossllision();
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

            if(canFiringEvent) {
                
                model.getBullets().add(new Bullet(player.x + player.size / 2 - 2, player.y));
                for (Fellow fellow : fellows) {
                    model.getBullets().add(new Bullet(fellow.x + fellow.size / 2 - 2, fellow.y));
                }

                canFiringEvent = false;
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