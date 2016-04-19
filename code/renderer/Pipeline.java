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
	private static Vector3D VMid;
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
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		// TODO fill this in.

		//Compute Surface Normal
		Vector3D v1 = poly.vertices[0]; Vector3D v2 = poly.vertices[1]; Vector3D v3 = poly.vertices[2];

		Vector3D a = (v2).minus(v1);			//NOTE: Normal OUTWARDS, CounterClockwise
		Vector3D b = (v3).minus(v2);

		Vector3D normal = a.unitVector().crossProduct(b.unitVector());		//Compute Normal to Surface
	
		float costh = Math.max(normal.unitVector().cosTheta(lightDirection.unitVector()), 0);	//Compute costh
		
		int r = (int) ((ambientLight.getRed() + lightColor.getRed() * costh) * (poly.reflectance.getRed() / 255.0) );
		int g = (int) ((ambientLight.getGreen() + lightColor.getGreen() * costh) * (poly.reflectance.getGreen() / 255.0));
		int bl = (int) ((ambientLight.getBlue() +  lightColor.getBlue() * costh) * (poly.reflectance.getBlue() / 255.0));
		
		return new Color(r,g,bl);
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

		return new Scene(polygons, Rot.multiply(scene.getLight()));
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

		Transform trans = Transform.newTranslation(20, 20, 20);

		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = trans.multiply(vertex);		//Apply transformation to vector
			}
		}

		return new Scene(polygons, trans.multiply(scene.getLight()));

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

		Transform scale = Transform.newScale(2, 2, 2);

		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = scale.multiply(vertex);		//Apply transformation to vector
			}
		}
		return new Scene(polygons, scale.multiply(scene.getLight()));
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		// TODO fill this in.


		if(!isHidden(poly)){
			Vector3D[] vertices = poly.getVertices();

			int minY = getMinY(vertices);
			int maxY = getMaxY(vertices);		//NOTE: Sets VMaxY VMinY Vectors

			for(Vector3D v : vertices){
				if(v != VMaxY && v != VMinY)
					VMid = v;
			}

			EdgeList edge = new EdgeList(minY, maxY);

			//How to determine left or right edge

			updateEdgeList(edge, VMinY, VMaxY, VMid);	//Edge: MaxY - MinY
			updateEdgeList(edge, VMinY, VMid, VMaxY);	//Edge: MinY - MidY
			updateEdgeList(edge, VMid, VMaxY, VMinY);	//Edge: MidY - MaxY

			return edge;
		}



		return null;
	}

	public static void updateEdgeList(EdgeList edge, Vector3D VStart, Vector3D VEnd, Vector3D refP){

		//Floor = lowest integer - round down, ceiling = round up
		
		float mx =	(float) ((((VEnd.x)) - VStart.x) / ((Math.floor(VEnd.y)) - Math.floor(VStart.y)));
		float mz =  (float) ((((VEnd.z)) - (VStart.z)) / ((Math.floor(VEnd.y)) - (Math.floor(VStart.y))) );

		float x =  VStart.x; float z = VStart.z;		//Set x and z
		int i = (int) VStart.y; int maxI = (int)VEnd.y;		//Set indices

		while( i < maxI){

			if(VStart == VMinY && VEnd == VMaxY && refP.x >= VStart.x && refP.x >= VEnd.x)
				edge.addRow(i, x, INF, z, INF);
			else{
				if(VStart == VMinY && VEnd == VMid && refP.x >= VStart.x && refP.x >= VEnd.x)
					edge.addRow(i, x, INF, z, INF);
				else if(VStart == VMid && VEnd == VMaxY && refP.x >= VStart.x && refP.x <= VEnd.x)
					edge.addRow(i, x, INF, z, INF);
				else
					edge.addRow(i, INF, x, INF, z);		//RIGHT
			}
			x += mx;
			z += mz;			//Update values using gradient/slope

			i++;
		}

		if(VStart == VMinY && VEnd == VMaxY && refP.x >= VStart.x && refP.x >= VEnd.x)		//Insert End
			edge.addRow(maxI, VEnd.x, INF, VEnd.z,INF);
		else{
			if(VStart == VMinY && VEnd == VMid && refP.x >= VStart.x && refP.x >= VEnd.x)
				edge.addRow(maxI, VEnd.x, INF, VEnd.z, INF);
			else if(VStart == VMid && VEnd == VMaxY && refP.x >= VStart.x && refP.x <= VEnd.x)
				edge.addRow(maxI, VEnd.x, INF, VEnd.z, INF);
			else
				edge.addRow(maxI, INF, VEnd.x, INF, VEnd.z);
		}
	}

	/**Get minimum y-value from set of vertices*/
	public static int getMinY(Vector3D[] vertices){

		int minY = (int)Double.POSITIVE_INFINITY;

		for(Vector3D v : vertices){
			if(v.y < minY){
				minY = (int)v.y;
				VMinY = v;
			}
		}

		return minY;
	}

	/**Get maximum y-value from set of vertices*/
	public static int getMaxY(Vector3D[] vertices){

		int maxY = 0;

		for(Vector3D v : vertices){
			if(v.y > maxY){
				maxY = (int)v.y;
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

			int x = (int) Math.floor(EL.getLeftX(y));	float z = EL.getLeftZ(y);
			float mz = (EL.getRightZ(y) - EL.getLeftZ(y)) / (EL.getRightX(y) - EL.getLeftX(y));
			
			while( x < Math.floor(EL.getRightX(y))){
				if(z < zDepth[x][y]){
					zDepth[x][y] = z;
					zBuffer[x][y] = polyColor ;
				}
				z += mz;
				x++;
			}
		}

	}
}

// code for comp261 assignments
