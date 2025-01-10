import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameView extends JPanel {
    private Main main;
    private Timer gameTimer;
    private int score; // ゲーム中のスコア
    private int playerX; // プレイヤーの位置（てきとー）

    //////////////////////////////////////////////////////
    //大まかにここを変更する。
    public GameView(Main main) {
        this.main = main;
        setLayout(null);
        score = 0;
        playerX = 50;

        // タイマーでゲーム進行
        gameTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaint();
            }
        });
    }

    public void startGame() {
        score = 0;
        playerX = 50;
        gameTimer.start(); // ゲームタイマー開始
    }

    private void updateGame() {
        double r = Math.random();
        playerX += 100*r; // プレイヤーが右に移動
        score += 10; // スコアを加算
        if (playerX > 500) {
            gameTimer.stop(); // ゲーム終了
            main.showScoreView(score); // ScoreView画面に移動
        }
    }
    /////////////////////////////////////////////////////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 白背景を描画
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // プレイヤーを描画
        g.setColor(Color.BLUE);
        g.fillOval(playerX, getHeight() / 2, 30, 30);

        // ゲーム中スコアを描画
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 40);
    }
}
