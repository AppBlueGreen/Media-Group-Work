package Test3D2;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class SharedData {
    // ウィンドウのサイズ
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static double fov = Math.PI / 2;

    // ゲームのタイマー
    public static Timer timer;
    // 銃に関するタイマー
    public static boolean canFiringEvent = true;
    public static Timer firingTimer;

    // 選択したマップ
    public static Map currentMap; 
    // 弾のリスト
    public static ArrayList<Bullet> bullets = new ArrayList<>();

    // Mapのリスト
    public static ArrayList<Map> Maps = new ArrayList<>();

    // 敵の画像
    public static Image enemyImage = new ImageIcon(SharedData.class.getResource("/Image/ma-rusu.png")).getImage();;
    public static Image bossImage0 = new ImageIcon(SharedData.class.getResource("/Image/risaju.png")).getImage();;
    public static Image bossImage1 = new ImageIcon(SharedData.class.getResource("/Image/risaju2.png")).getImage();;
    public static Image bossImage2 = new ImageIcon(SharedData.class.getResource("/Image/risaju1.png")).getImage();;
    public static Image gun = new ImageIcon(SharedData.class.getResource("/Image/gun.png")).getImage();
    ;
    // 背景画像
    public static final int TOTAL_BACKGROUNDS = 36;
    public static final BufferedImage[] backgrounds = new BufferedImage[TOTAL_BACKGROUNDS];

    static {
        loadBackgrounds();
        initializeMaps(); // ここでマップを初期化
    }

    private static void loadBackgrounds(){
        for (int i = 0; i < TOTAL_BACKGROUNDS; i++) {
            try {
                backgrounds[i] = ImageIO.read(new File("Image/background" + (i + 1) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1); // 画像がロードできない場合は終了
            }
        }
    }
    private static void initializeMaps(){
        Map nishi = new Map(600, 600, new Vec(590,130), -Math.PI);

        nishi.buildings.add(new Building(new Vec(400, 30), 60, 150, 4, new Color(0xffdead))); // 1  学生寮

        nishi.buildings.add(new Building(new Vec(430, 170), 40, 20, 1, new Color(0xfaebd7))); // 2.1　体育館
        nishi.buildings.add(new Building(new Vec(450, 150), 80, 110, 5, new Color(0xfaebd7))); // 2.2　体育館

        nishi.buildings.add(new Building(new Vec(505, 260), 10, 5, 1, new Color(0xcd853f))); // 3　第２体育館1 正面
        nishi.buildings.add(new Building(new Vec(510, 250), 40, 70, 3, new Color(0xcd853f))); // 3　第２体育館2 1,2F
        nishi.buildings.add(new Building(new Vec(510, 255), 30, 5, 1, new Color(0xfaf0e6))); // 3　第２体育館3 1F

        // nishi.buildings.add(new Building(new Vec(410, 290), 20, 20, 1, new Color(0xf5f5f5))); // 4　西５号館
        nishi.buildings.add(new Building(new Vec(430, 290), 20, 60, 5, new Color(0xdcdcdc))); // 5　西５号館
        nishi.buildings.add(new Building(new Vec(410, 310), 10, 80, 5, new Color(0xdcdcdc))); // 5　西５号館
        nishi.buildings.add(new Building(new Vec(410, 320), 30, 40, 5, new Color(0xdcdcdc))); // 5　西５号館

        nishi.buildings.add(new Building(new Vec(420, 370), 30, 40, 1, new Color(0xffebcd))); // 6　西食堂
        nishi.buildings.add(new Building(new Vec(460, 350), 50, 30, 1, new Color(0xffebcd))); // 7　西食堂

        nishi.buildings.add(new Building(new Vec(380, 440), 40, 90, 1, new Color(0xd3d3d3))); // 8　教育用計算機室

        nishi.buildings.add(new Building(new Vec(490, 420), 60, 20, 10, new Color(0xfffafa))); // 9　情報システム学研究科棟
        nishi.buildings.add(new Building(new Vec(510, 330), 150, 50, 10, new Color(0xf8f8ff))); // 10　情報システム学研究科棟

        nishi.buildings.add(new Building(new Vec(110, 170), 90, 50, 10, new Color(0xfaf0e6))); // 11　ピクトラボのところ

        nishi.buildings.add(new Building(new Vec(200, 160), 10, 40, 10, new Color(0xf8f8ff))); // 12　西７号館
        nishi.buildings.add(new Building(new Vec(180, 170), 40, 100, 10, new Color(0xf8f8ff))); // 13　西７号館

        nishi.buildings.add(new Building(new Vec(300, 170), 40, 60, 10, new Color(0xfffafa))); // 14　西６号館

        nishi.buildings.add(new Building(new Vec(110, 300), 40, 70, 10, new Color(0xfffaf0))); // 15　西４号館

        nishi.buildings.add(new Building(new Vec(190, 300), 40, 170, 10, new Color(0xffefd5))); // 16　西２号館

        nishi.buildings.add(new Building(new Vec(100, 420), 50, 50, 5, new Color(0xffe4c4))); // 17　西３号館
        nishi.buildings.add(new Building(new Vec(150, 420), 40, 40, 5, new Color(0xffe4c4))); // 18　西３号館

        nishi.buildings.add(new Building(new Vec(220, 420), 40, 100, 5, new Color(0xfaebd7))); // 19　西１号館

        nishi.buildings.add(new Building(new Vec(90, 490), 30, 50, 10, new Color(0xf8f8ff))); // 20　　西９号館
        nishi.buildings.add(new Building(new Vec(110, 520), 40, 30, 10, new Color(0xf8f8ff))); // 21　西９号館
        nishi.buildings.add(new Building(new Vec(80, 560), 40, 100, 10, new Color(0xf8f8ff))); // 22　西９号館

        nishi.buildings.add(new Building(new Vec(210, 490), 50, 40, 1, new Color(0xffebcd))); // 23　　西８号館
        nishi.buildings.add(new Building(new Vec(250, 520), 20, 40, 1, new Color(0xffebcd))); // 24　西８号館
        nishi.buildings.add(new Building(new Vec(290, 490), 50, 50, 1, new Color(0xffebcd))); // 25　西８号館
        nishi.buildings.add(new Building(new Vec(240, 540), 10, 100, 10, new Color(0xfaebd7))); // 26　西８号館
        nishi.buildings.add(new Building(new Vec(190, 550), 30, 110, 10, new Color(0xfaebd7))); // 27　西８号館

        nishi.buildings.add(new Building(new Vec(10, 80), 90, 50, 0.3, new Color(0xd3d3d3))); // 28　プール
        nishi.buildings.add(new Building(new Vec(330, 10), 20, 30, 1, new Color(0xffdead))); // 30　建物１
        nishi.buildings.add(new Building(new Vec(330, 100), 20, 30, 1, new Color(0xffdead))); // 31　建物２

        nishi.buildings.add(new Building(new Vec(0, 0), 590, 10, 0.5, Color.GRAY)); // 32
        nishi.buildings.add(new Building(new Vec(0, 590), 10, 330, 0.5, Color.GRAY)); // 33
        nishi.buildings.add(new Building(new Vec(330, 490), 100, 10, 0.5, Color.GRAY)); // 34
        nishi.buildings.add(new Building(new Vec(330, 490), 10, 260, 0.5, Color.GRAY)); // 35
        nishi.buildings.add(new Building(new Vec(590, 150), 390, 10, 0.5, Color.GRAY)); // 36
        nishi.buildings.add(new Building(new Vec(590, 0), 110, 10, 0.5, Color.GRAY)); // 37
        nishi.buildings.add(new Building(new Vec(0, 0), 10, 590, 0.5, Color.GRAY)); // 38

        for(Building building : nishi.buildings) {

            int x0 = (int)building.pos.getX();
            int y0 = (int)building.pos.getY();
            int width = (int)building.getWidth();
            int vertical = (int)building.getVertical();

            for(int j = y0; j < y0 + vertical; j++) {
                for(int i = x0; i < x0 + width; i++)
                    nishi.Map[i][j] = 1;
            }
        }
        Maps.add(nishi);
    }
    public static void addMap(Map map) {
        Maps.add(map);
    }
}
