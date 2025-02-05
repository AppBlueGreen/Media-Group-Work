package Test3D;

import hoge.Vec;

// 弾のクラス
class Bullet {
    Vec pos;
    int width = 10;
    int height = 20;

    public Bullet(Vec pos) {
        this.pos = pos;
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
    Vec pos;
    int size = 30;

    public Enemy(Vec pos) {
        this.pos = pos;
    }
}
