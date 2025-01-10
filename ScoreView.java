import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScoreView extends JPanel {
    private Main main;
    private JLabel scoreLabel;
    private ArrayList<Integer> topScores;
    private int currentScore;

    public ScoreView(Main main) {
        this.topScores = new ArrayList<>();
        this.currentScore = 0;
        this.setPreferredSize(new Dimension(200, 200));
        this.main = main;
        setLayout(new BorderLayout());

        //ScoreView画面の背景、うまくできない
        JLabel startbackimage = new JLabel("<html><img src='file:start5.jpg' width=805 height=580></html>", JLabel.CENTER);
        add(startbackimage, BorderLayout.CENTER);

        // Game Clearの文字
        JLabel titleLabel = new JLabel("Game Clear!!", JLabel.CENTER);
        titleLabel.setFont(new Font("MS Gothic", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        // Your Scoreの文字
        scoreLabel = new JLabel("Your Score: 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        add(scoreLabel, BorderLayout.CENTER);

        // Course Select画面に移動するボタン
        JButton restartButton = new JButton("コース選択画面へ");
        restartButton.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showCourseSelectView(); // スタート画面に戻る
            }
        });
        add(restartButton, BorderLayout.SOUTH);
    }

    public void setScore(int score) {
        currentScore = score; // 現在のスコアを更新
        scoreLabel.setText("Your Score: " + score); // スコアを表示
        addScore(score); // トップスコアに追加
    }

    public void addScore(int score) {
        topScores.add(score);

        // トップ5スコアを保持
        topScores.sort(Collections.reverseOrder());
        if (topScores.size() > 5) {
            topScores = new ArrayList<>(topScores.subList(0, 5));
        }
        repaint();
    }

    public void resetCurrentScore() {
        currentScore = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.setColor(Color.BLACK);
        // トップ5スコアを描画
        g.setFont(new Font("MS Gothic", Font.BOLD, 24));
        g.drawString("Top 5 Scores", 10, 40);
        for (int i = 0; i < topScores.size(); i++) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString((i + 1) + ": " + topScores.get(i), 10, 70 + i * 20);
        }
    }
}
