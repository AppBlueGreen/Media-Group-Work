import javax.swing.*;

public class Main {
    private JFrame frame;
    private StartView startView;
    private CourseSelectView courseSelectView;
    private GameView gameView;
    private ScoreView scoreView;

    public Main() {
        frame = new JFrame("SHOOTING WAR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        startView = new StartView(this);
        courseSelectView = new CourseSelectView(this);
        gameView = new GameView(this);
        scoreView = new ScoreView(this);

        frame.add(startView);
        frame.setVisible(true);
    }

    public void showStartView() {
        frame.getContentPane().removeAll();
        frame.add(startView);
        frame.revalidate();
        frame.repaint();
        startView.requestFocusInWindow();
    }

    public void showCourseSelectView() {
        frame.getContentPane().removeAll();
        frame.add(courseSelectView);
        frame.revalidate();
        frame.repaint();
        courseSelectView.requestFocusInWindow();
    }

    public void showGameView() {
        frame.getContentPane().removeAll();
        frame.add(gameView);
        frame.revalidate();
        frame.repaint();
        gameView.startGame();
    }

    public void showScoreView(int score) {
        frame.getContentPane().removeAll();
        scoreView.setScore(score);
        frame.add(scoreView);
        frame.revalidate();
        frame.repaint();
        scoreView.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new Main();
    }
}
