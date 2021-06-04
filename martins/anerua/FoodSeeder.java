package martins.anerua;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

public class FoodSeeder {
	
	private static final int FOOD_WIDTH = 10;
	private static final int FOOD_HEIGHT = 10;
	
	private static int MAX_X, MAX_Y;

	Random random = new Random();
	
	public FoodSeeder(int boardWidth, int boardHeight) {
		MAX_X = boardWidth - Game.SNAKE_SEGMENT_WIDTH;
		MAX_Y = boardHeight - Game.SNAKE_SEGMENT_HEIGHT;
	}
	
	/**
	 * Checks if food location intercepts location of any snake segment
	 * 
	 * @param snake - the current snake
	 * @param food - the food being checked
	 * @return - returns true if food intercepts any snake segment, false otherwise
	 */
	private Boolean snakeIntersects(ArrayList<Ellipse2D.Double> snake, Ellipse2D.Double food) {
		for (Ellipse2D.Double segment : snake) {
			if (food.intersects(segment.getBounds2D())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Generates food in a valid position
	 * 
	 * @param snake - the current snake
	 * @return - returns generated food
	 */
	public Ellipse2D.Double seedFood(ArrayList<Ellipse2D.Double> snake) {
		int chosen_x = random.nextInt(MAX_X);
		int chosen_y = random.nextInt(MAX_Y);
		Ellipse2D.Double food = new Ellipse2D.Double(chosen_x, chosen_y, FOOD_WIDTH, FOOD_HEIGHT);
		
		while (snakeIntersects(snake, food)) {
			chosen_x = random.nextInt(MAX_X);
			chosen_y = random.nextInt(MAX_Y);
			food = new Ellipse2D.Double(chosen_x, chosen_y, FOOD_WIDTH, FOOD_HEIGHT);
		}
		
		return food;
	}

}
