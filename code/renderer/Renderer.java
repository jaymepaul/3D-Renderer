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
	private static double imageWidth = 0;
	private static double imageHeight = 0;

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

			Vector3D lightVector = new Vector3D(xLight, yLight, zLight);
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

			this.scene = new Scene(polygons, lightVector);
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("file reading failed.");
		}
		
		//=============TRANSLATE + SCALE=======================

		scene.computeBoundingBox();
		Pipeline.scaleScene(scene);
		scene.computeBoundingBox();
		Pipeline.translateScene(scene);
		
		//=============TRANSLATE + SCALE=======================
		

	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		// TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
		float xRot = 0, yRot = 0;
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A'){
			xRot -= 90;
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, xRot, yRot);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D'){
			xRot += 90;
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, xRot, yRot);
		}
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN
				|| Character.toUpperCase(ev.getKeyChar()) == 'S'){
			yRot -= 90;
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, xRot, yRot);
		}
		else if(ev.getKeyCode() == KeyEvent.VK_UP
				|| Character.toUpperCase(ev.getKeyChar()) == 'W'){
			yRot += 90;
			scene.computeBoundingBox();
			Pipeline.rotateScene(scene, xRot, yRot);
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
		
		//Initialise
		for(int i = 0; i < zBuffer.length; i++){
			for(int j = 0; j < zBuffer[i].length; j++){
				zBuffer[i][j] = new Color(128,128,128);
				zDepth[i][j] = INF;
			}
		}
		
		Vector3D lightVect = scene.getLight();
		Color lightColor = new Color(255, 255, 255); 	//WHITE
		Color ambientLight = new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]);
		
		for(Scene.Polygon p : scene.getPolygons()){
			if(!Pipeline.isHidden(p)){
				Color shading  = Pipeline.getShading(p, lightVect, lightColor, ambientLight);
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
