package martins.anerua;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FoodSeeder {
	
	private static final int FOOD_WIDTH = 10;
	private static final int FOOD_HEIGHT = 10;
	
	private static int MAX_X, MAX_Y;

	Random random = new Random();
	
	public FoodSeeder(int boardWidth, int boardHeight) {
		MAX_X = boardWidth;
		MAX_Y = boardHeight;
	}
	
	/**
	 * Generates food in a valid position
	 * 
	 * @param snake - the current snake
	 * @return
	 */
	public Ellipse2D.Double seedFood(ArrayList<Ellipse2D.Double> snake) {
		Set<Integer> bad_x = new HashSet<Integer>();
		Set<Integer> bad_y = new HashSet<Integer>();
		
		for (Ellipse2D.Double segment : snake) {
			bad_x.add((int)segment.x);
			bad_y.add((int)segment.y);
		}
	
		int chosen_x = random.nextInt(MAX_X);
		while (bad_x.contains(chosen_x)) {
			chosen_x = random.nextInt(MAX_X);
		}
		
		int chosen_y = random.nextInt(MAX_Y);
		while (bad_y.contains(chosen_y)) {
			chosen_y = random.nextInt(MAX_Y);
		}
		
		return new Ellipse2D.Double(chosen_x, chosen_y, FOOD_WIDTH, FOOD_HEIGHT);
	}

}
