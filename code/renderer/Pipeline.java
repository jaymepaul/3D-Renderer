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

	private static Vector3D Va;		//MinY Vector
	private static Vector3D VMaxY;

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

		Vector3D a1 = v2.minus(v1);
		Vector3D b2 = v3.minus(v2);

		Vector3D n = a1.crossProduct(b2);		//Compute Normal to Surface

		float nFactor = (float) Math.sqrt(Math.pow(n.x, 2) + Math.pow(n.y, 2) + Math.pow(n.z, 2));
		Vector3D unitNormal = new Vector3D( n.x / nFactor, n.x / nFactor, n.x / nFactor);	//Compute Unit Normal

		float costh = unitNormal.cosTheta(lightDirection);

		int r = (int) ((ambientLight.getRed() * costh) * lightColor.getRed());
		int g = (int) ((ambientLight.getGreen() * costh) * lightColor.getGreen());
		int b = (int) ((ambientLight.getBlue() * costh) * lightColor.getBlue());

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

		List<Polygon> polygons = new ArrayList<Polygon>();
		Vector3D lightPos = null;

		for(Polygon p : scene.getPolygons()){					//Rotate each polygon

			Transform rotX = Transform.newXRotation(xRot);
			Transform rotY = Transform.newYRotation(yRot);
			Transform rot = rotX.compose(rotY);					//Combine Matrices

			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = rot.multiply(vertex);		//Apply Rotation to vector
			}
		}

		return new Scene(polygons, lightPos);
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
		Vector3D lightPos = scene.getLight();

		Transform trans = Transform.newTranslation(20, 20, 20);

		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = trans.multiply(vertex);		//Apply transformation to vector
			}
		}

		return new Scene(polygons, lightPos);

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
		Vector3D lightPos = scene.getLight();

		Transform scale = Transform.newScale(2, 2, 2);

		for(Polygon p : polygons){
			for(int i = 0; i < p.getVertices().length; i++){
				Vector3D vertex = p.getVertices()[i];
				p.getVertices()[i] = scale.multiply(vertex);		//Apply transformation to vector
			}
		}
		return new Scene(polygons, lightPos);
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		// TODO fill this in.

		Vector3D[] vertices = poly.getVertices();

		int minY = getMinY(vertices);
		int maxY = getMaxY(vertices);

		EdgeList edge = new EdgeList(minY, maxY);

		for(Vector3D v : vertices){

			if( v != Va){

				Vector3D Vb = v;		//Get other vertex

				float mx =	(float) ( (Math.pow(Vb.x,2) - Math.pow(Va.x,2)) / (Math.pow(Vb.y,2) - Math.pow(Va.y,2)) );
				float mz =  (float) ( (Math.pow(Vb.z,2) - Math.pow(Va.z,2)) / (Math.pow(Vb.y,2) - Math.pow(Va.y,2)) );

				int x = (int) Va.x; int z = (int) Va.z;			//Set x and z
				int i = (int) Va.y; int maxI = (int)Vb.y;		//Set indices

				while( i >= maxI){

					//NOTE: Determine if x is LEFT or RIGHT
//					if(isLeft(x, Vb))
						edge.addRow(i, x, 0, z, 0);
//					else if(isRight(x))
						edge.addRow(i, 0, x, 0, z);		//Update edgeLists

					x += mx;
					z += mz;			//Update values using gradient/slope

					i++;
				}

				//NOTE: Determine if x is LEFT or RIGHT
//				if(isLeft(x))
					edge.addRow(i, (int)Vb.x, 0, (int)Vb.z, 0);
//				else if(isRight(x))
					edge.addRow(i, 0, (int)Vb.x, 0, (int)Vb.z);		//Update edgeLists
			}
		}

		return edge;
	}

//	public boolean isRight(int x, Vector3D Vb){
//
//
//	}

	/**Get minimum y-value from set of vertices*/
	public static int getMinY(Vector3D[] vertices){

		int minY = (int)Double.POSITIVE_INFINITY;

		for(Vector3D v : vertices){
			if(v.y < minY){
				minY = (int)v.y;
				Va = v;
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
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		// TODO fill this in.


	}
}

// code for comp261 assignments
