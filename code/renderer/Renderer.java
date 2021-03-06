package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Renderer extends GUI {

	private Scene scene;
	private float INF = (float) Double.POSITIVE_INFINITY;

	@Override
	protected void onLoad(File file) {
		// TODO fill this in.

		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = null;
			line = br.readLine();
			String[] lightTokens = line.split("\\s+");

			float xLight = Float.parseFloat(lightTokens[0]);
			float yLight = Float.parseFloat(lightTokens[1]);
			float zLight = Float.parseFloat(lightTokens[2]);
			
			//============TEST=============// NOTE: Add LIGHT VECTOR TO FILE
			Vector3D lightVector = new Vector3D(xLight, yLight, zLight);
			Vector3D lightVector2 = new Vector3D(100.79056706f, -200.43019001f, -1.5113221f);
			Vector3D lightVector3 = new Vector3D(-200.79056706f, 100.43019001f, -0.9113221f);
			Vector3D [] lightSources = new Vector3D[3];
			lightSources[0] = lightVector;	lightSources[1] = lightVector2;	lightSources[2] = lightVector3;
			
			List<Scene.Polygon> polygons = new ArrayList<Scene.Polygon>();

			while ((line = br.readLine()) != null) {

				String[] tokens = line.split("\\s+");

				float xT1 = Float.parseFloat(tokens[0]); float yT1 = Float.parseFloat(tokens[1]); float zT1 = Float.parseFloat(tokens[2]);
				float xT2 = Float.parseFloat(tokens[3]); float yT2 = Float.parseFloat(tokens[4]); float zT2 = Float.parseFloat(tokens[5]);
				float xT3 = Float.parseFloat(tokens[6]); float yT3 = Float.parseFloat(tokens[7]); float zT3 = Float.parseFloat(tokens[8]);
				int r = Integer.parseInt(tokens[9]); int g = Integer.parseInt(tokens[10]); int b = Integer.parseInt(tokens[11]);

				Vector3D T1 = new Vector3D(xT1, yT1, zT1);
				Vector3D T2 = new Vector3D(xT2, yT2, zT2);
				Vector3D T3 = new Vector3D(xT3, yT3, zT3);
				Color col = new Color(r,g,b);

				Scene.Polygon polygon = new Scene.Polygon(T1, T2, T3, col);
				polygons.add(polygon);
			}

			this.scene = new Scene(polygons, lightSources);
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("file reading failed.");
		}
		
		//=============PRE TRANSLATE + SCALE=======================

		scene.computeBoundingBox();
		Pipeline.scaleScene(scene);
		scene.computeBoundingBox();
		Pipeline.rotateScene(scene, 0, 100);
		scene.computeBoundingBox();
		Pipeline.translateScene(scene);
		
		//=============PRE TRANSLATE + SCALE=======================
		
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A'){
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, 0, 0.3f);
			scene.computeBoundingBox();
			Pipeline.translateScene(scene);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D'){
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, 0, -0.3f);
			scene.computeBoundingBox();
			Pipeline.translateScene(scene);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN
				|| Character.toUpperCase(ev.getKeyChar()) == 'S'){
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, 0.3f, 0);
			scene.computeBoundingBox();
			Pipeline.translateScene(scene);
		}
		else if(ev.getKeyCode() == KeyEvent.VK_UP
				|| Character.toUpperCase(ev.getKeyChar()) == 'W'){
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, -0.3f, 0);
			scene.computeBoundingBox();
			Pipeline.translateScene(scene);
		}
	}

	@Override
	protected BufferedImage render() {
		// TODO fill this in.

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		if(scene == null)
			return null;
		
		Color[][] zBuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zDepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		
		//Initialize Z-Buffer
		for(int i = 0; i < zBuffer.length; i++){
			for(int j = 0; j < zBuffer[i].length; j++){
				zBuffer[i][j] = Color.BLACK;
				zDepth[i][j] = INF;
			}
		}
		
		//Illumination Variables
		Color lightColor = new Color(getLightSource1()[0], getLightSource1()[1] , getLightSource1()[2]); 	//WHITE
		Color lightColor2 = new Color(getLightSource2()[0], getLightSource2()[1], getLightSource2()[2]);
		Color lightColor3 = new Color(getLightSource3()[0], getLightSource3()[1], getLightSource3()[2]);
		Color ambientLight = new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]);
		Vector3D[] lightSources = scene.getLightSources();
		Color [] lightColors = new Color[3];
		lightColors[0] = lightColor;	lightColors[1] = lightColor2;	lightColors[2] = lightColor3;
		
		//Compute Surface Normals
		Pipeline.computeSurfaceNormals(scene.getPolygons());
		//Compute Vertex Normals
		Pipeline.computeVertexNormals(scene.getPolygons());
		//Compute Light Intensity @ each Vertex
		Pipeline.computeIntensityAtVertex(scene.getPolygons(), lightSources, lightColors, ambientLight);
		
		for(Scene.Polygon p : scene.getPolygons()){
			if(!Pipeline.isHidden(p)){
				Color shading  = Pipeline.getShading(p, lightSources, lightColors, ambientLight);
				EdgeList edgeList = Pipeline.computeEdgeList(p);
				Pipeline.computeZBuffer(zBuffer, zDepth, edgeList, shading);
			}
		}

		return convertBitmapToImage(zBuffer);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
