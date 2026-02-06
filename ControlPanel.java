import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.FlowLayout;


public class ControlPanel {
    private JSlider rychlostSlider;
    private JLabel rychlostLabel;
    private JButton generateMazeButton;
    private JButton resetMazeButton;
    private JButton dijkstraButton;
    private JButton mazeExitButton;
    private JButton aStarButton;
    private JButton bfsButton;
    private JButton playerButton;

    private GameEngine gameEngine;

    public ControlPanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;

        int maxRychlost = gameEngine.getTikSpeed() + 200;
        int initialRychlost = gameEngine.getTikSpeed();

        this.rychlostLabel = new JLabel("Rýchlosť: " + initialRychlost);
        this.rychlostSlider = new JSlider(0, maxRychlost, initialRychlost);
        this.rychlostSlider.addChangeListener(e -> {
            int novaRychlost = maxRychlost - this.rychlostSlider.getValue();
            this.gameEngine.setTikSpeed(novaRychlost);
            this.rychlostLabel.setText("Rýchlosť: " + novaRychlost);
        });
        

        this.generateMazeButton = new JButton("Generate Maze");
        this.generateMazeButton.addActionListener(e -> new Thread(gameEngine::generateMaze).start());

        this.resetMazeButton = new JButton("Reset Maze");
        this.resetMazeButton.addActionListener(e -> new Thread(gameEngine::resetMaze).start());

        this.dijkstraButton = new JButton("Dijkstra Algorithm");
        this.dijkstraButton.addActionListener(e -> new Thread(gameEngine::dijkstraAlgorithm).start());

        this.mazeExitButton = new JButton("Show Maze Exit");
        this.mazeExitButton.addActionListener(e -> new Thread(gameEngine::toggleMazeEntryAndExitDisplay).start());

        this.aStarButton = new JButton("A*");
        this.aStarButton.addActionListener(e -> new Thread(gameEngine::aStarAlgorithm).start());

        this.bfsButton = new JButton("BFS");
        this.bfsButton.addActionListener(e -> new Thread(gameEngine::bfsAlgorithm).start());

        this.playerButton = new JButton("Spawn Player");
        this.playerButton.addActionListener(e -> new Thread(gameEngine::spawnPlayer).start());

        JFrame frame = new JFrame("Ovládací Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sliderPanel.add(this.rychlostLabel);
        sliderPanel.add(this.rychlostSlider);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(this.generateMazeButton);
        buttonPanel.add(this.resetMazeButton);
        buttonPanel.add(this.dijkstraButton);
        buttonPanel.add(this.aStarButton);
        buttonPanel.add(this.bfsButton);
        buttonPanel.add(this.playerButton);
        buttonPanel.add(this.mazeExitButton);

        panel.add(sliderPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(buttonPanel);

        frame.add(panel);
        frame.setFocusable(false); 
        frame.setVisible(true);
    }
}
