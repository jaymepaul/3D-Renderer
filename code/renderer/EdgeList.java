package renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {

	private int[][] edgeListLeft;
	private int[][] edgeListRight;
	private int startY;
	private int endY;

	public EdgeList(int startY, int endY) {
		// TODO fill this in.

		this.startY = startY;
		this.endY = endY;

		this.edgeListLeft = new int[endY - startY][2];
		this.edgeListRight = new int[endY - startY][2];

	}


	/**Adds a row based on y value, inserts x and z value at appropriate indexes*/
	public void addRow(int y, int xLeft, int xRight, int zLeft, int zRight){

		if(xLeft != 0 && zLeft != 0){
			edgeListLeft[y][0] = xLeft;
			edgeListLeft[y][1] = zLeft;			//Left List
		}
		if(xRight != 0 && zRight!= 0){
			edgeListRight[y][0] = xRight;
			edgeListRight[y][1] = zRight;		//Right List
		}
	}

	public int getStartY() {
		// TODO fill this in.
		return startY;
	}

	public int getEndY() {
		// TODO fill this in.
		return endY;
	}

	public float getLeftX(int y) {
		// TODO fill this in.
		return edgeListLeft[y][0];
	}

	public float getRightX(int y) {
		// TODO fill this in.
		return edgeListRight[y][0];
	}

	public float getLeftZ(int y) {
		// TODO fill this in.
		return edgeListLeft[y][1];
	}

	public float getRightZ(int y) {
		// TODO fill this in.
		return edgeListRight[y][1];
	}


}

// code for comp261 assignments
