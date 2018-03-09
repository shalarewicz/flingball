/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import flingball.TriangleBumper.Orientation;
import physics.Angle;
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
	 * coverage is exposed in gadget.setCoverage();
	 */
	
	
	//SerialVersionUID required for a serializable class (JPanel)
	private static final long serialVersionUID = 1L;

	public static final double GRAVITY = 25.0;

	public static final double FRICTION_1 = 0.025;

	public static final double FRICTION_2 = 0.025;
	
	public static int BALL_LIMIT = 20;
	
	
	private final String NAME;
	private final int HEIGHT = 20;
	private final int WIDTH = 20;
	private final Wall TOP = new Wall ("TOP", 0, 0, WIDTH, 0);
	private final Wall BOTTOM = new Wall ("BOTTOM", 0, -HEIGHT, WIDTH, -HEIGHT);
	private final Wall LEFT = new Wall ("LEFT", 0, 0, 0, -HEIGHT);
	private final Wall RIGHT = new Wall ("RIGHT", WIDTH, 0, WIDTH, -HEIGHT);
	private double gravity = GRAVITY;
	private double friction1 = FRICTION_1;
	private double friction2 = FRICTION_2;
	
	private final List<Gadget> WALLS = new ArrayList<Gadget>(Arrays.asList(TOP, BOTTOM, LEFT, RIGHT));
	private List<Gadget> gadgets = new ArrayList<Gadget>();
	private List<Ball> balls = new ArrayList<Ball>();
	private Map<Gadget, List<Gadget>> triggers = new HashMap<Gadget, List<Gadget>>();
	private Map<Gadget, List<Action>> boardTriggers = new HashMap<Gadget, List<Action>>();
	private Deque<Ball> actionQueue = new LinkedList<Ball>();
	private int area = 0;
	
	private int[][] gadgetCoverage = new int[WIDTH][HEIGHT];
	
	public int GADGET_LIMIT = WIDTH * HEIGHT / 7;
	
	private int L = 40;

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
		
		for (Gadget gadget : gadgets) {
			Vect position = gadget.position();
			int gadgetWidth = gadget.width();
			int gadgetHeight = gadget.height();
			assert position.x() <= WIDTH - gadgetWidth;
			assert position.x() >= 0;
			assert position.y() <= HEIGHT - gadgetHeight;
			assert position.y() >= 0;
			for (Ball ball : balls) {
				if (!ball.isTrapped()) {
					// TODO Is there a way to check this only on board creation. Floating point math errors
					// cause move to invalidate the rep invariant if this check is performed. 
					//assert !gadget.ballOverlap(ball);
				}
			}
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
	
	/**
	 * A list of actions which can be performed on the board.
	 */
	public enum Action {
		FIRE_ALL, ADD_BALL, ADD_SQUARE, ADD_CIRCLE, ADD_TRIANGLE, ADD_ABSORBER, REVERSE_BALLS
		//TODO REMOVE_BALL, REMOVE_BUMPER, REMOVE_ABSORBER
	}
	
	/**
	 * 
	 * @return the name of the board. 
	 */
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
	 * Adds gadget to the board. 
	 * @param gadget gadget to be added to the board
	 */
	public void addGadget(Gadget gadget) {
		this.gadgets.add(gadget);
		gadget.setCoverage(this.gadgetCoverage);
		this.area += gadget.area();
		checkRep();
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
	
	/**
	 * 
	 * @return a list of balls on the board.
	 */
	public List<Ball> getBalls(){
		List<Ball> result = new ArrayList<Ball>(this.balls);
		return result;
	}
	
	/**
	 * Adds a ball to the board. 
	 * @param ball ball to be added. 
	 */
	public void addBall(Ball ball) {
		balls.add(ball);
		checkRep();
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
	
	/**
	 * Will move all balls and gadgets on the board to their new positions after all moves/collisions that would occur
	 * within time have occurred. Any actions which were triggered during this time will also occur. 
	 * @param time length of time in seconds the board should be "played"
	 */
	public void play(double time) {
		//TODO Add an event queue so that actions that could not be performed are performed at the earliest possible moment
		for (int i = 0; i < this.balls.size(); i++) {
			if (!balls.get(i).isTrapped()) {
				moveOneBall(balls.get(i), time);
			}
		}
		checkRep();
	}

	private void moveOneBall(Ball ball, final double time) {
		final Gadget NO_COLLISION = new Wall("NO_COLLISION", 0, 0, 0, 0);
		double collisionTime = Double.POSITIVE_INFINITY;
		Gadget nextGadget = NO_COLLISION;
		
		// Find the gadget with which the ball will collide next
		for (Gadget gadget : this.gadgets) {
			if (gadget.collisionTime(ball) < collisionTime) {
				collisionTime = gadget.collisionTime(ball);
				nextGadget = gadget;
			}
		}
		
		// If the ball will not collide with the gadgets check the outer walls of the board. 
		if (nextGadget == NO_COLLISION) {
			for (Gadget wall : this.WALLS) {
				if (wall.collisionTime(ball) < collisionTime) {
					collisionTime = wall.collisionTime(ball);
					nextGadget = wall;
				}
			}
		}
		
		// If a ball will collide during time perform the collision. 
		if (collisionTime <= time && nextGadget != NO_COLLISION) {
			// Move ball to collision point
			ball.move(collisionTime, this.gravity, this.friction1, this.friction2);
			//TODO - technically this won't move the ball to the collision point because of friction/gravity
			checkRep();
			nextGadget.reflectBall(ball);
			
			// Perform any actions triggered by the collision
			if (triggers.containsKey(nextGadget)) {
				for (Gadget gadget : triggers.get(nextGadget)) {
					gadget.takeAction();
				}
			}
			if (boardTriggers.containsKey(nextGadget)) {
				for (Action action : boardTriggers.get(nextGadget)) {
					this.takeAction(action, ball);
				}
			}
			
			// Move ball during the rest of time after collision has occurred. 
			if (ball.getVelocity().length() > 0.0 && collisionTime > 0) {
				// TODO What about simultaneous collisions
				moveOneBall(ball, time - collisionTime);
			}
		} else {
			ball.move(time, this.gravity, this.friction1, this.friction2);
		}
	}
	
	/**
	 * Checks to see if any ball on the board and a gadget overlap
	 * @param gadget we are checking
	 * @return true if the gadget and ball over lap. 
	 */
	private boolean checkBallOverlaps(Gadget gadget) {
		for (Ball ball : this.balls) {
			if (gadget.ballOverlap(ball)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Takes the specified action on the board. 
	 * @param gadget gadget whose action is taken
	 * @param action action to be taken
	 * @param currentBall ball which triggered the action
	 */
	private void takeAction(Board.Action action, Ball currentBall) {
		switch (action) {
		case ADD_BALL:{
			if (balls.size() <= BALL_LIMIT) {
				this.addRandomBall(currentBall);
			}
			break;
			
		}
		case ADD_SQUARE:{
			if (this.gadgets.size() <= GADGET_LIMIT) {
				Gadget newGadget;
				try {
					newGadget = addSquare(findEmptySpot(0, 0, 1,1));
					if (checkBallOverlaps(newGadget)) {
						this.addGadget(newGadget);
					}
					else {
						takeAction(action, currentBall);
					}
				} catch (NoOpenSpotsException e) {
					break;
				}
			}
			checkRep();
			break;
		}
		case ADD_CIRCLE:{
			if (this.gadgets.size() <= GADGET_LIMIT) {
				Gadget newGadget;
				try {
					newGadget = addCircle(findEmptySpot(0, 0, 1, 1));
				for (Ball ball : this.balls) {
					if (newGadget.ballOverlap(ball)) {
						takeAction(action, currentBall);
					}
				}
				this.addGadget(newGadget);
				} catch (NoOpenSpotsException e) {
					break;
				}
			}
			checkRep();
			break;
		}
		case ADD_TRIANGLE:{
			if (this.gadgets.size() <= GADGET_LIMIT) {
				Gadget newGadget;
				try {
					newGadget = addTriangle(findEmptySpot(0, 0, 1, 1));
				for (Ball ball : this.balls) {
					if (newGadget.ballOverlap(ball)) {
						takeAction(action, currentBall);
					}
				}
				this.addGadget(newGadget);
				} catch (NoOpenSpotsException e) {
					break;
				}
			}
			checkRep();
			break;
		}
		case ADD_ABSORBER:{
			//TODO Absorber getting added with circle in firing spot
			if (this.gadgets.size() <= GADGET_LIMIT) {
				// Create an absorber between sizes 1 x 1 and 5 x 2
				int randomWidth = (int) (Math.random() * 5 + 1);
				int randomHeight = (int) (Math.random() * 2 + 1);
				// Note that absorbers cannot be in the top row as they must be able to fire a ball;
				Gadget newGadget;
				try {
					newGadget = addAbsorber(findEmptySpot(0, 1, randomWidth, randomHeight), randomWidth, randomHeight);
					if (checkBallOverlaps(newGadget)) {
						this.addGadget(newGadget);
						// Make the absorber self firing
						this.triggers.put(newGadget, new ArrayList<Gadget>(Arrays.asList(newGadget)));
					}
					else {
						takeAction(action, currentBall);
					}
				} catch (NoOpenSpotsException e) {
					break;
				}
			}
			checkRep();
			break;
		}
		case REVERSE_BALLS:{
			this.reverseAll();
			currentBall.setVelocity(currentBall.getVelocity().rotateBy(Angle.DEG_180));
			checkRep();
			break;
		}
		case FIRE_ALL: {
			this.fireAll();
			checkRep();
			break;
		}
		default:
			throw new RuntimeException("Should never get here");
		}
	}

	private void addRandomBall(Ball ball) {
		String newName = ball.name();
		List<String> names = new ArrayList<String>();
		for (Ball b: balls) {
			names.add(b.name());
		}
		for (int i = 1; i < 20; i++) {
			newName = newName + i;
			if (names.add(newName)) {
				break;
			}
		}
		Ball newBall = new Ball(newName, ball.getBoardCenter(), new Vect(Math.random()*100, Math.random()*100), ball.getRadius());
		this.addBall(newBall);
		
		checkRep();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("FLINGBALL BOARD:{ Specs:" + this.NAME + ", Gravity: " + this.gravity);
		result.append(", friction1: " + this.friction1 + ", friction2: " + this.friction2);
		result.append(", Balls:" + this.balls);
		result.append(", Gadgets:" + this.gadgets +"}");
		return result.toString();
	}
	
	/**
	 * Adds trigger which triggers actions' action. 
	 * 
	 * @param trigger - name of the Gadget which triggers the action
	 * @param action - name of the Gadget on whose action will be taken
	 */
	public void addAction(String trigger, String action) {
		Gadget gTrigger = getGadget(trigger);
		if (!triggers.containsKey(gTrigger)) {
			triggers.put(gTrigger, new ArrayList<Gadget>());
		}
		triggers.get(gTrigger).add(getGadget(action));
	}
	
	/**
	 * Adds a trigger which triggers action to the board. 
	 * @param trigger name of the trigger gadget
	 * @param action the board action which will be taken 
	 */
	public void addAction(String trigger, Action action) {
		Gadget gTrigger = getGadget(trigger);
		if (!boardTriggers.containsKey(gTrigger)) {
			boardTriggers.put(gTrigger, new ArrayList<Action>());
		}
		this.boardTriggers.get(getGadget(trigger)).add(action);
	}
	
	/**
	 * Fires all trapped balls on the board.
	 */
	private void fireAll() {
		for (Gadget gadget : this.gadgets) {
			gadget.fireAll();
		}
	}
	
	/**
	 * Reverses the direction of all balls on the board. 
	 */
	private void reverseAll() {
		for (Ball ball : balls) {
			ball.setVelocity(ball.getVelocity().rotateBy(Angle.DEG_180));
		}
	}
	
	/**
	 * Generates a triangle bumper in an open spot on the board.
	 * @param position position where triangle is added
	 * @return a triangle bumper with position position and random orientation
	 */
	private Gadget addTriangle(Vect position) {
		Orientation o;
		switch ((int) (Math.random() * 100 / 25)) {
		case 0: {
			o = Orientation.ZERO;
			break;
		}
		case 1: {
			o = Orientation.NINETY;
			break;
		}
		case 2: {
			o = Orientation.ONEEIGHTY;
			break;
		}
		case 3: {
			o = Orientation.TWOSEVENTY;
			break;
		}
		default:
			o = Orientation.ZERO;
		}
		return new TriangleBumper("Triangle"+ position, (int) position.x(), (int) position.y(), o);
	}
	
	
	/**
	 * Generates a square bumper in an open spot on the board.
	 * @param position position where triangle is added
	 * @return a square bumper with position position 
	 */
	private Gadget addSquare(Vect position) {
		return new SquareBumper("Square"+ position, (int) position.x(), (int) position.y());
	}
	
	/**
	 * Generates a circle bumper in an open spot on the board.
	 * @param position position where triangle is added
	 * @return a circle bumper with position position 
	 */
	private Gadget addCircle(Vect position) {
		return new CircleBumper("Circle"+ position, (int) position.x(), (int) position.y());
	}
	
	/**
	 * Generates an absorber in an open spot on the board.
	 * @param position position where triangle is added
	 * @return an absorber with position position 
	 */
	private Gadget addAbsorber(Vect position, int width, int height) {
		return new Absorber("Absorber"+ position, (int) position.x(), (int) position.y(), width, height);
	}
	
	/**
	 * Finds an open spot on the board. 
	 * 
	 * @param xOrigin the minimum x on the board the new spot must have
	 * @param yOrigin the minimum y on the board the new spot must have
	 * @param width the width of the new spot
	 * @param height the height of the new spot
	 * @return a vector with xOrigin <= x < this.width - width and yOrigin <= y <= this.height - height where
	 * y + height spots by x + width spots are empty
	 * @throws RuntimeException if no spots are available
	 */
	private Vect findEmptySpot(int xOrigin, int yOrigin, int width, int height) throws NoOpenSpotsException{
		if (this.area >= this.WIDTH * this.HEIGHT - this.balls.size() * 2) {
			throw new NoOpenSpotsException();
		}
		whileloop:
		while (true) {
			int newY = (int) (Math.random() * (HEIGHT - yOrigin) + yOrigin);
			int newX = (int) (Math.random() * (WIDTH - xOrigin) + xOrigin);
			if ((newX + width > this.WIDTH) || (newY + height > this.HEIGHT)) {
				continue whileloop;
			}
			for (int j = newY; j < newY + height; j++) {
				for (int i = newX; i < newX + width; i++) {
					if (gadgetCoverage[j][i] == 1) {
						continue whileloop;
					}
				}
			}
			return new Vect (newX, newY);
		}
	}
	
	private class NoOpenSpotsException extends Exception {
		private static final long serialVersionUID = 1L;


	}

	/**
	 * A flingball board is 20 * L pixels high and wide. 
	 * @return the integer unit L for the board
	 */
	public int getL() {
		return this.L;
	}
	
	/**
	 * Sets the flingball board to be 20 * l pixels wide and high
	 * @param l the new value of L for the baord. 
	 */
	public void setL(int l) {
		this.L = l;
	}
	
}


