package renderer;

public class BoundingBox {

	private float width;
	private float height;
	public float minX, maxX, minY, maxY;
	public float centerX, centerY;
	
	public BoundingBox(float minX, float maxX, float minY, float maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		
		this.width = Math.abs(maxX - minX);
		this.height = Math.abs(maxY - minY);
		
		this.centerX = minX + (width/2);
		this.centerY = minY + (height/2);
	}
	
	public int shiftX(){	
		return (int) Math.floor((GUI.CANVAS_WIDTH / 2) - Math.floor(centerX));	//SHIFT RIGHT
	}
	
	public int shiftY(){		
		return (int) Math.floor((GUI.CANVAS_HEIGHT / 2) - Math.floor(centerY));	//SHIFT UP
	}
	
	public float scaleFactor(){
		
//		System.out.println("WINDOW WIDTH: "+CANVAS_WIDTH+"BB WIDTH: "+width);
//	
		return (GUI.CANVAS_WIDTH/2.5f) / width;
			
	}

	
	
}
