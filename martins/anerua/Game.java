package martins.anerua;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Game extends JComponent {

	private static final int LEFT = -1, RIGHT = 1, DOWN = -2, UP = 2, STILL = 0;
	protected static final int SNAKE_SEGMENT_WIDTH = 10, SNAKE_SEGMENT_HEIGHT = 10;
	protected static final int SNAKE_SPEED = 10;
	private static final int START_X = 300, START_Y = 200;

	private BufferedImage buffer;


	private ArrayList<Ellipse2D.Double> snake = new ArrayList<Ellipse2D.Double>();

	private int direction = STILL;
	private boolean x_lock = false, y_lock = false;

	private FoodSeeder fs;
	private Ellipse2D.Double food;
	private boolean eaten = true, grow = false;

	private double tail_x = START_X, tail_y = START_Y; // position of snake tail
	
	private int score = 0;

	public Game() {

		snake.add(new Ellipse2D.Double(START_X, START_Y, SNAKE_SEGMENT_WIDTH, SNAKE_SEGMENT_HEIGHT));

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				buffer = null;
			}

		});

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {

				int key = e.getKeyCode();

				switch (key) {
				case KeyEvent.VK_LEFT:
					if (!x_lock) {
						direction = LEFT;
						x_lock = true;
						y_lock = false;
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!x_lock) {
						direction = RIGHT;
						x_lock = true;
						y_lock = false;
					}
					break;
				case KeyEvent.VK_UP:
					if (!y_lock) {
						direction = UP;
						x_lock = false;
						y_lock = true;
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!y_lock) {
						direction = DOWN;
						x_lock = false;
						y_lock = true;
					}
					break;
				}

				return false;
			}

		});
	}

	@Override
	protected void paintComponent(Graphics g1) {

		if (buffer == null) {
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

			fs = new FoodSeeder(getWidth(), getHeight()); // define FoodSeeder here to obtain non-zero width and height
															// of JComponent
		}

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		// If food has been eaten, generate a new food
		if (eaten) {
			food = fs.seedFood(snake);
			eaten = false;
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// paint board
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw food
		g.setColor(Color.red);
		g.fill(food);

		
		// draw snake head
		g.setColor(Color.blue);
		g.fill(snake.get(0));
		
		// draw snake
		g.setColor(Color.green);
		for (int i = 1; i < snake.size(); i++) {
			g.fill(snake.get(i));
		}

		g1.drawImage(buffer, 0, 0, null);
	}

	/**
	 * Moves the snake head to snakeHead_x and snakeHead_y position.
	 * 
	 * @param snakeHead_x - new x position of snake head
	 * @param snakeHead_y - new y position of snake head
	 */
	private void updateSnakeHeadPosition(double snakeHead_x, double snakeHead_y) {
		if (snakeHead_x > getWidth() - SNAKE_SEGMENT_WIDTH) {
			snakeHead_x = 0;
		} else if (snakeHead_x < 0) {
			snakeHead_x = getWidth() - SNAKE_SEGMENT_WIDTH;
		}
		if (snakeHead_y > getHeight() - SNAKE_SEGMENT_HEIGHT) {
			snakeHead_y = 0;
		} else if (snakeHead_y < 0) {
			snakeHead_y = getHeight() - SNAKE_SEGMENT_HEIGHT;
		}

		snake.get(0).x = snakeHead_x;
		snake.get(0).y = snakeHead_y;
	}

	/**
	 * Moves other snake segment in line with snake head.
	 * 
	 * @param oldSnakeHead_x - previous x position of snake head
	 * @param oldSnakeHead_y - previous y position of snake head
	 */
	private void updateSnakeSegmentPosition(double oldSnakeHead_x, double oldSnakeHead_y) {
		double new_x = oldSnakeHead_x, new_y = oldSnakeHead_y;
		double temp_x = new_x, temp_y = new_y;
		
		for (int i = 1; i < snake.size(); i++) {
			temp_x = snake.get(i).x;
			temp_y = snake.get(i).y;
			snake.get(i).x = new_x;
			snake.get(i).y = new_y;
			new_x = temp_x;
			new_y = temp_y;
		}
	}

	/**
	 * Moves snake according to key input and step.
	 * 
	 * @param oldSnakeHead_x - old x position of snake head
	 * @param oldSnakeHead_y - old y position of snake head
	 * @param newSnakeHead_x - new x position of snake head
	 * @param newSnakeHead_y - new y position of snake head
	 */
	private void moveSnake(double oldSnakeHead_x, double oldSnakeHead_y, double newSnakeHead_x, double newSnakeHead_y) {
		updateSnakeHeadPosition(newSnakeHead_x, newSnakeHead_y);
		updateSnakeSegmentPosition(oldSnakeHead_x, oldSnakeHead_y);
	}

	/**
	 * Adds a snake segment at previous position of snake tail
	 */
	private void growSnake() {
		snake.add(new Ellipse2D.Double(tail_x, tail_y, SNAKE_SEGMENT_WIDTH, SNAKE_SEGMENT_HEIGHT));
	}
	
	/**
	 * Getter method for getting current game score
	 * 
	 * @return current game score
	 */
	protected int getScore() {
		return score;
	}

	/**
	 * Main game repeating function. Follows Timer in Main UI.
	 * 
	 * @return true if game is over, false otherwise
	 */
	protected Boolean update() {

		if (grow) {
			growSnake();
			grow = false;
		}

		Ellipse2D.Double snakeHead = snake.get(0);

		double newSnakeHead_x = snakeHead.x;
		double newSnakeHead_y = snakeHead.y;

		double oldSnakeHead_x = newSnakeHead_x, oldSnakeHead_y = newSnakeHead_y;

		switch (direction) {
		case LEFT:
			newSnakeHead_x = snakeHead.x - SNAKE_SPEED;
			break;
		case RIGHT:
			newSnakeHead_x = snakeHead.x + SNAKE_SPEED;
			break;
		case UP:
			newSnakeHead_y = snakeHead.y - SNAKE_SPEED;
			break;
		case DOWN:
			newSnakeHead_y = snakeHead.y + SNAKE_SPEED;
			break;
		}

		moveSnake(oldSnakeHead_x, oldSnakeHead_y, newSnakeHead_x, newSnakeHead_y);
		
		for (int i = 1; i < snake.size(); i++) {
			if (snakeHead.intersects(snake.get(i).getBounds2D())) {
				return true;  // return true to stop game timer
			}
		}

		if (!eaten) {
			if (snakeHead.intersects(food.getBounds2D())) {
				eaten = true;
				grow = true;
				++score;
				tail_x = snake.get(snake.size() - 1).x;
				tail_y = snake.get(snake.size() - 1).y;
			}
		}

		repaint();
		
		return false;  // game not yet over
	}

}
