package Test3D2;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

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