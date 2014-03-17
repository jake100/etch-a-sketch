import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Etch extends Canvas implements Runnable
{
	static Thread t;
	private final int brighnessAdjustTime = 20, speedAdjustTime = 10, sizeAdjustTime = 10;
	private final int width = 600, height = 600, second = 1000000, minBrushSize = 5, maxBrushSize = 100;
	private int	brightnessAdjustDelay = 0, speedAdjustDelay = 0, sizeAdjustDelay, brushSize = minBrushSize;
	private final double speedStep = .05, minSpeed = 1, maxSpeed = speedStep * 500;
	private double x = width / 2, y = height / 2, speed = 1;
	private InputHandler input;
	private Color brushColor = Color.black;
	int aiDir = 0;
	private boolean reset = false;

	public static void main(String args[])
	{
		JFrame frame = new JFrame();
		Etch game = new Etch();
		frame.setResizable(false);
		frame.setTitle("Etch-a-sketch");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		t.start();
	}

	public Etch()
	{
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		t = new Thread(this, "Etch");
		input = new InputHandler();
		addKeyListener(input);
	    addMouseListener( new MouseAdapter() {
	        public void mousePressed(MouseEvent e)
	        {
	        	x = e.getX();
	        	y = e.getY();
	        }
	      });
	}

	public void run()
	{
		long lastLoopTime = System.nanoTime();
		while (true)
		{
			long now = System.nanoTime();
			lastLoopTime = now;
			update();
			render();
			try
			{
				Thread.sleep((lastLoopTime - System.nanoTime()) / second + 10);
			} catch (Exception ex)
			{
			}
		}
	}

	public void update()
	{
		reset = false;
		if (brightnessAdjustDelay > 0)
			brightnessAdjustDelay--;
		if(speedAdjustDelay > 0)
			speedAdjustDelay--;
		if(sizeAdjustDelay > 0)
			sizeAdjustDelay--;
		if (input.keys[KeyEvent.VK_W])
			y = (int)(y - speed + .5);
		if (input.keys[KeyEvent.VK_S])
			y = (int)(y + speed + .5);
		if (input.keys[KeyEvent.VK_A])
			x = (int)(x - speed + .5);
		if (input.keys[KeyEvent.VK_D])
			x = (int)(x + speed + .5);
		if (input.keys[KeyEvent.VK_1])
			brushColor = Color.black;
		if (input.keys[KeyEvent.VK_2])
			brushColor = Color.white;
		if (input.keys[KeyEvent.VK_3])
			brushColor = Color.gray;
		if (input.keys[KeyEvent.VK_4])
			brushColor = Color.red;
		if (input.keys[KeyEvent.VK_5])
			brushColor = Color.green;
		if (input.keys[KeyEvent.VK_6])
			brushColor = Color.blue;
		if (input.keys[KeyEvent.VK_7])
			brushColor = Color.yellow;
		if (input.keys[KeyEvent.VK_8])
			brushColor = Color.orange;
		if (input.keys[KeyEvent.VK_9])
			brushColor = Color.magenta;
		if (input.keys[KeyEvent.VK_0])
			brushColor = Color.cyan;

		if (input.keys[KeyEvent.VK_LEFT] && brightnessAdjustDelay <= 0)
		{
			brushColor = brushColor.darker();
			brightnessAdjustDelay = brighnessAdjustTime;
		}
		if (input.keys[KeyEvent.VK_RIGHT] && brightnessAdjustDelay <= 0)
		{
			brushColor = brushColor.brighter();
			brightnessAdjustDelay = brighnessAdjustTime;
		}
		if (input.keys[KeyEvent.VK_UP] && sizeAdjustDelay <= 0)
		{
			if(brushSize + 2 <= maxBrushSize)
			{
				brushSize += 2;
				x--;
				y--;
				sizeAdjustDelay = sizeAdjustTime;
			}
			brightnessAdjustDelay = brighnessAdjustTime;
		}
		if (input.keys[KeyEvent.VK_DOWN] && sizeAdjustDelay <= 0)
		{
			if(brushSize - 2 >= minBrushSize)
			{
				brushSize -= 2;
				x++;
				y++;
				sizeAdjustDelay = sizeAdjustTime;
			}
			brightnessAdjustDelay = brighnessAdjustTime;
		}
		if(input.keys[KeyEvent.VK_SPACE] && speedAdjustDelay <= 0)
		{
			if(speed + speedStep <= maxSpeed)
			{
				speed += speedStep;
				speedAdjustDelay = speedAdjustTime;
			}
		}
		if(input.keys[KeyEvent.VK_SHIFT] && speedAdjustDelay <= 0)
		{
			if(speed - speedStep >= minSpeed)
			{
				speed -= speedStep;
				speedAdjustDelay = speedAdjustTime;
			}
		}
		if (input.keys[KeyEvent.VK_ESCAPE])
			reset = true;
	}

	public void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		if (reset)
			g.clearRect(0, 0, width, height);
		g.setColor(brushColor);
		g.fillOval((int)x, (int)y, brushSize, brushSize);
		g.dispose();
		bs.show();
	}

	class InputHandler implements KeyListener
	{
		public boolean[] keys = new boolean[65536];

		public void keyPressed(KeyEvent e)
		{
			int keyCode = e.getKeyCode();
			keys[keyCode] = true;
		}

		public void keyReleased(KeyEvent e)
		{
			int keyCode = e.getKeyCode();
			keys[keyCode] = false;
		}

		public void keyTyped(KeyEvent e)
		{
			int keyCode = e.getKeyCode();
			keys[keyCode] = true;
		}
	}
}
