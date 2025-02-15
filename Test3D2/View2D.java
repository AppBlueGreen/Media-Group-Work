package Test3D2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class View2D extends JPanel implements ActionListener, KeyListener{
    private Map map;
    private Main main;
    private boolean canFiringEvent = true;
    
    public View2D(Main main){
        this.setPreferredSize(new Dimension(SharedData.WIDTH, SharedData.HEIGHT));
        setOpaque(false);
        map = SharedData.currentMap; 
        this.main = main;
        // タイマーとイベントリスナーの設定
        SharedData.timer = new Timer(15, this);
        SharedData.timer.start();
        this.setFocusable(true);
        this.addKeyListener(this);

        // 銃に関するタイマーの設定
        SharedData.firingTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canFiringEvent = true;
            }
        });
        SharedData.firingTimer.start();

        spawnEnemies(10, map.player.getPos(), Integer.MAX_VALUE);
    }
    private void spawnEnemies(int num, Vec pos, int dist) {
        for (int i = 0; i < num; ) {
            Vec NewEnemiePos = new Vec(Math.random() * map.field_width, Math.random() * map.field_height / 2);
            if(map.Map[(int)NewEnemiePos.getX()][(int)NewEnemiePos.getY()] == 0 && NewEnemiePos.sub(pos).mag() <= dist){
                map.enemies.add(new Enemy(NewEnemiePos));
                i++;
            }
        }
    }

    private void spawnBoss() {
        do{
            Vec NewBossPos = new Vec(Math.random() * map.field_width, Math.random() * map.field_height / 2);
            if(map.Map[(int)NewBossPos.getX()][(int)NewBossPos.getY()] == 0){
                map.bosses.add(new Boss(NewBossPos));
            }
        }while(map.bosses.isEmpty()); 
    }
    private boolean isMove(Vec pos){
        if(map.Map[(int)pos.getX()][(int)pos.getY()] == 0){
            return true;
        }
        return false;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // 弾を動かす
        Iterator<Bullet> bulletIterator = SharedData.bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.pos = bullet.pos.add((new Vec(Math.cos(bullet.angle), Math.sin(bullet.angle))).mult(5));
            if (bullet.pos.getY() < 0 || bullet.pos.getY() > map.field_height || bullet.pos.getX() < 0 || bullet.pos.getX() > map.field_width || map.Map[(int)bullet.pos.getX()][(int)bullet.pos.getY()] == 1) {
                bulletIterator.remove();
            }
        }

        // 敵と弾の衝突判定
        Iterator<Enemy> enemyIterator = map.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            for (Bullet bullet : SharedData.bullets) {
                if (new Rectangle((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height)
                        .intersects(new Rectangle((int) enemy.pos.getX(), (int) enemy.pos.getY(), enemy.size, enemy.size))) {

                    enemy.HP--;
                    SharedData.bullets.remove(bullet);

                    if(enemy.HP == 0) {
                        enemyIterator.remove();
                        map.player.addScore();

                        if(map.player.getScore() == 1){
                            spawnBoss();
                        }
                    }
                    break;
                }
            }
        }

        Iterator<Boss> bossIterator = map.bosses.iterator();
        while (bossIterator.hasNext()) {
            Boss boss = bossIterator.next();
            for (Bullet bullet : SharedData.bullets) {
                if (new Rectangle((int) bullet.pos.getX(), (int) bullet.pos.getY(), bullet.width, bullet.height)
                        .intersects(new Rectangle((int) boss.pos.getX(), (int) boss.pos.getY(), boss.size, boss.size))) {
                        
                    boss.HP--;
                    SharedData.bullets.remove(bullet);
                        
                    if (boss.HP == 0) {
                        bossIterator.remove();
                        map.player.addScore();
                    
                        // ボスを倒した後の処理（例: 次のステージへ）
                        // if (player.getScore() >= 10) {
                            // nextStage();
                        main.showScoreView(map.player.getScore());
                        // }
                    }
                    break;
                }
            }
        }

        // 再描画
        repaint();
        main.getView3D().repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 敵を描画
        for (Enemy enemy : map.enemies) {
            Vec direction = new Vec(map.player.getPos().getX() - enemy.pos.getX(), map.player.getPos().getY() - enemy.pos.getY());
            double len = direction.mag();
            Vec new_pos = enemy.pos.add((new Vec(direction.getX() / (len * 2), direction.getY() / (len * 2))));
            if(isMove(new_pos)) {
                if(enemy.pos.sub(map.player.getPos()).mag() < 15){
                    enemy.addAttackCount();
                    if(enemy.getAttackCount() == 40){
                        map.player.subHP(1);
                        enemy.resetAttackCount();
                    }
                    continue;
                }
                if(enemy.pos.sub(map.player.getPos()).mag() < 100){
                    enemy.pos = new_pos;
                }
            }

        } 
        // bossを描画
        for (Boss boss : map.bosses) {
            Vec direction = new Vec(map.player.getPos().getX() - boss.pos.getX(), map.player.getPos().getY() - boss.pos.getY());
            double len = direction.mag();
            Vec new_pos = boss.pos.add((new Vec(direction.getX() / (len * 2), direction.getY() / (len * 2))));
            boss.setMotion(0);
            if(isMove(new_pos)){
                if(boss.pos.sub(map.player.getPos()).mag() < 30){
                    boss.addAttackCount();
                    if(boss.getAttackCount() == 40){
                        spawnEnemies(1, boss.pos, 10);
                        map.player.subHP(5);
                        // bullets.add(new Bullet(new Vec(boss.pos.getX() - 2 * Math.cos(map.player.getAngle() + Math.PI / 32), boss.pos.getY() - 2 * Math.sin(map.player.getAngle() + Math.PI / 32)), -map.player.getAngle()));
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
                if(boss.pos.sub(map.player.getPos()).mag() < 100){
                    boss.pos =  new_pos;
                }
            }
        }  

        // 銃表示
        g2d.drawImage(SharedData.gun, (SharedData.WIDTH / 2) - 150, (SharedData.HEIGHT / 2) - 330, 700, 700, null);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(SharedData.WIDTH / 2 - 20, SharedData.HEIGHT / 2, 40, 2);
        g2d.fillRect(SharedData.WIDTH / 2, SharedData.HEIGHT / 2 - 20, 2, 40);
        
        
        
        g2d.setColor(new Color(34, 139, 34)); // 緑
        g2d.fillRect(20, 20, map.field_width / 7, map.field_height / 7);

        // 敵描画
        g2d.setColor(Color.RED);
        for (Enemy enemy : map.enemies) {
            g2d.fillOval((int) enemy.pos.getX() / 7 + 20, (int) enemy.pos.getY() / 7 + 20, enemy.size, enemy.size);
        }

        // boss描画
        g2d.setColor(Color.BLACK);
        for (Boss boss : map.bosses) {
            g2d.fillOval((int) boss.pos.getX() / 7 + 20, (int) boss.pos.getY() / 7 + 20, boss.size, boss.size);
        }

        g2d.setColor(Color.BLACK);
        for (Boss boss : map.bosses) {
            g2d.fillOval((int) boss.pos.getX() / 7 + 20, (int) boss.pos.getY() / 7 + 20, boss.size, boss.size);
        }

        // beamを描画
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine((int) map.player.getPos().getX() / 7 + 20, (int) map.player.getPos().getY() / 7 + 20, (int) (map.player.getPos().getX() / 7 + 20 + 10 * Math.cos(map.player.getAngle()) - SharedData.fov / 2), (int) (map.player.getPos().getY() / 7 + 20 + 10 * Math.sin(map.player.getAngle() - SharedData.fov / 2)));
        g2d.drawLine((int) map.player.getPos().getX() / 7 + 20, (int) map.player.getPos().getY() / 7 + 20, (int) (map.player.getPos().getX() / 7 + 20 + 10 * Math.cos(map.player.getAngle()) + SharedData.fov / 2), (int) (map.player.getPos().getY() / 7 + 20 + 10 * Math.sin(map.player.getAngle() + SharedData.fov / 2)));

        // プレイヤーを描画
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) map.player.getPos().getX() / 7 + 20, (int) map.player.getPos().getY() / 7 + 20, 4, 4);

        // 建物を描画
        for(Building building : map.buildings) {
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
        for (Ray wall : map.fieldWalls) {
            g2d.drawLine((int) wall.getBegin().getX() / 7 + 20, (int) wall.getBegin().getY() / 7 + 20,
                         (int) wall.getEnd(1).getX() / 7 + 20, (int) wall.getEnd(1).getY() / 7 + 20);
        }
        // 弾を描画
        g2d.setColor(Color.YELLOW);
        for (Bullet bullet : SharedData.bullets) {
            g2d.fillRect((int) bullet.pos.getX() / 7 + 20, (int) bullet.pos.getY() / 7 + 20, bullet.width / 2, bullet.height / 2);
        }

        // スコアを表示
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Score : " + map.player.getScore(), SharedData.WIDTH - 100, 30);

        // BossのHP表示
        if(!map.bosses.isEmpty()){
            for(Boss boss : map.bosses){
                g2d.setFont(new Font("Serif", Font.BOLD, 20));
                g2d.setColor(Color.BLACK);
                g2d.drawString("ボスのHP : " + boss.getHP(), SharedData.WIDTH - 650, 30);
                int x[] = {SharedData.WIDTH - 500, SharedData.WIDTH - 500, SharedData.WIDTH - 200, SharedData.WIDTH - 200};
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
        g2d.drawString("HP : " + map.player.getHP(), SharedData.WIDTH - 650, 520);
        int x[] = {SharedData.WIDTH - 500, SharedData.WIDTH - 500, SharedData.WIDTH - 200, SharedData.WIDTH - 200};
        int y[] = {505        ,         525,         525,         505};
        g2d.drawPolygon(x, y, 4);
        g2d.setColor(Color.YELLOW);
        double ratio = (double)map.player.getHP() / map.player.getMAXHP();
        double len = x[2] - x[0];
        int new_x = (int)(len * ratio + x[0]);
        x[2] = new_x; x[3] = new_x;
        y[0]++; y[1]--; y[2]--; y[3]++;
        x[0]++; x[1]++; x[2]--; x[3]--;
        g2d.fillPolygon(x, y, 4);
    }
    @Override
    public void keyPressed(KeyEvent e) {

        Vec new_pos = new Vec(map.player.getPos().getX(), map.player.getPos().getY());
        

        if (e.getKeyCode() == KeyEvent.VK_LEFT ) map.player.setAngle(map.player.getAngle() - Math.PI / 36);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) map.player.setAngle(map.player.getAngle() + Math.PI / 36);

        if (e.getKeyCode() == KeyEvent.VK_W   ){
            new_pos.setX(map.player.getPos().getX() + Math.cos(map.player.getAngle()));
            new_pos.setY(map.player.getPos().getY() + Math.sin(map.player.getAngle()));
            if(map.Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                map.player.setPos(new_pos);
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_S   ) {
            new_pos.setX(map.player.getPos().getX() - Math.cos(map.player.getAngle()));
            new_pos.setY(map.player.getPos().getY() - Math.sin(map.player.getAngle()));
            if(map.Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                map.player.setPos(new_pos);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_D   ) {
            new_pos.setX(map.player.getPos().getX() - Math.sin(map.player.getAngle()));
            new_pos.setY(map.player.getPos().getY() + Math.cos(map.player.getAngle()));
            if(map.Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                map.player.setPos(new_pos);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_A   ) {
            new_pos.setX(map.player.getPos().getX() + Math.sin(map.player.getAngle()));
            new_pos.setY(map.player.getPos().getY() - Math.cos(map.player.getAngle()));
            if(map.Map[(int)new_pos.getX()][(int)new_pos.getY()] == 0) {
                map.player.setPos(new_pos);
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if(canFiringEvent) {

                // 弾を発射
                SharedData.bullets.add(new Bullet(new Vec(map.player.getPos().getX() + 2 * Math.cos(map.player.getAngle() + Math.PI / 8), map.player.getPos().getY() + 2 * Math.sin(map.player.getAngle() + Math.PI / 8)), map.player.getAngle()));
                // SharedData.bullets.add(new Bullet(new Vec(player.getPos().getX() + 2 * Math.cos(player.getAngle() + Math.PI / 6 + Math.PI / 8), player.getPos().getY() + 2 * Math.sin(player.getAngle() +Math.PI / 6 + Math.PI / 8)), player.getAngle()+ Math.PI / 6 ));
                // SharedData.bullets.add(new Bullet(new Vec(player.getPos().getX() + 2 * Math.cos(player.getAngle() - Math.PI / 6  + Math.PI / 8), player.getPos().getY() + 2 * Math.sin(player.getAngle() - Math.PI / 6  + Math.PI / 8)), player.getAngle() - Math.PI / 6));
                canFiringEvent = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}