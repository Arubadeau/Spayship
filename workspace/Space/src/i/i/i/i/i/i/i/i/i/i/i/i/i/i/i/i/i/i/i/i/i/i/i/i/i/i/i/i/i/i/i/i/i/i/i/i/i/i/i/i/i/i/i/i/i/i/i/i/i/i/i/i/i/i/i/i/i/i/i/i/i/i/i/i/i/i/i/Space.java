package i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i.i;

import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

public class Space extends JFrame 
{

	final int WIDTH;
	final int HEIGHT;
	final int MAX_FPS;

	public Space(int width, int height, int fps){
		super();
		
		this.WIDTH = width;
		this.HEIGHT = height;
		this.MAX_FPS = fps;
	}
	void init() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setBounds(0, 0, WIDTH, HEIGHT);
		setIgnoreRepaint(true);
		setVisible(true);
	
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}
	public void run() {
		init();
	}
	
	public static void space(String.args[]){
		Space my_space = new Space(800, 600, 60);
		my_space.run();
	}

