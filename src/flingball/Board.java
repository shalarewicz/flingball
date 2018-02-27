/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;


import physics.Vect;

public class Board extends JPanel{
	/*
	 * TODO: AF(height, width, gadgets, triggers) ::= a flingball board of size width*L x height*L containing gadgets and associated triggers and balls
	 * Rep Invariant = 
	 * 		No two gadgets have same anchor
	 * 		All gadgets are entirely on the board
	 * 		All balls on board
	 * 
	 * TODO: Safety from rep exposure
	 */
	
	//private int height, width;
	
	//TODO What is this?
	private static final long serialVersionUID = 1L;
	
	private final int HEIGHT = 20;
	private final int WIDTH = 20;
	private final Wall TOP = new Wall ("TOP", 0, 0, WIDTH, 0);
	private final Wall BOTTOM = new Wall ("BOTTOM", 0, -HEIGHT, WIDTH, -HEIGHT);
	private final Wall LEFT = new Wall ("LEFT", 0, 0, 0, -HEIGHT);
	private final Wall RIGHT = new Wall ("RIGHT", WIDTH, 0, WIDTH, -HEIGHT);
	
	private final List<Gadget> WALLS = new ArrayList<Gadget>(Arrays.asList(TOP, BOTTOM, LEFT, RIGHT));
	private List<Gadget> gadgets = new ArrayList<Gadget>();
	private List<Ball> balls = new ArrayList<Ball>();
	private Map<Gadget, Gadget> triggers;
	
	private final int L = 30;
	
	public Board() {
		this.gadgets = new ArrayList<Gadget>();
		this.triggers = new HashMap<Gadget,Gadget>();
		this.balls = new ArrayList<Ball>();
		checkRep();
	}
	
	// This can used when creating custom sized boards
//	public Board(int height, int width) {
//		this.height = height;
//		this.width = width;
//		
//		// Flingball board as origin in top left. Moving origin to bottom left to make physics calculations easier
//		// Otherwise velocity <x, y> --> <x, -y> or <r, theta> --> <r, 320 - theta>
//		this.top = new Wall(0, 0, width, 0);
//		this.bottom = new Wall(0, -height, width, -height);
//		this.left = new Wall(0, 0, 0, -height);
//		this.right = new Wall(width, 0, width, -height);
//		
//		checkRep();
//		
//	}

	private Board(List<Gadget> newGadgets, Map<Gadget, Gadget> newTriggers, List<Ball> balls) {
		this.gadgets = newGadgets;
		this.triggers = newTriggers;
		this.balls = balls;
		checkRep();
	
	}

	private void checkRep() {
		
		for (int i = 0; i < gadgets.size() - 2; i++) {
			Vect position = gadgets.get(i).position();
			assert position.x() <= WIDTH - 1;
			assert position.x() >= 0;
			assert -position.y() <= HEIGHT - 1;
			assert -position.y() >= 0;
			for (int j = i + 1; j < gadgets.size() - 1; j++) {
				assert position != gadgets.get(j).position();
			}
			
		}
		
//		for (Ball ball : balls) {
//			//TODO: Fix this
//			assert ball.getPosition().x() >= 0;
//			assert ball.getPosition().x() <= WIDTH;
//			assert -ball.getPosition().y() <= 0;
//			assert -ball.getPosition().y() >= -HEIGHT;
//			
//		}
	}
	
	
	/**
	 * 
	 * @return height in L units of the board
	 */
	public int getHeight() {
		return this.HEIGHT;
	}
	
	/**
	 * 
	 * @return width in L units of the board
	 */
	public int getWidth() {
		return this.WIDTH;
	}
	
	/**
	 * 
	 * @param name name of the gadget to be found
	 * @return Gadget with name name
	 * @throws RuntimeException if the Gadget is nor found. 
	 */
	private Gadget getGadget(String name) {
		for (Gadget g : gadgets) {
			if (name.equals(g.name())) {
				return g;
			}
		}
		throw new RuntimeException("GadgetNotFound");
	}
	
	/**
	 * 
	 * @param gadget gadget to be added to the board
	 * @return a board with the added gadget. If the Gadget already exists on the board it will overwrite the existing gadet
	 */
	public Board addGadget(Gadget gadget) {
		
		final String triggerName = gadget.getTrigger();
		Map<Gadget, Gadget> newTriggers = new HashMap<Gadget, Gadget>(triggers);
		
		if (!triggerName.equals(Gadget.NO_TRIGGER)) {
			try {
				final Gadget trigger = getGadget(triggerName);
				newTriggers.put(gadget, trigger);
			}
			catch (Exception e) {
				throw new RuntimeException("Trigger not on board");
			}
		}
		
		List<Gadget> newGadgets = new ArrayList<Gadget>(this.gadgets);
		newGadgets.add(gadget);
		
		return new Board(newGadgets, newTriggers, this.balls);
	}
	
	/**
	 * 
	 * @return a list of gadgets on the board
	 */
	public List<Gadget> getGadgets(){
		//This might not be necessary
		List<Gadget> result = new ArrayList<Gadget>(gadgets);
		return result;
	}
	
	public List<Ball> getBalls(){
		List<Ball> result = new ArrayList<Ball>(this.balls);
		return result;
	}
	
	public void addBall(Ball ball) {
		balls.add(ball);
		checkRep();
	}
	
	private void removeBall(Ball ball) {
		balls.remove(ball);
		
	}
	
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, this.WIDTH * L, this.HEIGHT * L);
		
		final ImageObserver NO_OBSERVER_NEEDED = null;
		
		graphics.setColor(Color.BLUE);
		for (Ball ball : balls) {
			final Vect anchor = ball.getAnchor().times(L);
			
			graphics.drawImage(ball.generate(L), (int) anchor.x(), (int) anchor.y(), NO_OBSERVER_NEEDED);
					
		}
		
		for (Gadget gadget : gadgets) {
			final int xAnchor = (int) gadget.position().x()*L;
			final int yAnchor = (int) gadget.position().y()*L;
			
			graphics.drawImage(gadget.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
			
		}
	}
	
	public void generate() {
		
		BufferedImage output = new BufferedImage(this.WIDTH * L, this.HEIGHT * L, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics graphics = (Graphics2D) output.getGraphics();
		
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, WIDTH * L, HEIGHT * L);
		
		final ImageObserver NO_OBSERVER_NEEDED = null;
		
		graphics.setColor(Color.BLUE);
		for (Ball ball : balls) {
			final Vect anchor = ball.getAnchor().times(L);
			
			
			graphics.drawImage(ball.generate(L), (int) anchor.x(), (int) anchor.y(), NO_OBSERVER_NEEDED);
					
		}
		
		for (Gadget gadget : gadgets) {
			final int xAnchor = (int) gadget.position().x()*L;
			final int yAnchor = (int) gadget.position().y()*L;
			
			graphics.drawImage(gadget.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
			
		}
		
	//	return graphics;
	}
	
	
	
//	public void paint(Graphics g) {
//		Timer timer = new Timer();
//		
//		BufferedImage output = new BufferedImage(this.WIDTH * L, this.HEIGHT * L, BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics graphics = (Graphics2D) output.getGraphics();
//		
//		graphics.setColor(Color.BLACK);
//		graphics.fillRect(0, 0, WIDTH * L, HEIGHT * L);
//		
//		final ImageObserver NO_OBSERVER_NEEDED = null;
//		
//		final long FRAME_RATE = 100;
//		TimerTask play = new TimerTask() {
//			public void run() {
//				graphics.setColor(Color.BLUE);
//				for (Ball ball : balls) {
//					final int xAnchor = (int) ball.getPosition().x() * L;
//					final int yAnchor = (int) ball.getPosition().y() * L;
//					
//					
//					graphics.drawImage(ball.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
//					ball.move(0.01);
//					System.out.println(ball);
//							
//				}
//				
//				for (Gadget gadget : gadgets) {
//					final int xAnchor = (int) gadget.position().x()*L;
//					final int yAnchor = (int) gadget.position().y()*L;
//					
//					graphics.drawImage(gadget.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
//					
//				}
//        		
//        	}
//        };
//		
//        JFrame frame = new JFrame("this is a board with a ball");
//        frame.add(new JLabel(new ImageIcon(output)));
//        frame.pack();
//        frame.setVisible(true);
//        timer.schedule(play, 0, FRAME_RATE);
//	}
	
	
//	public void draw() {
//		JFrame frame = new JFrame("this is a board with a ball");
//		Ball toPlay = new Ball(new Vect(10,10), new Vect(0,1), 0.5);
//	    Board toDraw = new Board();
//	    toDraw.addBall(toPlay);
//	     
//	    frame.add(new JLabel(new ImageIcon(toDraw.generate())));
// 		frame.pack();
// 		frame.setVisible(true);
//	     
//	    Timer timer = new Timer();
//	        
//	    final long FRAME_RATE = 1000;
//	}
	
	public void play(double time) {
		final Gadget NO_COLLISION = new Wall("NO_COLLISION", 0, 0, 0, 0);
		for (int i = 0; i < this.balls.size(); i++) {
			Ball ball = this.balls.get(i);
			double collisionTime = Double.POSITIVE_INFINITY;
			Gadget nextGadget = NO_COLLISION;
			
			for (Gadget gadget : this.gadgets) {
				//TODO:System.out.println(gadget);
				if (gadget.collisionTime(ball) < collisionTime) {
					collisionTime = gadget.collisionTime(ball);
					//TODO:System.out.println(gadget +" "+ collisionTime);
					nextGadget = gadget;
				}
			}
			if (nextGadget == NO_COLLISION) {
				for (Gadget wall : this.WALLS) {
					if (wall.collisionTime(ball) < collisionTime) {
						collisionTime = wall.collisionTime(ball);
						nextGadget = wall;
					}
				}
			}
			
			if (collisionTime <= time && nextGadget != NO_COLLISION) {
				//TODO: If two objects are stuck to each other the ball will not move. 
				//TODO: A ball will bounce off a square bumper or line if it goes right next to it parallel to a side
				ball.move(collisionTime);
				Ball newBall = nextGadget.reflectBall(ball);
				this.removeBall(ball);
				this.addBall(newBall);
				newBall.move(time - collisionTime);
				continue;
			} else {		
				ball.move(time);
			}
		}
	}


	
//	public class TestPane extends JPanel {
//		private final int FRAME_RATE = 1000;
//		private Board board;
//		private final TimerTask play = new TimerTask() {
//        	public void run() {
//        		board.play(FRAME_RATE);
//        		repaint();
//        		
//        	}
//        };
//		public TestPane(Board board) {
//			this.board = board;
//			Timer timer = new Timer();
//			timer.schedule(play, 0, (long) FRAME_RATE);
//		}
//		
//	}
	
//	public static void main(String[] args) {
//		//TODO How to refresh an image?
//        JFrame frame = new JFrame("this is a board with a ball");
//        Board toDraw = new Board();
//        Ball toPlay = new Ball(new Vect(10,10), new Vect(0,1), 0.5);
//        toDraw.addBall(toPlay);
//        
//        frame.add(toDraw);
// 		frame.pack();
// 		frame.setVisible(true);
// 		
// 		Timer timer = new Timer();
// 		final long FRAME_RATE = 1000;
//		TimerTask play = new TimerTask() {
//			public void run() {
//				toDraw.play(1);
//				toDraw.repaint();
//			}
//		};
// 		timer.schedule(play, 0, (long) FRAME_RATE);
//        
//    }
}


