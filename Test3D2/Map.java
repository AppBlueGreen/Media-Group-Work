package Test3D2;

import java.util.ArrayList;

public class Map {
    // フィールドの大きさ
    public int field_width;
    public int field_height;

    public Player player;

    // 敵とのリスト
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Ray> fieldWalls = new ArrayList<>();
    public ArrayList<Boss> bosses = new ArrayList<>(); 
    public ArrayList<Building> buildings = new ArrayList<>();

    public int Map[][]; 

    Map(int field_width, int field_height, Vec playerPos, double playerAngle){
        this.field_width = field_width;
        this.field_height = field_height;
        player = new Player(playerPos, playerAngle);
        Map = new int[field_width][field_height];
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(field_width, 0).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(0, 0), new Vec(0, field_height).sub(new Vec(0, 0))));
        fieldWalls.add(new Ray(new Vec(field_width, field_height), new Vec(field_width, 0).sub(new Vec(field_width, field_height))));
        fieldWalls.add(new Ray(new Vec(field_width, field_height), new Vec(0, field_height).sub(new Vec(field_width, field_height))));
    }


}
