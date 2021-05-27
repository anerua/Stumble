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

	private BufferedImage buffer;

	int xLocation = 100, yLocation = 100, snakeWidth = 10, snakeHeight = 10;
	int step = 10;

	ArrayList<Ellipse2D.Double> snake = new ArrayList<Ellipse2D.Double>();

	int direction = STILL; // -1 ==> left, 1 ==> right, -2 ==> up, 2 ==> down, else ==> still
	int prev_direction = STILL;
	boolean x_lock = false, y_lock = false;
	
	FoodSeeder fs;
	Ellipse2D.Double food;
	boolean eaten = true;

	public Game() {

		snake.add(new Ellipse2D.Double(xLocation, yLocation, snakeWidth, snakeHeight));

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
						prev_direction = direction;
						addSegment(LEFT);
						direction = LEFT;
						x_lock = true;
						y_lock = false;
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!x_lock) {
						prev_direction = direction;
						addSegment(RIGHT);
						direction = RIGHT;
						x_lock = true;
						y_lock = false;
					}
					break;
				case KeyEvent.VK_UP:
					if (!y_lock) {
						prev_direction = direction;
						addSegment(UP);
						direction = UP;
						x_lock = false;
						y_lock = true;
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!y_lock) {
						prev_direction = direction;
						addSegment(DOWN);
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
			fs = new FoodSeeder(getWidth(), getHeight());
		}

		Graphics2D g = (Graphics2D) buffer.getGraphics();
		
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
		
		// draw snake
		g.setColor(Color.BLUE);
		for (Ellipse2D.Double segment : snake) {
			g.fill(segment);
		}

		g1.drawImage(buffer, 0, 0, null);
	}

	private void updateSnakePosition(double snake_x, double snake_y) {
		if (snake_x > getWidth()) {
			snake_x = 0 - snakeWidth;
		} else if (snake_x < 0 - snakeWidth) {
			snake_x = getWidth();
		}
		if (snake_y > getHeight()) {
			snake_y = 0 - snakeHeight;
		} else if (snake_y < 0 - snakeHeight) {
			snake_y = getHeight();
		}

		snake.get(0).x = snake_x;
		snake.get(0).y = snake_y;
	}

	private void updateSnakeSegmentPosition(double old_x, double old_y) {
		double new_x = old_x, new_y = old_y;
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

	private void addSegment(int new_direction) {
		double tail_x = snake.get(snake.size() - 1).x;
		double tail_y = snake.get(snake.size() - 1).y;

		double new_x = 0, new_y = 0;
		
		switch (prev_direction) {
		case LEFT:
			new_x = tail_x + 10;
			break;
		case RIGHT:
			new_x = tail_x - 10;
			break;
		case UP:
			new_y = tail_y + 10;
			break;
		case DOWN:
			new_y = tail_y - 10;
			break;
		case STILL:
			switch (new_direction) {
			case LEFT:
				new_x = tail_x + 10;
				break;
			case RIGHT:
				new_x = tail_x - 10;
				break;
			case UP:
				new_y = tail_y + 10;
				break;
			case DOWN:
				new_y = tail_y - 10;
				break;
			}
			break;
		}

		snake.add(new Ellipse2D.Double(new_x, new_y, snakeWidth, snakeHeight));
	}

	public void update() {
		Ellipse2D.Double snakeHead = snake.get(0);

		double new_x = snakeHead.x;
		double new_y = snakeHead.y;

		double old_x = new_x, old_y = new_y;

		switch (direction) {
		case LEFT:
			new_x = snakeHead.x - step;
			break;
		case RIGHT:
			new_x = snakeHead.x + step;
			break;
		case UP:
			new_y = snakeHead.y - step;
			break;
		case DOWN:
			new_y = snakeHead.y + step;
			break;
		}

		updateSnakePosition(new_x, new_y);

		updateSnakeSegmentPosition(old_x, old_y);
		
		if (!eaten) {
			if (snakeHead.intersects(food.getBounds2D())) {
				eaten = true;
			}
		}

		repaint();
	}

}
