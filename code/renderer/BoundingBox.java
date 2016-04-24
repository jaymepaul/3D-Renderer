package renderer;

public class BoundingBox {

	private float width;
	private float height;
	public float minX, maxX, minY, maxY;
	public float centerX, centerY;
	
	private final int CANVAS_HEIGHT = 600;
	private final int CANVAS_WIDTH = 600;
	
	
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
		if(Math.floor(centerX) > Math.floor(CANVAS_WIDTH / 2))		//SHIFT LEFT
			return (int) (Math.floor(centerX) - Math.floor(CANVAS_WIDTH / 2));
		else 
			return (int) Math.floor((CANVAS_WIDTH / 2) - Math.floor(centerX));	//SHIFT RIGHT
	}
	
	public int shiftY(){
		if(Math.floor(centerY) > Math.floor(CANVAS_HEIGHT / 2))
			return (int) (Math.floor(centerY) - Math.floor(CANVAS_HEIGHT / 2));	//SHIFT DOWN
		else 
			return (int) Math.floor((CANVAS_HEIGHT / 2) - Math.floor(centerY));	//SHIFT UP
	}
	
	public float scaleFactor(){
		
		System.out.println("WINDOW WIDTH: "+CANVAS_WIDTH+"BB WIDTH: "+width);
	
		return (CANVAS_WIDTH/2) / width;
			
	}

	
	
}
