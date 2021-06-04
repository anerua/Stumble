package martins.anerua;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

public class StumbleUI implements ActionListener{

	private JFrame frame;
	private JLabel scoreValue;
	
	private Timer timer;
	
	private Game game;
	
	private static final int FAST = 50;
	private static final int MEDIUM = 100;
	private static final int SLOW = 150;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StumbleUI window = new StumbleUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StumbleUI() {
		timer = new Timer(MEDIUM, this);
		game = new Game();
		
		initialize();
		
		timer.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 642, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Snake Game");
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(game, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{65, 0};
		gbl_panel.rowHeights = new int[]{37, 36, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel scoreLabel = new JLabel();
		scoreLabel.setText("Score:");
		GridBagConstraints gbc_scoreLabel = new GridBagConstraints();
		gbc_scoreLabel.gridx = 0;
		gbc_scoreLabel.gridy = 0;
		panel.add(scoreLabel, gbc_scoreLabel);
		
		scoreValue = new JLabel();
		scoreValue.setText(Integer.toString(0));
		GridBagConstraints gbc_scoreValue = new GridBagConstraints();
		gbc_scoreValue.gridx = 0;
		gbc_scoreValue.gridy = 1;
		panel.add(scoreValue, gbc_scoreValue);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Boolean game_over = game.update();
		scoreValue.setText(Integer.toString(game.getScore()));
		if (game_over) {
			timer.stop();
		}
	}

}
