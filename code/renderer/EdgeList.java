package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {

	private float[][] edgeListArr;
	private int startY;
	private int endY;
	private Map<Integer, Integer> indexMap;
	public static int INF = (int)Double.POSITIVE_INFINITY;

	public EdgeList(int startY, int endY) {
		// TODO fill this in.

		this.startY = startY;
		this.endY = endY;
		//EndY is negative - must be positive size
		this.edgeListArr = new float[endY+1][4];
		
		for(int row = 0; row < edgeListArr.length; row++){
			edgeListArr[row][0] = INF;
			edgeListArr[row][1] = INF;
			edgeListArr[row][2] = -INF;
			edgeListArr[row][3] = INF;
		}
		
		this.indexMap = new LinkedHashMap<Integer, Integer>();
		
		int i = 0;
		for(int j = startY; j <= endY; j++){
			indexMap.put(j, i);
			i++;
		}
		
	}


	/**Adds a row based on y value, inserts x and z value at appropriate indexes*/
	public void addRow(int y, float xLeft, float xRight, float zLeft, float zRight){
		
//		int y = 0;
//		if(i >= 0 && i < edgeListArr.length)
//			y = i;
//		else 
//			y = getMappingIndex(i);
		
		if(xLeft != INF && zLeft != INF){
			edgeListArr[y][0] = xLeft;
			edgeListArr[y][1] = zLeft;			//Left List
		}
		if(xRight != INF && zRight!= INF){
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
//		if(indexMap.containsKey(y))
//			return edgeListArr[getMappingIndex(y)][0];
		
		return edgeListArr[y][0];

	}

	public float getRightX(int y) {
		// TODO fill this in.
		
//		if(indexMap.containsKey(y))
//			return edgeListArr[getMappingIndex(y)][2];

		return edgeListArr[y][2];
	}

	public float getLeftZ(int y) {
		// TODO fill this in.
//		if(indexMap.containsKey(y))
//			return edgeListArr[getMappingIndex(y)][1];

		return edgeListArr[y][1];
	}

	public float getRightZ(int y) {
		// TODO fill this in.
//		if(indexMap.containsKey(y))
//			return edgeListArr[getMappingIndex(y)][3];

		return edgeListArr[y][3];
	}

	public int getEdgeListSize(){
		return edgeListArr.length;
	}
	
	public int getMappingIndex(int y){
		
		int index = 0;
		
		for(Map.Entry<Integer, Integer> e : indexMap.entrySet()){
			if( e.getKey() == y)
					index = e.getValue();
		} 
		
		return index;
		
	}

}

// code for comp261 assignments
