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

	private int[][] edgeListArr;
	private int startY;
	private int endY;

	public EdgeList(int startY, int endY) {
		// TODO fill this in.

		this.startY = startY;
		this.endY = endY;

		this.edgeListArr = new int[endY - startY][4];

	}


	/**Adds a row based on y value, inserts x and z value at appropriate indexes*/
	public void addRow(int y, int xLeft, int xRight, int zLeft, int zRight){

		if(xLeft != 0 && zLeft != 0){
			edgeListArr[y][0] = xLeft;
			edgeListArr[y][1] = zLeft;			//Left List
		}
		if(xRight != 0 && zRight!= 0){
			edgeListArr[y][2] = xRight;
			edgeListArr[y][3] = zRight;		//Right List
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
		return edgeListArr[y][0];
	}

	public float getRightX(int y) {
		// TODO fill this in.
		return edgeListArr[y][2];
	}

	public float getLeftZ(int y) {
		// TODO fill this in.
		return edgeListArr[y][1];
	}

	public float getRightZ(int y) {
		// TODO fill this in.
		return edgeListArr[y][3];
	}

	public int getEdgeListSize(){
		return edgeListArr.length-1;
	}

}

// code for comp261 assignments
