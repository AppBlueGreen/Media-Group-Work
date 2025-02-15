package Test3D2;

import java.awt.GridLayout;

import javax.swing.*;

public class Main {
    private JFrame frame;
    private StartView startView;
    private CourseSelectView courseSelectView;
    // private ShootingGameView gameView;
    private View2D view2d;
    private View3D view3d;
    private ScoreView scoreView;


    public Main() {
        frame = new JFrame("SHOOTING WAR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        startView = new StartView(this);
        courseSelectView = new CourseSelectView(this);
        // gameView = new ShootingGameView(this);
        // view2d = new View2D(this);
        // view3d = new View3D(this);
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

    public void showGameView(View2D view2d, View3D view3d) {
        this.view2d = view2d;
        this.view3d = view3d;
        frame.getContentPane().removeAll(); 
    
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(frame.getSize());
    
        // View3D（背景）を追加
        view3d.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(view3d, Integer.valueOf(1)); // 背景として低いレイヤー
    
        // View2D（前面）を追加
        view2d.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(view2d, Integer.valueOf(2)); // 前面に表示するために高いレイヤー
    
        frame.add(layeredPane);
        frame.revalidate();
        frame.repaint();
        view2d.requestFocusInWindow();
    }

    public void showScoreView(int score) {
        frame.getContentPane().removeAll();
        scoreView.setScore(score);
        frame.add(scoreView);
        frame.revalidate();
        frame.repaint();
        scoreView.requestFocusInWindow();
    }
    public View3D getView3D() {
        return view3d;
    }

    public static void main(String[] args) {
        new Main();
    }
}
