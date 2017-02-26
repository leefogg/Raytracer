package Window;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class Window {
	
	public static void main(String arg[]) {
		new Window("RayTracer", 512, 540, new Main());
	}
	
	public Window(String Title, int Width, int Height, Component contents) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(contents);
		contents.setFocusable(true);
		frame.setSize(Width,Height);
		frame.setResizable(false);
		frame.setTitle(Title);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
