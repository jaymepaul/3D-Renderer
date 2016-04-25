package renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 *
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	private static Vector3D VMinY;		//MinY Vector
	private static Vector3D VMaxY;
	public static int INF = (int)Double.POSITIVE_INFINITY;

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		// TODO fill this in.

		Vector3D v1 = poly.vertices[0]; Vector3D v2 = poly.vertices[1]; Vector3D v3 = poly.vertices[2];

		Vector3D a = v2.minus(v1);
		Vector3D b = v3.minus(v2);

		Vector3D normal = a.crossProduct(b);		//Compute Normal to Surface

		if( normal.z > 0 )
			return true;

		return false;
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 *
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D[] lightSources, Color[] lightColors, Color ambientLight) {
		// TODO fill this in.

		//Compute Surface Normal
		Vector3D v1 = poly.vertices[0]; Vector3D v2 = poly.vertices[1]; Vector3D v3 = poly.vertices[2];

		Vector3D e1 = (v2).minus(v1);			//NOTE: Normal OUTWARDS, CounterClockwise
		Vector3D e2 = (v3).minus(v2);

		Vector3D normal = e1.unitVector().crossProduct(e2.unitVector());		//Compute Normal to Surface
	
		float iR =0.0f, iG =0.0f, iB = 0.0f;											//Compute Sum of Diffuse Light Reflection
		for(int i = 0; i < lightSources.length; i++){
			float costh = Math.max(normal.unitVector().cosTheta(lightSources[i]), 0.0f);
			float pR = lightColors[i].getRed() * costh;		iR += pR;
			float pG = lightColors[i].getGreen() * costh; 	iG += pG;
			float pB = lightColors[i].getBlue() * costh; 	iB += pB;
		}
		

		int r = (int) Math.min(((ambientLight.getRed() + iR) * (poly.reflectance.getRed()) / 255.0), 255);
		int g = (int) Math.min(((ambientLight.getGreen() + iG) * (poly.reflectance.getGreen()) / 255.0), 255);
		int b = (int) Math.min(((ambientLight.getBlue() +  iB) * (poly.reflectance.getBlue()) / 255.0) , 255);
		
		return new Color(r,g,b);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 *
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		// TODO fill this in.

		List<Polygon> polygons = scene.getPolygons();

		Transform RotX = Transform.newXRotation(xRot);
		Transform RotY = Transform.newYRotation(yRot);
		Transform Rot = RotX.compose(RotY);

		for(Polygon p : polygons){					//Rotate each polygon
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D rotV = Rot.multiply(p.getVertices()[i]);		
				p.getVertices()[i] = rotV;
			}
		}

		for(int i = 0; i < scene.getLightSources().length; i++){			//Apply to light sources
			Vector3D rotV = Rot.multiply(scene.getLightSources()[i]);
			scene.getLightSources()[i] = rotV;
		}
		
		return new Scene(polygons, scene.getLightSources());
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 *
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		// TODO fill this in.

		List<Polygon> polygons = scene.getPolygons();

		//Compute bounding box, translate accordingly
		System.out.println("CenterX: "+scene.getBoundingBox().centerX+" CenterY: "+scene.getBoundingBox().centerY+"MinX: "+scene.getBoundingBox().minX+" MaxX: "+scene.getBoundingBox().maxX+" MinY: "+scene.getBoundingBox().minY+" MaxY: "+scene.getBoundingBox().maxY);
		System.out.println("ShiftX: "+scene.getBoundingBox().shiftX() +","+ "ShiftY:"+scene.getBoundingBox().shiftY());
		
		Transform trans = Transform.newTranslation(scene.getBoundingBox().shiftX(), scene.getBoundingBox().shiftY(), 0);
		
		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = trans.multiply(vertex);		//Apply transformation to vector
			}
		}
		
		for(int i = 0; i < scene.getLightSources().length; i++){			//Apply to light sources
			Vector3D transV = trans.multiply(scene.getLightSources()[i]);
			scene.getLightSources()[i] = transV;
		}

		return new Scene(polygons, scene.getLightSources());

	}

	/**
	 * This should scale the scene.
	 *
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// TODO fill this in.
		List<Polygon> polygons = scene.getPolygons();

		//Compute bounding box, scale accordingly
		float scaleFactor = scene.getBoundingBox().scaleFactor();
		System.out.println("ScaleFACTOR: "+ scaleFactor);
		Transform scale = Transform.newScale(scaleFactor, scaleFactor, scaleFactor);

		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = scale.multiply(vertex);		//Apply transformation to vector
			}
		}
		
		for(int i = 0; i < scene.getLightSources().length; i++){			//Apply to light sources
			Vector3D scaleV = scale.multiply(scene.getLightSources()[i]);
			scene.getLightSources()[i] = scaleV;
		}
		
		return new Scene(polygons, scene.getLightSources());
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		// TODO fill this in.

		Vector3D[] vertices = poly.getVertices();
		
		int minY = getMinY(vertices);
		int maxY = getMaxY(vertices);		//NOTE: Sets VMaxY VMinY Vectors
		EdgeList edge = new EdgeList(minY, maxY);
		
		Vector3D VMidY = null;
		for(Vector3D v : vertices){
			if(v != VMinY && v != VMaxY)
				VMidY = v;
		}
		Vector3D VStart = VMinY; Vector3D VEnd = VMaxY;
		
		//Along each edge:
		while(true){
			
			float mx =	(float) ((((VEnd.x)) - VStart.x) / ((Math.floor(VEnd.y)) - Math.floor(VStart.y)));
			float mz =  (float) ((((VEnd.z)) - (VStart.z)) / ((Math.floor(VEnd.y)) - (Math.floor(VStart.y))) );
	
			float x =  VStart.x; float z = VStart.z;	//Set x and z
			int i = (int) Math.floor(VStart.y);	int maxI = (int) Math.floor(VEnd.y);

			while( i <= maxI){
				
				//Determine if left or right
				if( x < edge.getLeftX(i))
					edge.addRow(i, x, INF, z, INF);		//LEFT
				if( x > edge.getRightX(i))
					edge.addRow(i, INF, x, INF, z);		//RIGHT
				
				x += mx;
				z += mz;
				i++;
			}
			
		
			if( x < edge.getLeftX(maxI))							//ADD END
				edge.addRow(maxI, VEnd.x, INF, VEnd.z, INF);		//LEFT
			else if( x > edge.getRightX(maxI))
				edge.addRow(maxI, INF, VEnd.x, INF, VEnd.z);		//RIGHT
			
			
			//Determine Next Edge
			if(VStart == VMinY && VEnd == VMaxY){		//EDGE: VMinY - VMaxY
				VEnd = VMidY;
				continue;
			}
			else if(VStart == VMinY && VEnd == VMidY){	//EDGE: VMinY - VMidY
				VStart = VMidY;	VEnd  = VMaxY;
				continue;
			}
			else if(VStart == VMidY && VEnd == VMaxY)	//EDGE: VMidY - VMaxY
				break;
			
			
		}
		return edge;
	}
	
	public static Vector3D setMidX(Vector3D[] vertices){
		
		//Get max, min
		float max = 0, min = INF;

		for(Vector3D v : vertices){
			if(v.x > max)
				max = v.x;
			else if(v.x < min)
				min = v.x;
		}

		for(Vector3D v : vertices){
			if(v.x != max && v.x != min)
				return v;
		}

		return null;
	}

	/**Get minimum y-value from set of vertices*/
	public static int getMinY(Vector3D[] vertices){

		int minY = (int)Double.POSITIVE_INFINITY;

		for(Vector3D v : vertices){
			if(v.y < minY){
				minY = (int) Math.floor(v.y);
				VMinY = v;
			}
		}

		return minY;
	}

	/**Get maximum y-value from set of vertices*/
	public static int getMaxY(Vector3D[] vertices){

		int maxY = (int)Double.NEGATIVE_INFINITY;

		for(Vector3D v : vertices){
			if(v.y > maxY){
				maxY = (int) Math.floor(v.y);
				VMaxY = v;
			}
		}

		return maxY;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 *
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 *
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far. zbuffer [pixel][val]
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far. Ie. float [pixel][z-val]
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer / SHADING
	 */
	public static void computeZBuffer(Color[][] zBuffer, float[][] zDepth, EdgeList EL, Color polyColor) {
		// TODO fill this in.

		
		for(int y = 0; y < EL.getEdgeListSize(); y++){

			int x = (int) Math.floor(EL.getLeftX(y));		//CHECK: IF MAX IS RIGHT
			float z = EL.getLeftZ(y);
			float mz = (EL.getRightZ(y) - EL.getLeftZ(y)) / (EL.getRightX(y) - EL.getLeftX(y));
			
			if(x < INF && x > -INF){
				while( x < (int)Math.floor(EL.getRightX(y))){		///Dont fill if outside edgeList boundaries
					if(x < zBuffer[0].length && y < zBuffer.length){	
						if(z < zDepth[x][y]){
							zDepth[x][y] = z;
							zBuffer[x][y] = polyColor;
						}
						
					}
					z += mz;
					x++;
				}
			}
		}
	}
	
	
	
}

// code for comp261 assignments
