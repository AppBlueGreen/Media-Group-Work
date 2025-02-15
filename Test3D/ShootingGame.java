package Test3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Iterator;

public class ShootingGame extends JPanel implements ActionListener, KeyListener {
    // ウィンドウのサイズ
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FIELD_WIDTH = 600;
    private static final int FIELD_HEIGHT = 600;


    int Map[][] = new int[WIDTH][HEIGHT];
    // プレイヤーの位置とサイズ
    private Player player;
    // private Vec playerPos = new Vec(WIDTH / 2, HEIGHT - 40);
    private Vec playerPos = new Vec(590,130);

    // 弾と敵のリスト
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Ray> fieldWalls = new ArrayList<>();
    private ArrayList<Boss> bosses = new ArrayList<>();

    private ArrayList<Building> buildings = new ArrayList<>();
    private Image enemyImage;
    private Image bossImage0;
    private Image bossImage1;
    private Image bossImage2;
    private Image gun;
    // private Image buckGraund;
    private static final int TOTAL_BACKGROUNDS = 36;
    // private final Image[] backgrounds = new Image[TOTAL_BACKGROUNDS]; 
    private final BufferedImage[] backgrounds = new BufferedImage[TOTAL_BACKGROUNDS]; 

    private Main main;
    // ゲームのタイマー
    private Timer timer;
    // 銃に関するタイマー
    private boolean canFiringEvent = true;
    private Timer firingTimer;

    public ShootingGame(Main main) {
        this.main = main;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        enemyImage = new ImageIcon(getClass().getResource("/ma-rusu.png")).getImage();
        bossImage0 = new ImageIcon(getClass().getResource("/risaju.png")).getImage();
        bossImage1 = new ImageIcon(getClass().getResource("/risaju2.png")).getImage();
        bossImage2 = new ImageIcon(getClass().getResource("/risaju1.png")).getImage();
        gun = new ImageIcon(getClass().getResource("/gun.png")).getImage();
        // buckGraund = new ImageIcon(getClass().getResource("/Default_superflat_world.png")).getImage();

        for (int i = 0; i < TOTAL_BACKGROUNDS; i++) {
            // backgrounds[i] = Toolkit.getDefaultToolkit().getImage("background" + (i + 1) + ".png");
            try {
                backgrounds[i] = ImageIO.read(new File("background" + (i + 1) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1); // 画像がロードできない場合は終了
            }
        }


        // add buildings
        buildings.add(new Building(new Vec(400, 30), 60, 150, 4, new Color(0xffdead))); // 1  学生寮

        buildings.add(new Building(new Vec(430, 170), 40, 20, 1, new Color(0xfaebd7))); // 2.1　体育館
        buildings.add(new Building(new Vec(450, 150), 80, 110, 5, new Color(0xfaebd7))); // 2.2　体育館

        buildings.add(new Building(new Vec(505, 260), 10, 5, 1, new Color(0xcd853f))); // 3　第２体育館1 正面
        buildings.add(new Building(new Vec(510, 250), 40, 70, 3, new Color(0xcd853f))); // 3　第２体育館2 1,2F
        buildings.add(new Building(new Vec(510, 255), 30, 5, 1, new Color(0xfaf0e6))); // 3　第２体育館3 1F

        // buildings.add(new Building(new Vec(410, 290), 20, 20, 1, new Color(0xf5f5f5))); // 4　西５号館
        buildings.add(new Building(new Vec(430, 290), 20, 60, 5, new Color(0xdcdcdc))); // 5　西５号館
        buildings.add(new Building(new Vec(410, 310), 10, 80, 5, new Color(0xdcdcdc))); // 5　西５号館
        buildings.add(new Building(new Vec(410, 320), 30, 40, 5, new Color(0xdcdcdc))); // 5　西５号館

        buildings.add(new Building(new Vec(420, 370), 30, 40, 1, new Color(0xffebcd))); // 6　西食堂
        buildings.add(new Building(new Vec(460, 350), 50, 30, 1, new Color(0xffebcd))); // 7　西食堂

        buildings.add(new Building(new Vec(380, 440), 40, 90, 1, new Color(0xd3d3d3))); // 8　教育用計算機室

        buildings.add(new Building(new Vec(490, 420), 60, 20, 10, new Color(0xfffafa))); // 9　情報システム学研究科棟
        buildings.add(new Building(new Vec(510, 330), 150, 50, 10, new Color(0xf8f8ff))); // 10　情報システム学研究科棟

        buildings.add(new Building(new Vec(110, 170), 90, 50, 10, new Color(0xfaf0e6))); // 11　ピクトラボのところ

        buildings.add(new Building(new Vec(200, 160), 10, 40, 10, new Color(0xf8f8ff))); // 12　西７号館
        buildings.add(new Building(new Vec(180, 170), 40, 100, 10, new Color(0xf8f8ff))); // 13　西７号館

        buildings.add(new Building(new Vec(300, 170), 40, 60, 10, new Color(0xfffafa))); // 14　西６号館

        buildings.add(new Building(new Vec(110, 300), 40, 70, 10, new Color(0xfffaf0))); // 15　西４号館

        buildings.add(new Building(new Vec(190, 300), 40, 170, 10, new Color(0xffefd5))); // 16　西２号館

        buildings.add(new Building(new Vec(100, 420), 50, 50, 5, new Color(0xffe4c4))); // 17　西３号館
        buildings.add(new Building(new Vec(150, 420), 40, 40, 5, new Color(0xffe4c4))); // 18　西３号館

        buildings.add(new Building(new Vec(220, 420), 40, 100, 5, new Color(0xfaebd7))); // 19　西１号館

        buildings.add(new Building(new Vec(90, 490), 30, 50, 10, new Color(0xf8f8ff))); // 20　　西９号館
        buildings.add(new Building(new Vec(110, 520), 40, 30, 10, new Color(0xf8f8ff))); // 21　西９号館
        buildings.add(new Building(new Vec(80, 560), 40, 100, 10, new Color(0xf8f8ff))); // 22　西９号館

        buildings.add(new Building(new Vec(210, 490), 50, 40, 1, new Color(0xffebcd))); // 23　　西８号館
        buildings.add(new Building(new Vec(250, 520), 20, 40, 1, new Color(0xffebcd))); // 24　西８号館
        buildings.add(new Building(new Vec(290, 490), 50, 50, 1, new Color(0xffebcd))); // 25　西８号館
        buildings.add(new Building(new Vec(240, 540), 10, 100, 10, new Color(0xfaebd7))); // 26　西８号館
        buildings.add(new Building(new Vec(190, 550), 30, 110, 10, new Color(0xfaebd7))); // 27　西８号館

        buildings.add(new Building(new Vec(10, 80), 90, 50, 0.3, new Color(0xd3d3d3))); // 28　プール
        buildings.add(new Building(new Vec(330, 10), 20, 30, 1, new Color(0xffdead))); // 30　建物１
        buildings.add(new Building(new Vec(330, 100), 20, 30, 1, new Color(0xffdead))); // 31　建物２

        buildings.add(new Building(new Vec(0, 0), 590, 10, 0.5, Color.GRAY)); // 32
        buildings.add(new Building(new Vec(0, 590), 10, 330, 0.5, Color.GRAY)); // 33
        buildings.add(new Building(new Vec(330, 490), 100, 10, 0.5, Color.GRAY)); // 34
        buildings.add(new Building(new Vec(330, 490), 10, 260, 0.5, Color.GRAY)); // 35
        buildings.add(new Building(new Vec(590, 150), 390, 10, 0.5, Color.GRAY)); // 36
        buildings.add(new Building(new Vec(590, 0), 110, 10, 0.5, Color.GRAY)); // 37
        buildings.add(new Building(new Vec(0, 0), 10, 590, 0.5, Color.GRAY)); // 38


        
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(FIELD_WIDTH, 0).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(0, FIELD_HEIGHT).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(FIELD_WIDTH, FIELD_HEIGHT), new Vec(FIELD_WIDTH, 0).sub(new Vec(FIELD_WIDTH, FIELD_HEIGHT))));
        fieldWalls.add(new Ray(new Vec(FIELD_WIDTH, FIELD_HEIGHT), new Vec(0, FIELD_HEIGHT).sub(new Vec(FIELD_WIDTH, FIELD_HEIGHT))));

        this.player = new Player(playerPos, -Math.PI);
        // タイマーとイベントリスナーの設定
        timer = new Timer(15, this);
        timer.start();
        this.setFocusable(true);
        this.addKeyListener(this);

        // 銃に関するタイマーの設定
        firingTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canFiringEvent = true;
            }
        });
        firingTimer.start();


        for(int i = 0; i < WIDTH; i++)
        for(int j = 0; j < HEIGHT; j++)
            Map[i][j] = 0;

        for(Building building : buildings) {

            int x0 = (int)building.pos.getX();
            int y0 = (int)building.pos.getY();
            int width = (int)building.getWidth();
            int vertical = (int)building.getVertical();

            for(int j = y0; j < y0 + vertical; j++) {
                for(int i = x0; i < x0 + width; i++)
                    Map[i][j] = 1;
            }
        }

        // 敵を生成
        spawnEnemies(10, player.getPos(), Integer.MAX_VALUE);
    }

    private void spawnEnemies(int num, Vec pos, int dist) {
        for (int i = 0; i < num; ) {
            Vec NewEnemiePos = new Vec(Math.random() * FIELD_WIDTH, Math.random() * FIELD_HEIGHT / 2);
            if(Map[(int)NewEnemiePos.getX()][(int)NewEnemiePos.getY()] == 0 && NewEnemiePos.sub(pos).mag() <= dist){
                enemies.add(new Enemy(NewEnemiePos));
                i++;
            }
        }
    }

    private void spawnBoss() {
        do{
            Vec NewBossPos = new Vec(Math.random() * FIELD_WIDTH, Math.random() * FIELD_HEIGHT / 2);
            if(Map[(int)NewBossPos.getX()][(int)NewBossPos.getY()] == 0){
                bosses.add(new Boss(NewBossPos));
            }
        }while(bosses.isEmpty()); 
    }

    private boolean getCanMove(Vec pos){
        if(Map[(int)pos.getX()][(int)pos.getY()] == 0){
            return true;
        }
        return false;
    }
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int index = (int) ((player.getAngle() / (2 * Math.PI)) * TOTAL_BACKGROUNDS) % TOTAL_BACKGROUNDS;
        if (index < 0) {
            index += TOTAL_BACKGROUNDS; // 負の角度に対応
        }

        // 背景画像を描画
        g2d.drawImage(backgrounds[index], 0, -5, WIDTH, HEIGHT + 5, null);

        // 敵を描画
        for (Enemy enemy : enemies) {
            Vec direction = new Vec(player.getPos().getX() - enemy.pos.getX(), player.getPos().getY() - enemy.pos.getY());
            double len = direction.mag();
            Vec new_pos = enemy.pos.add((new Vec(direction.getX() / (len * 2), direction.getY() / (len * 2))));
            if(getCanMove(new_pos)) {
                if(enemy.pos.sub(player.getPos()).mag() < 15){
                    enemy.addAttackCount();
                    if(enemy.getAttackCount() == 40){
                        player.subHP(1);
                        enemy.resetAttackCount();
                    }
                    continue;
                }
                if(enemy.pos.sub(player.getPos()).mag() < 100){
                    enemy.pos = new_pos;
                }
            }

        } 
        // bossを描画
        for (Boss boss : bosses) {
            Vec direction = new Vec(player.getPos().getX() - boss.pos.getX(), player.getPos().getY() - boss.pos.getY());
            double len = direction.mag();
            Vec new_pos = boss.pos.add((new Vec(direction.getX() / (len * 2), direction.getY() / (len * 2))));
            boss.setMotion(0);
            if(getCanMove(new_pos)){
                if(boss.pos.sub(player.getPos()).mag() < 30){
                    boss.addAttackCount();
                    if(boss.getAttackCount() == 40){
                        spawnEnemies(1, boss.pos, 10);
                        player.subHP(5);
                        // bullets.add(new Bullet(new Vec(boss.pos.getX() - 2 * Math.cos(player.getAngle() + Math.PI / 32), boss.pos.getY() - 2 * Math.sin(player.getAngle() + Math.PI / 32)), -player.getAngle()));
                        boss.resetAttackCount();
                    }
                    if(14 <= boss.getAttackCount() && boss.getAttackCount() < 22){
                        boss.setMotion(1);
                    }
                    if(22 <= boss.getAttackCount() && boss.getAttackCount() < 40){
                        boss.setMotion(2);
                    }
                    continue;
                }
                if(boss.pos.sub(player.getPos()).mag() < 100){
                    boss.pos =  new_pos;
                }
            }
        }  

        double fov = Math.PI / 2;
        double beamStep = fov / 5500;
        ArrayList<WallHit> wallHits = new ArrayList<>();
        for (double angle = player.getAngle() - fov / 2; angle < player.getAngle() + fov / 2; angle += beamStep) {
            getWallhits(wallHits,  player,  angle,  fov,  enemies, bullets);
        }
        draw3DWalls(g2d, wallHits, player, fov, enemies, bullets, buildings);

        // 銃表示
        g2d.drawImage(gun, (WIDTH / 2) - 150, (HEIGHT / 2) - 330, 700, 700, null);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(WIDTH / 2 - 20, HEIGHT / 2, 40, 2);
        g2d.fillRect(WIDTH / 2, HEIGHT / 2 - 20, 2, 40);
        
        
        
        g2d.setColor(new Color(34, 139, 34)); // 緑
        g2d.fillRect(20, 20, FIELD_WIDTH / 7, FIELD_HEIGHT / 7);

        // 敵描画
        g2d.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g2d.fillOval((int) enemy.pos.getX() / 7 + 20, (int) enemy.pos.getY() / 7 + 20, enemy.size, enemy.size);
        }

        // boss描画
        g2d.setColor(Color.BLACK);
        for (Boss boss : bosses) {
            g2d.fillOval((int) boss.pos.getX() / 7 + 20, (int) boss.pos.getY() / 7 + 20, boss.size, boss.size);
        }

        g2d.setColor(Color.BLACK);
        for (Boss boss : bosses) {
            g2d.fillOval((int) boss.pos.getX() / 7 + 20, (int) boss.pos.getY() / 7 + 20, boss.size, boss.size);
        }

        // beamを描画
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine((int) player.getPos().getX() / 7 + 20, (int) player.getPos().getY() / 7 + 20, (int) (player.getPos().getX() / 7 + 20 + 10 * Math.cos(player.getAngle()) - fov / 2), (int) (player.getPos().getY() / 7 + 20 + 10 * Math.sin(player.getAngle() - fov / 2)));
        g2d.drawLine((int) player.getPos().getX() / 7 + 20, (int) player.getPos().getY() / 7 + 20, (int) (player.getPos().getX() / 7 + 20 + 10 * Math.cos(player.getAngle()) + fov / 2), (int) (player.getPos().getY() / 7 + 20 + 10 * Math.sin(player.getAngle() + fov / 2)));

        // プレイヤーを描画
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) player.getPos().getX() / 7 + 20, (int) player.getPos().getY() / 7 + 20, 4, 4);

        // 建物を描画
        for(Building building : buildings) {
            g2d.setColor(building.color);
            g2d.setStroke(new BasicStroke(1));

            // for(Ray wall : building.lines) {
            //     g2d.drawLine((int) wall.getBegin().getX() / 7 + 20, (int) wall.getBegin().getY() / 7 + 20,
            //     (int) wall.getEnd(1).getX() / 7 + 20, (int) wall.getEnd(1).getY() / 7 + 20);
            // }

            int positionX = (int)building.lines.get(0).getBegin().getX();
            int positionY = (int)building.lines.get(0).getBegin().getY();
            int width = (int)building.getWidth();
            int vertical = (int)building.getVertical();
            g2d.fillRect(positionX / 7 + 20, positionY / 7 + 20, width / 7, vertical / 7);
        }

        // フィールドの限界を描画
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        for (Ray wall : fieldWalls) {
            g2d.drawLine((int) wall.getBegin().getX() / 7 + 20, (int) wall.getBegin().getY() / 7 + 20,
                         (int) wall.getEnd(1).getX() / 7 + 20, (int) wall.getEnd(1).getY() / 7 + 20);
        }
        // 弾を描画
        g2d.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g2d.fillRect((int) bullet.pos.getX() / 7 + 20, (int) bullet.pos.getY() / 7 + 20, bullet.width / 2, bullet.height / 2);
        }

        // スコアを表示
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Score : " + player.getScore(), WIDTH - 100, 30);

        // BossのHP表示
        if(!bosses.isEmpty()){
            for(Boss boss : bosses){
                g2d.setFont(new Font("Serif", Font.BOLD, 20));
                g2d.setColor(Color.BLACK);
                g2d.drawString("ボスのHP : " + boss.getHP(), WIDTH - 650, 30);
                int x[] = {WIDTH - 500, WIDTH - 500, WIDTH - 200, WIDTH - 200};
                int y[] = {15         ,          35,          35,          15};
                g2d.drawPolygon(x, y, 4);

                g2d.setColor(Color.YELLOW);
                double ratio = (double)boss.getHP() / boss.getMAXHP();
                double len = x[2] - x[0];
                int new_x = (int)(len * ratio + x[0]);
                x[2] = new_x; x[3] = new_x;
                y[0]++; y[1]--; y[2]--; y[3]++;
                x[0]++; x[1]++; x[2]--; x[3]--;

                g2d.fillPolygon(x, y, 4);
            }
        }

        // プレイヤーHP表示
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        g2d.setColor(Color.BLACK);
        g2d.drawString("HP : " + player.getHP(), WIDTH - 650, 520);
        int x[] = {WIDTH - 500, WIDTH - 500, WIDTH - 200, WIDTH - 200};
        int y[] = {505        ,         525,         525,         505};
        g2d.drawPolygon(x, y, 4);
        g2d.setColor(Color.YELLOW);
        double ratio = (double)player.getHP() / player.getMAXHP();
        double len = x[2] - x[0];
        int new_x = (int)(len * ratio + x[0]);
        x[2] = new_x; x[3] = new_x;
        y[0]++; y[1]--; y[2]--; y[3]++;
        x[0]++; x[1]++; x[2]--; x[3]--;
        g2d.fillPolygon(x, y, 4);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 弾を動かす
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.pos = bullet.pos.add((new Vec(Math.cos(bullet.angle), Math.sin(bullet.angle))).mult(5));
            if (bullet.pos.getY() < 0 || bullet.pos.getY() > FIELD_HEIGHT || bullet.pos.getX() < 0 || bullet.pos.getX() > FIELD_WIDTH || Map[(int)bullet.pos.getX()][(int)bullet.pos.getY()] == 1) {
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

                    enemy.HP--;
                    bullets.remove(bullet);

                    if(enemy.HP == 0) {
                        enemyIterator.remove();
                        player.addScore();

                        if(player.getScore() == 1){
                            spawnBoss();
                        }
                    }
                    break;
                }
            }
        }

        Iterator<Boss> bossIterator = bosses.iterator();
        while (bossIterator.hasNext()) {
            Boss boss = bossIterator.next();
            for (Bullet bullet : bullets) {
                if (new Rectangle((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height)
                        .intersects(new Rectangle((int) boss.pos.getX(), (int) boss.pos.getY(), boss.size, boss.size))) {
                        
                    boss.HP--;
                    bullets.remove(bullet);
                        
                    if (boss.HP == 0) {
                        bossIterator.remove();
                        player.addScore();
                    
                        // ボスを倒した後の処理（例: 次のステージへ）
                        // if (player.getScore() >= 10) {
                            // nextStage();
                        main.showScoreView(player.getScore());
                        // }
                    }
                    break;
                }
            }
        }

        // 再描画
        repaint();
    }

    private void getWallhits(ArrayList<WallHit> wallHits, Player player, double angle, double fov, ArrayList<Enemy> enemies, ArrayList<Bullet> bullets){
        
        for (Enemy enemy : enemies) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(enemy.pos, new Vec(1 * Math.cos(player.getAngle()), 1 * Math.sin(player.getAngle())));
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
        for (Building bill : buildings) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            for(Ray line : bill.lines){
                Vec hit = beam.intersection(line);
                if (hit != null) {
                    double wallDist = hit.sub(player.getPos()).mag();
                    double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                    // int brightness = (int) Math.max(0, Math.min(255, 255 - wallPerpDist * 10));
                    // wallHits.add(new WallHit(hit, wallPerpDist, new Color(brightness, brightness, brightness), 1));
                    WallHit wallHit = new WallHit(hit, wallPerpDist, angle, bill.color, 4);
                    height = bill.getHeight();
                    wallHit.setIndex(height);
                    // System.out.println(index);
                    wallHits.add(wallHit);
                }
            }
        }
        for (Boss boss : bosses) {
            Ray beam = new Ray(player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(boss.pos, new Vec(1 * Math.cos(player.getAngle()), 1 * Math.sin(player.getAngle())));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, Color.RED, 5));
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

            if(wallHit.wallNumber == 5){
                int enemyHeight = (int) Math.min(HEIGHT, 3000 / wallHit.distance);
                int enemyWidth = enemyHeight; // 正方形と仮定
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - player.getAngle()) * getWidth() / fov - enemyWidth / 2);
                int screenY = screenCenterY - enemyHeight / 2;
    
                for(Boss boss: bosses){
                    if(boss.getMotion() == 0){
                        g2d.drawImage(bossImage0, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                    if(boss.getMotion() == 1){
                        g2d.drawImage(bossImage1, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                    if(boss.getMotion() == 2){
                        g2d.drawImage(bossImage2, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                }
            }
        }
    }
    

    @Override
    public void keyPressed(KeyEvent e) {

        Vec new_pos = new Vec(player.getPos().getX(), player.getPos().getY());
        

        if (e.getKeyCode() == KeyEvent.VK_LEFT ) player.setAngle(player.getAngle() - Math.PI / 36);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.setAngle(player.getAngle() + Math.PI / 36);

        if (e.getKeyCode() == KeyEvent.VK_W   ){
            new_pos.setX(player.getPos().getX() + Math.cos(player.getAngle()));
            new_pos.setY(player.getPos().getY() + Math.sin(player.getAngle()));
            if(Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                player.setPos(new_pos);
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_S   ) {
            new_pos.setX(player.getPos().getX() - Math.cos(player.getAngle()));
            new_pos.setY(player.getPos().getY() - Math.sin(player.getAngle()));
            if(Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                player.setPos(new_pos);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_D   ) {
            new_pos.setX(player.getPos().getX() - Math.sin(player.getAngle()));
            new_pos.setY(player.getPos().getY() + Math.cos(player.getAngle()));
            if(Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                player.setPos(new_pos);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_A   ) {
            new_pos.setX(player.getPos().getX() + Math.sin(player.getAngle()));
            new_pos.setY(player.getPos().getY() - Math.cos(player.getAngle()));
            if(Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                player.setPos(new_pos);
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if(canFiringEvent) {

                // 弾を発射
                bullets.add(new Bullet(new Vec(player.getPos().getX() + 2 * Math.cos(player.getAngle() + Math.PI / 8), player.getPos().getY() + 2 * Math.sin(player.getAngle() + Math.PI / 8)), player.getAngle()));
                // bullets.add(new Bullet(new Vec(player.getPos().getX() + 2 * Math.cos(player.getAngle() + Math.PI / 6 + Math.PI / 8), player.getPos().getY() + 2 * Math.sin(player.getAngle() +Math.PI / 6 + Math.PI / 8)), player.getAngle()+ Math.PI / 6 ));
                // bullets.add(new Bullet(new Vec(player.getPos().getX() + 2 * Math.cos(player.getAngle() - Math.PI / 6  + Math.PI / 8), player.getPos().getY() + 2 * Math.sin(player.getAngle() - Math.PI / 6  + Math.PI / 8)), player.getAngle() - Math.PI / 6));
                canFiringEvent = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // public static void main(String[] args) {
    //     JFrame frame = new JFrame("Shooting Game");
    //     ShootingGame gamePanel = new ShootingGame();

    //     frame.add(gamePanel);
    //     frame.pack();
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setVisible(true);
    // }
}
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
    int width = 5;
    int height = 5;

    public Bullet(Vec pos, double angle) {
        this.pos = pos;
        this.angle = angle;
    }
}

class Player {
    private Vec pos;
    private double angle;
    private int score;
    private int MAX_HP = 50;
    private int HP = 50;

    Player(Vec pos, double angle) {
        this.pos = pos;
        this.angle = angle;
        this.score = 0;
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
    void addScore(){
        this.score++;
    }
    int getScore(){
        return this.score;
    }
    void subHP(int x){
        this.HP -= x;;
    }
    int getHP() {
        return HP;
    }

    int getMAXHP(){
        return MAX_HP;
    }
}
// 敵のクラス
class Enemy {
    Vec pos;
    int size = 3;
    int HP = 2;
    private int attackCount = 0;  
    Boolean canMoveEnemy = true;

    public Enemy(Vec pos) {
        this.pos = pos;
    }

    public int getHP() {
        return HP;
    }
    void addAttackCount(){
        this.attackCount++;
    }
    int getAttackCount(){
        return this.attackCount;
    }
    void resetAttackCount(){
        this.attackCount = 0;
    }
}
class Boss {
    Vec pos;
    int size = 5;
    int HP = 5;
    private final int MAX_HP = 5;
    private int attackCount = 0;
    private int motion = 0;

    public Boss(Vec pos) {
        this.pos = pos;
    }

    public int getHP() {
        return HP;
    }

    int getMAXHP(){
        return MAX_HP;
    }
    void addAttackCount(){
        this.attackCount++;
    }
    int getAttackCount(){
        return this.attackCount;
    }
    void resetAttackCount(){
        this.attackCount = 0;
    }
    void setMotion(int x){
        this.motion = x;
    }
    int getMotion(){
        return this.motion;
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
    Vec pos;
    private double vertical, width, height;
    ArrayList<Ray> lines = new ArrayList<>();
    Color color;


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

    public double getHeight() {
        return height;
    }

    public double getVertical() {
        return vertical;
    }

    public double getWidth() {
        return width;
    }

    public void draw(Graphics g) { }
}