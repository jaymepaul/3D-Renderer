package renderer;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderListener implements ChangeListener{

	private GUI gui;
	
	public SliderListener(GUI gui){
		this.gui = gui;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		gui.render();
		gui.redraw();
	}

}
