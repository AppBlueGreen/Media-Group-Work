package Test3D2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View3D extends JPanel{
    private Map map;
    // private ArrayList<WallHit> wallHits;
    private Main main;

    public View3D(Main main){
        this.main = main;
        map = SharedData.currentMap;
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int index = (int) ((map.player.getAngle() / (2 * Math.PI)) * SharedData.TOTAL_BACKGROUNDS) % SharedData.TOTAL_BACKGROUNDS;
        if (index < 0) {
            index += SharedData.TOTAL_BACKGROUNDS; // 負の角度に対応
        }

        // 背景画像を描画
        g2d.drawImage(SharedData.backgrounds[index], 0, -5, SharedData.WIDTH, SharedData.HEIGHT + 5, null);

        double beamStep = SharedData.fov / 2500;
        ArrayList<WallHit> wallHits = new ArrayList<>();
        for (double angle = map.player.getAngle() - SharedData.fov / 2; angle < map.player.getAngle() + SharedData.fov / 2; angle += beamStep) {
            getWallhits(angle, wallHits);
        }
        draw3DWalls(g2d, wallHits);
    }

    private void getWallhits(double angle, ArrayList<WallHit> wallHits){
        
        for (Enemy enemy : map.enemies) {
            Ray beam = new Ray(map.player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(enemy.pos, new Vec(1 * Math.cos(map.player.getAngle()), 1 * Math.sin(map.player.getAngle())));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(map.player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - map.player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, null, 1, 0));
            }
        }

        for (Bullet bullet : SharedData.bullets) {
            Ray beam = new Ray(map.player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(bullet.pos, (new Vec(Math.cos(bullet.angle), Math.sin(bullet.angle))).mult(5));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(map.player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - map.player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, Color.YELLOW, 2, 0));
            }
        }

        double height = 0;
        for (Building bill : map.buildings) {
            Ray beam = new Ray(map.player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            for(Ray line : bill.lines){
                Vec hit = beam.intersection(line);
                if (hit != null) {
                    double wallDist = hit.sub(map.player.getPos()).mag();
                    double wallPerpDist = wallDist * Math.cos(angle - map.player.getAngle()); // 修正点
                    height = bill.getHeight();
                    WallHit wallHit = new WallHit(hit, wallPerpDist, angle, bill.color, 3, height);
                    wallHits.add(wallHit);
                }
            }
        }
        for (Boss boss : map.bosses) {
            Ray beam = new Ray(map.player.getPos(), new Vec(Math.cos(angle), Math.sin(angle)));
            Ray wall = new Ray(boss.pos, new Vec(1 * Math.cos(map.player.getAngle()), 1 * Math.sin(map.player.getAngle())));
            Vec hit = beam.intersection(wall);
            if (hit != null) {
                double wallDist = hit.sub(map.player.getPos()).mag();
                double wallPerpDist = wallDist * Math.cos(angle - map.player.getAngle()); // 修正点
                wallHits.add(new WallHit(hit, wallPerpDist, angle, null, 4, 0));
            }
        }
    }
    private void draw3DWalls(Graphics2D g2d, ArrayList<WallHit> wallHits) {
        
        wallHits.sort((a, b) -> Double.compare(b.distance, a.distance));
     

        for (WallHit wallHit : wallHits) {
            int screenCenterY = SharedData.HEIGHT / 2;
            double wallHeight = Math.min(SharedData.HEIGHT, 3000 / wallHit.distance);
            int wallY1 = (int)(screenCenterY + wallHeight / 2);
            int wallY2 = (int)(screenCenterY - wallHeight / 2);
            if (wallHit.wallNumber == 1) { // 敵の場合
                int enemyHeight = (int) Math.min(SharedData.HEIGHT, 3000 / wallHit.distance);
                int enemyWidth = enemyHeight; // 正方形と仮定
    
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - map.player.getAngle()) * getWidth() / SharedData.fov - enemyWidth / 2);
                int screenY = screenCenterY - enemyHeight / 2;
                g2d.drawImage(SharedData.enemyImage, screenX, screenY, enemyWidth, enemyHeight, null);
            }
            if(wallHit.wallNumber == 2){ // 弾の場合
                wallY1 = (int)(screenCenterY + wallHeight / 20);
                wallY2 = (int)(screenCenterY - wallHeight / 20);
                g2d.setColor(wallHit.color);
                g2d.drawLine((int) (getWidth() / 2 + (wallHit.angle - map.player.getAngle()) * getWidth() / SharedData.fov), wallY1,
                             (int) (getWidth() / 2 + (wallHit.angle - map.player.getAngle()) * getWidth() / SharedData.fov), wallY2);
            }

            if (wallHit.wallNumber == 3) { // ビルの場合
                double buildingHeight = wallHit.height;
                wallY2 = (int)(screenCenterY - wallHeight * buildingHeight);
                g2d.setColor(wallHit.color);
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - map.player.getAngle()) * getWidth() / SharedData.fov);
                g2d.drawLine(screenX, wallY1, screenX, wallY2);
            }

            if(wallHit.wallNumber == 4){ // ボスの場合
                int enemyHeight = (int) Math.min(SharedData.HEIGHT, 3000 / wallHit.distance);
                int enemyWidth = enemyHeight; // 正方形と仮定
                int screenX = (int) (getWidth() / 2 + (wallHit.angle - map.player.getAngle()) * getWidth() / SharedData.fov - enemyWidth / 2);
                int screenY = screenCenterY - enemyHeight / 2;
    
                for(Boss boss: map.bosses){
                    if(boss.getMotion() == 0){
                        g2d.drawImage(SharedData.bossImage0, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                    if(boss.getMotion() == 1){
                        g2d.drawImage(SharedData.bossImage1, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                    if(boss.getMotion() == 2){
                        g2d.drawImage(SharedData.bossImage2, screenX, screenY, enemyWidth, enemyHeight, null);
                    }
                }
            }
        }
    }
}