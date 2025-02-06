import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Iterator;

class WallHit {
    Vec hitPoint;        // 壁との交差点
    double distance;     // プレイヤーから交差点までの距離
    double angle;        // beamの角度
    Color color;         // 壁の色
    int wallNumber;      // 壁の番号
    double index;

    public WallHit(Vec hitPoint, double distance, double angle, Color color, int wallNumber) {
        this.hitPoint = hitPoint;
        this.distance = distance;
        this.angle = angle;
        this.color = color;
        this.wallNumber = wallNumber;
    }

    public void setIndex(double index) {
        this.index = index;
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
    double angle;
    int width = 10;
    int height = 20;

    public Bullet(Vec pos, double angle) {
        this.pos = pos;
        this.angle = angle;
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
    private Vec pos;
    private int size = 30;
    private int HP = 2;

    public Enemy(Vec pos) {
        this.pos = pos;
    }

    public Vec getPos() {
        return pos;
    }

    public void setPos(Vec pos) {
        this.pos = pos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }
}

class Boss {
    private Vec pos;
    private int size = 40;
    private int HP = 5;

    public Boss(Vec pos) {
        this.pos = pos;
    }

    public Vec getPos() {
        return pos;
    }

    public void setPos(Vec pos) {
        this.pos = pos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
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

class Building extends Ray{
    private Vec pos;
    private double vertical, width, height;
    private ArrayList<Ray> lines = new ArrayList<>();
    private Color color;


    public Building(Vec pos, double vertical, double width, double height, Color color) {
        super(pos, pos);
        this.pos = pos;
        this.vertical = vertical;
        this.width = width;
        this.height = height;
        this.color = color;

        Vec vec1 = new Vec(width, 0);
        Vec vec2 = new Vec(0, vertical);

        this.lines.add(new Ray(pos, vec1));
        this.lines.add(new Ray(pos, vec2));
        this.lines.add(new Ray(pos.add(vec2), vec1));
        this.lines.add(new Ray(pos.add(vec1), vec2));
    }

    public double getHeight() { return height;}

    public Color getColor() { return color; }

    public ArrayList<Ray> getLines() { return lines;}

    public void draw(Graphics g) { }
}

//////////////////////////////////////////////////////////////////////
// Model
//////////////////////////////////////////////////////////////////////
class ShootingModel {

    // ウィンドウのサイズ
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;

    // プレイヤーの位置とサイズ
    private Player player;
    // private Vec playerPos = new Vec(WIDTH / 2, HEIGHT - 40);
    private Vec playerPos = new Vec(WIDTH,40);

    // 弾と敵のリスト
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Ray> fieldWalls = new ArrayList<>();
    private ArrayList<Boss> bosses = new ArrayList<>();
    private ArrayList<Building> buildings = new ArrayList<>();

    ShootingModel() {

        this.player = new Player(playerPos, -Math.PI / 2);

        // 敵を生成
        spawnEnemies();      

        // 建物の追加
        buildings.add(new Building(new Vec(400, 30), 60, 150, 4, new Color(0xf5deb3))); // 1  学生
        buildings.add(new Building(new Vec(430, 170), 40, 20, 1, Color.WHITE)); // 2.1　体育館
        buildings.add(new Building(new Vec(450, 150), 80, 110, 5, Color.WHITE)); // 2.2　体育館

        buildings.add(new Building(new Vec(510, 250), 40, 70, 2, new Color(0xf4a460))); // 3　第２体育館

        buildings.add(new Building(new Vec(410, 290), 60, 40, 5, Color.WHITE)); // 4　西５号館
        buildings.add(new Building(new Vec(450, 290), 30, 40, 5, Color.WHITE)); // 5　西５号館

        buildings.add(new Building(new Vec(420, 370), 30, 40, 1, new Color(0xf5deb3))); // 6　西食堂
        buildings.add(new Building(new Vec(460, 350), 50, 30, 1, new Color(0xf5deb3))); // 7　西食堂

        buildings.add(new Building(new Vec(380, 440), 40, 90, 1, new Color(0xd3d3de))); // 8　教育用計算機室

        buildings.add(new Building(new Vec(490, 420), 60, 20, 10, Color.WHITE)); // 9　情報システム学研究科棟
        buildings.add(new Building(new Vec(510, 330), 150, 50, 10, Color.WHITE)); // 10　情報システム学研究科棟

        buildings.add(new Building(new Vec(110, 170), 90, 50, 10, Color.WHITE)); // 11  西１１号館

        buildings.add(new Building(new Vec(200, 160), 10, 40, 10, Color.WHITE)); // 12　西７号館
        buildings.add(new Building(new Vec(180, 170), 40, 100, 10, Color.WHITE)); // 13　西７号館

        buildings.add(new Building(new Vec(300, 170), 40, 60, 10, Color.WHITE)); // 14　西６号館

        buildings.add(new Building(new Vec(110, 300), 40, 70, 10, Color.GRAY)); // 15　西４号館

        buildings.add(new Building(new Vec(190, 300), 40, 170, 10, Color.GRAY)); // 16　西２号館

        buildings.add(new Building(new Vec(100, 420), 50, 50, 5, new Color(0xfffabc))); // 17　西３号館
        buildings.add(new Building(new Vec(150, 420), 40, 40, 5, new Color(0xfffabc))); // 18　西３号館

        buildings.add(new Building(new Vec(220, 420), 40, 100, 5, Color.GRAY)); // 19　西１号館

        buildings.add(new Building(new Vec(90, 490), 30, 50, 10, Color.GRAY)); // 20　　西９号館
        buildings.add(new Building(new Vec(110, 520), 40, 30, 10, Color.GRAY)); // 21　西９号館
        buildings.add(new Building(new Vec(80, 560), 40, 100, 10, Color.GRAY)); // 22　西９号館

        buildings.add(new Building(new Vec(210, 490), 50, 40, 1, Color.GRAY)); // 23　　西８号館
        buildings.add(new Building(new Vec(250, 520), 20, 40, 1, Color.GRAY)); // 24　西８号館
        buildings.add(new Building(new Vec(290, 490), 50, 50, 1, Color.GRAY)); // 25　西８号館
        buildings.add(new Building(new Vec(240, 540), 10, 100, 10, Color.GRAY)); // 26　西８号館
        buildings.add(new Building(new Vec(190, 550), 30, 110, 10, Color.GRAY)); // 27　西８号館


        
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(WIDTH, 0).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(0, HEIGHT).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(WIDTH, HEIGHT), new Vec(WIDTH, 0).sub(new Vec(WIDTH, HEIGHT))));
        fieldWalls.add(new Ray(new Vec(WIDTH, HEIGHT), new Vec(0, HEIGHT).sub(new Vec(WIDTH, HEIGHT))));
    }

    // 出現に関するメソッド
    public void spawnEnemies() {
        for (int i = 0; i < 10; i++) {
            enemies.add(new Enemy(new Vec(Math.random() * WIDTH, Math.random() * HEIGHT / 2)));
        }
    }

    // 値取得に関するメソッド
    public Player getPlayer() {return player;}

    public ArrayList<Enemy> getEnemys() {return enemies;}

    public ArrayList<Bullet> getBullets() {return bullets;}

    public ArrayList<Building> getBuildings() {return buildings;}
    
    public ArrayList<Ray> getFieldWalls() {return fieldWalls;}

    public int getHIGHT() { return HEIGHT;}

    public int getWIDTH() { return WIDTH;}
}


//////////////////////////////////////////////////////////////////////
// View
//////////////////////////////////////////////////////////////////////
class ViewPanel extends JPanel{

    private ShootingModel model;

    private Image enemyImage;
    private Image gun;
    // private Image buckGraund;
    private static final int TOTAL_BACKGROUNDS = 36;
    // private final Image[] backgrounds = new Image[TOTAL_BACKGROUNDS]; 
    private final BufferedImage[] backgrounds = new BufferedImage[TOTAL_BACKGROUNDS]; 

    ViewPanel(ShootingModel model) {

        this.model = model;
        // 背景の追加

        enemyImage = new ImageIcon(getClass().getResource("/creeper1.png")).getImage();
        gun = new ImageIcon(getClass().getResource("/gun2.png")).getImage();

        // buckGraund = new ImageIcon(getClass().getResource("/Default_superflat_world.png")).getImage();
        this.setFocusable(true);
    }

    private void getWallhits(ArrayList<WallHit> wallHits, Player player, double angle, double fov, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets){

        for (Enemy enemy : enemies) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(enemy.getPos(), new Vec(1 * Math.cos(player.getAngle()), 1 * Math.sin(player.getAngle())));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, Color.RED, 2));
            }
        }

        for (Bullet bullet : bullets) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(bullet.pos, (new Vec(Math.cos(bullet.angle), Math.sin(bullet.angle))).mult(5));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, Color.YELLOW, 3));
            }
        }

        // これがBuildingクラスのbeamと交差しているか判定
        // 変数名等変更すべき場所等は変更して使う
        double height = 0;
        for (Building bill : model.getBuildings()) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            for(Ray line : bill.getLines()){
                Vec hit = beam.intersection(line);
                if (hit != null) {
                    double wallDist = hit.sub(player.getPos()).mag();
                    double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                    // int brightness = (int) Math.max(0, Math.min(255, 255 - wallPerpDist * 10));
                    // wallHits.add(new WallHit(hit, wallPerpDist, new Color(brightness, brightness, brightness), 1));
                    WallHit wallHit = new WallHit(hit, wallPerpDist, angle, bill.getColor(), 4);
                    height = bill.getHeight();
                    wallHit.setIndex(height);
                    // System.out.println(index);
                    wallHits.add(wallHit);
                }
            }
        }
    }

    private void draw3DWalls(Graphics2D g2d, ArrayList<WallHit> wallHits, Player player, double fov, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets, ArrayList<Building> buildings) {
        
        wallHits.sort((a, b) -> Double.compare(b.distance, a.distance));
     

        for (WallHit wallHit : wallHits) {
            int screenCenterY = HEIGHT / 2;
            double wallHeight = Math.min(HEIGHT, 3000 / wallHit.distance);
            int wallY1 = (int)(screenCenterY + wallHeight / 2);
            int wallY2 = (int)(screenCenterY - wallHeight / 2);
            if(wallHit.wallNumber == 1){
                wallY2 = (int)(screenCenterY - wallHeight * 5);
                g2d.setColor(wallHit.color);
                g2d.drawLine((int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov), wallY1,
                             (int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov), wallY2);
            }
            if (wallHit.wallNumber == 2) { // 敵の場合
                int enemyHeight = (int) Math.min(HEIGHT, 3000 / wallHit.distance);
                int enemyWidth = enemyHeight; // 正方形と仮定
    
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov - enemyWidth / 2);
                int screenY = screenCenterY - enemyHeight / 2;
                g2d.drawImage(enemyImage, screenX, screenY, enemyWidth, enemyHeight, null);
            }
            if(wallHit.wallNumber == 3){
                wallY1 = (int)(screenCenterY + wallHeight / 20);
                wallY2 = (int)(screenCenterY - wallHeight / 20);
                g2d.setColor(wallHit.color);
                g2d.drawLine((int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov), wallY1,
                             (int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov), wallY2);
            }

            if (wallHit.wallNumber == 4) {
                double buildingHeight = wallHit.index;
                wallY2 = (int)(screenCenterY - wallHeight * buildingHeight);
                g2d.setColor(wallHit.color);
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov);
                g2d.drawLine(screenX, wallY1, screenX, wallY2);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 背景の上半分を水色に塗る
        // g2d.setColor(new Color(135, 206, 235)); // 水色 (スカイブルー)
        // g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        
        // 背景の下半分を緑色に塗る
        // g2d.setColor(new Color(34, 139, 34)); // 緑色 (草原のような色)
        // g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        // g2d.drawImage(buckGraund, 0, -5, WIDTH, HEIGHT+5, null);
        
        int index = (int) ((model.getPlayer().getAngle() / (2 * Math.PI)) * TOTAL_BACKGROUNDS) % TOTAL_BACKGROUNDS;
        if (index < 0) {
            index += TOTAL_BACKGROUNDS; // 負の角度に対応
        }

        // 背景画像を描画
        g2d.drawImage(backgrounds[index], 0, -5, WIDTH, HEIGHT + 5, null);

        // 敵を描画
        g2d.setColor(Color.RED);
        for (Enemy enemy : model.getEnemys()) {
            g2d.fillOval((int) enemy.getPos().getX() / 7 + 20, (int) enemy.getPos().getY() / 7 + 20, enemy.getSize() / 7 , enemy.getSize() / 7 );
            // enemy.pos = enemy.pos.add(new Vec(0, 1)); // 敵を下に動かす
            if(enemy.getPos().sub(model.getPlayer().getPos()).mag() < 15){
                continue;
            }
            Vec direction = new Vec(model.getPlayer().getPos().getX() - enemy.getPos().getX(), model.getPlayer().getPos().getY() - enemy.getPos().getY());
            double len = direction.mag();
            enemy.setPos(enemy.getPos().add(new Vec(direction.getX() / (len * 2), direction.getY() / (len * 2)))); // 敵を下に動かす
        }  

        double fov = Math.PI / 2;
        double beamStep = fov / 5500;
        ArrayList<WallHit> wallHits = new ArrayList<>();
        for (double angle = model.getPlayer().getAngle() - fov / 2; angle < model.getPlayer().getAngle() + fov / 2; angle += beamStep) {
            getWallhits(wallHits,  model.getPlayer(),  angle,  fov,  model.getEnemys(), model.getBullets());
        }
        draw3DWalls(g2d, wallHits, model.getPlayer(), fov, model.getEnemys(), model.getBullets(), model.getBuildings());
        g2d.drawImage(gun, (WIDTH / 2) + 120, (HEIGHT / 2) - 250, 600, 600, null);

        // beamを描画
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1));


        // プレイヤーを描画
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) model.getPlayer().getPos().getX() / 7 + 20, (int) model.getPlayer().getPos().getY() / 7 + 20, 4, 4);

        // 建物を描画
        for(Building building : model.getBuildings()) {
            g2d.setColor(building.getColor());
            g2d.setStroke(new BasicStroke(1));

            for(Ray wall : building.getLines()) {
                g2d.drawLine((int) wall.getBegin().getX() / 7 + 20, (int) wall.getBegin().getY() / 7 + 20,
                (int) wall.getEnd(1).getX() / 7 + 20, (int) wall.getEnd(1).getY() / 7 + 20);
            }
        }

        // フィールドの限界を描画
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        for (Ray wall : model.getFieldWalls()) {
            g2d.drawLine((int) wall.getBegin().getX() / 7 + 20, (int) wall.getBegin().getY() / 7 + 20,
                         (int) wall.getEnd(1).getX() / 7 + 20, (int) wall.getEnd(1).getY() / 7 + 20);
        }
        // 弾を描画
        g2d.setColor(Color.YELLOW);
        for (Bullet bullet : model.getBullets()) {
            g2d.fillRect((int) bullet.pos.getX() / 7 + 20, (int) bullet.pos.getY() / 7 + 20, bullet.width / 7, bullet.height / 7);
        }
    }

}

//////////////////////////////////////////////////////////////////////
// Controller
//////////////////////////////////////////////////////////////////////

class Controller extends JPanel implements ActionListener, KeyListener {

    private ShootingModel model;
    private ViewPanel view;
    private Timer timer; // ゲームのタイマー

    Controller(ShootingModel model, ViewPanel view) {

        this.model = model;
        this.view = view;
        timer = new Timer(15, this); // タイマーとイベントリスナーの設定
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 弾を動かす
        Iterator<Bullet> bulletIterator = model.getBullets().iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.pos = bullet.pos.add((new Vec(Math.cos(bullet.angle), Math.sin(bullet.angle))).mult(3));
            if (bullet.pos.getY() < 0 || bullet.pos.getY() > model.getHIGHT() || bullet.pos.getX() < 0 || bullet.pos.getX() > model.getWIDTH()) {
                bulletIterator.remove();
            }
        }

        // 敵と弾の衝突判定
        Iterator<Enemy> enemyIterator = model.getEnemys().iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            for (Bullet bullet : model.getBullets()) {
                if (new Rectangle((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height)
                        .intersects(new Rectangle((int) enemy.getPos().getX(), (int) enemy.getPos().getY(), enemy.getSize(), enemy.getSize()))) {

                    enemy.setHP(enemy.getHP() -1);
                    model.getBullets().remove(bullet);

                    if(enemy.getHP() == 0) {
                        enemyIterator.remove();
                    }
                    break;
                }
            }
        }

        // 再描画
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // if (e.getKeyCode() == KeyEvent.VK_LEFT && playerPos.getX() > 0) {
        //     player.setPos(new Vec(player.getPos().getX() - 10, player.getPos().getY()));
        // }
        // if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerPos.getX() < WIDTH) {
        //     player.setPos(new Vec(player.getPos().getX() + 10, player.getPos().getY()));
        // }
        if (e.getKeyCode() == KeyEvent.VK_LEFT ) model.getPlayer().setAngle(model.getPlayer().getAngle() - Math.PI / 36);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) model.getPlayer().setAngle(model.getPlayer().getAngle() + Math.PI / 36);
        if (e.getKeyCode() == KeyEvent.VK_W   ) model.getPlayer().setPos(new Vec(model.getPlayer().getPos().getX() + Math.cos(model.getPlayer().getAngle()), model.getPlayer().getPos().getY() + Math.sin(model.getPlayer().getAngle())));
        if (e.getKeyCode() == KeyEvent.VK_S   ) model.getPlayer().setPos(new Vec(model.getPlayer().getPos().getX() - Math.cos(model.getPlayer().getAngle()), model.getPlayer().getPos().getY() - Math.sin(model.getPlayer().getAngle())));
        if (e.getKeyCode() == KeyEvent.VK_D   ) model.getPlayer().setPos(new Vec(model.getPlayer().getPos().getX() - Math.sin(model.getPlayer().getAngle()), model.getPlayer().getPos().getY() + Math.cos(model.getPlayer().getAngle())));
        if (e.getKeyCode() == KeyEvent.VK_A   ) model.getPlayer().setPos(new Vec(model.getPlayer().getPos().getX() + Math.sin(model.getPlayer().getAngle()), model.getPlayer().getPos().getY() - Math.cos(model.getPlayer().getAngle())));
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 弾を発射
            model.getBullets().add(new Bullet(new Vec(model.getPlayer().getPos().getX() + 2 * Math.cos(model.getPlayer().getAngle() + Math.PI / 8), model.getPlayer().getPos().getY() + 2 * Math.sin(model.getPlayer().getAngle() + Math.PI / 8)), model.getPlayer().getAngle()));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

//////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////

class ShootingGame{
    public static void main(String[] args) {

        ShootingModel model = new ShootingModel();
        ViewPanel view = new ViewPanel(model);

        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(ShootingModel.WIDTH, ShootingModel.HEIGHT);

        frame.add(view);
        frame.pack();
        frame.setVisible(true);

        new Controller(model, view);
    }
}