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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;


import physics.Vect;

public class Board extends JPanel{
	/*
	 * TODO: AF(height, width, gadgets, triggers) ::= a flingball board of size width*L x height*L containing gadgets and associated triggers and balls
	 * Rep Invariant = 
	 * 		No two gadgets have same anchor
	 * 		All gadgets are entirely on the board
	 * 		All balls on board
	 * 		No two gadgets or balls have the same name
	 * 
	 * TODO: Safety from rep exposure
	 */
	
	//private int height, width;
	
	//TODO What is this?
	private static final long serialVersionUID = 1L;

	public static final double GRAVITY = 25.0;

	public static final double FRICTION_1 = 0.025;

	public static final double FRICTION_2 = 0.025;
	
	
	private final String NAME;
	private final int HEIGHT = 20;
	private final int WIDTH = 20;
	private final Wall TOP = new Wall ("TOP", 0, 0, WIDTH, 0);
	private final Wall BOTTOM = new Wall ("BOTTOM", 0, -HEIGHT, WIDTH, -HEIGHT);
	private final Wall LEFT = new Wall ("LEFT", 0, 0, 0, -HEIGHT);
	private final Wall RIGHT = new Wall ("RIGHT", WIDTH, 0, WIDTH, -HEIGHT);
	private double gravity = this.GRAVITY;
	private double friction1 = this.FRICTION_1;
	private double friction2 = this.FRICTION_2;
	
	private final List<Gadget> WALLS = new ArrayList<Gadget>(Arrays.asList(TOP, BOTTOM, LEFT, RIGHT));
	private List<Gadget> gadgets = new ArrayList<Gadget>();
	private List<Ball> balls = new ArrayList<Ball>();
	private Map<Gadget, Gadget> triggers = new HashMap<Gadget, Gadget>();
	
	//TODO Allow user to set this field
	final int L = 40;
	
	public Board() {
		this.NAME = "TEST"; //TODO Update or remove constructor
		this.gadgets = new ArrayList<Gadget>();
		this.triggers = new HashMap<Gadget,Gadget>();
		this.balls = new ArrayList<Ball>();
		checkRep();
	}
	
	public Board(String name) {
		this.NAME = name;
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
		this.NAME = "TEST"; //TODO Update or remove constructor
		this.gadgets = newGadgets;
		this.triggers = newTriggers;
		this.balls = balls;
		checkRep();
	
	}

	public Board(String name, double gravity, double friction1, double friction2) {
		this.NAME = name;
		this.gravity = gravity;
		this.friction1 = friction1;
		this.friction2 = friction2;
		checkRep();
	}

	private void checkRep() {
		
		Set<Vect> anchors = new HashSet<Vect>();
		Set<String> names = new HashSet<String>();
		
		for (Gadget gadget : gadgets) {
			assert names.add(gadget.name());
			assert anchors.add(gadget.position());
		}
		
		for (int i = 0; i < gadgets.size(); i++) {
			Vect position = gadgets.get(i).position();
			assert position.x() <= WIDTH - 1;
			assert position.x() >= 0;
			assert position.y() <= HEIGHT - 1;
			assert position.y() >= 0;
		}
		
		for (Ball ball : balls) {
			final double radius = ball.getRadius();
			final double cx = ball.getBoardCenter().x();
			final double cy = ball.getBoardCenter().y();
			assert cx - radius >= 0;
			assert cx + radius <= WIDTH;
			assert cy - radius >= 0;
			assert cy + radius <= HEIGHT;
			
		}
	}
	
	public String getName() {
		return this.NAME;
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
	 * @return a board with the added gadget. If the Gadget already exists on the board it will overwrite the existing gadget
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
		
	}

	/**
	 * Will move all balls and gadgets on the board to their new positions/stats after all moves/collisions that would occur
	 * within time have occurred.  
	 * @param time length of time in seconds the board should be "played"
	 */
	public void play(double time) {
		for (int i = 0; i < this.balls.size(); i++) {
			moveOneBall(balls.get(i), time);
		}
	}

	private void moveOneBall(Ball ball, final double time) {
		final Gadget NO_COLLISION = new Wall("NO_COLLISION", 0, 0, 0, 0);
		double collisionTime = Double.POSITIVE_INFINITY;
		Gadget nextGadget = NO_COLLISION;
		
		for (Gadget gadget : this.gadgets) {
			if (gadget.collisionTime(ball) < collisionTime) {
				collisionTime = gadget.collisionTime(ball);
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
			//TODO: A ball will bounce off a square bumper or line if it goes right next to it parallel to a side
			//Ball movedBall = ball.move(collisionTime, this.gravity, this.friction1, this.friction2);
			Ball movedBall = ball.move(collisionTime);
			//System.out.println("Moved to collision point: " + ball.getBoardCenter());
			Ball newBall = nextGadget.reflectBall(movedBall);
			//System.out.println("Collided: " + newBall.getBoardCenter());
			this.removeBall(ball);
			this.addBall(newBall);
			moveOneBall(newBall, time - collisionTime);
			//System.out.println("Moved remainder of frame: " + newBall.getBoardCenter());
		} else {	
			//Ball newBall = ball.move(time, this.gravity, this.friction1, this.friction2);
			Ball newBall = ball.move(time);
			this.removeBall(ball);
			this.addBall(newBall);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{FLINGBALL BOARD:[" + this.NAME + ", ");
		result.append("Balls:" + this.balls);
		result.append("Gadgets:" + this.gadgets);
		return result.toString();
	}

}


