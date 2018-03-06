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
import java.util.HashMap;
import java.util.HashSet;
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
	 */
	
	
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
	private double gravity = GRAVITY;
	private double friction1 = FRICTION_1;
	private double friction2 = FRICTION_2;
	
	private final List<Gadget> WALLS = new ArrayList<Gadget>(Arrays.asList(TOP, BOTTOM, LEFT, RIGHT));
	private List<Gadget> gadgets = new ArrayList<Gadget>();
	private List<Ball> balls = new ArrayList<Ball>();
	private Map<Gadget, List<Gadget>> triggers = new HashMap<Gadget, List<Gadget>>();
	private Map<Gadget, List<Action>> boardTriggers = new HashMap<Gadget, List<Action>>();
	
	//TODO Allow user to set this field
	final int L = 40;
	
	private Board(List<Gadget> newGadgets, Map<Gadget, List<Gadget>> newTriggers, Map<Gadget, List<Action>> newBoardTriggers, List<Ball> balls) {
		this.NAME = "TEST"; //TODO Update or remove constructor
		this.gadgets = newGadgets;
		this.triggers = newTriggers;
		this.balls = balls;
		this.boardTriggers = newBoardTriggers;
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
					assert !gadget.ballOverlap(ball);
				}
			}
		}
		
		for (Ball ball : balls) {
			final double radius = ball.getRadius();
			final double cx = ball.getBoardCenter().x();
			final double cy = ball.getBoardCenter().y();
			System.out.println(cx - radius);
			assert cx - radius >= 0;
			System.out.println(cx + radius);
			assert cx + radius <= WIDTH;
			System.out.println(cy - radius);
			assert cy - radius >= 0;
			System.out.println(cy + radius);
			assert cy + radius <= HEIGHT;
			
		}
	}
	
	public enum Action {
		FIRE_ALL, ADD_BALL, ADD_SQUARE, ADD_CIRCLE, ADD_TRIANGLE, ADD_ABSORBER, REVERSE_BALLS, DEFAULT
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
		
		Map<Gadget, List<Gadget>> newTriggers = new HashMap<Gadget, List<Gadget>>(this.triggers);

		List<Gadget> newGadgets = new ArrayList<Gadget>(this.gadgets);
		newGadgets.add(gadget);
		
		Board newBoard = new Board(newGadgets, newTriggers, this.boardTriggers, this.balls);
		newBoard.gravity = this.gravity;
		newBoard.friction1 = this.friction1;
		newBoard.friction2 = this.friction2;
		newBoard.checkRep();
		
		return newBoard;
		
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
	
//	public void generate() {
//		
//		BufferedImage output = new BufferedImage(this.WIDTH * L, this.HEIGHT * L, BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics graphics = (Graphics2D) output.getGraphics();
//		
//		graphics.setColor(Color.BLACK);
//		graphics.fillRect(0, 0, WIDTH * L, HEIGHT * L);
//		
//		final ImageObserver NO_OBSERVER_NEEDED = null;
//		
//		graphics.setColor(Color.BLUE);
//		for (Ball ball : balls) {
//			final Vect anchor = ball.getAnchor().times(L);
//			
//			
//			graphics.drawImage(ball.generate(L), (int) anchor.x(), (int) anchor.y(), NO_OBSERVER_NEEDED);
//					
//		}
//		
//		for (Gadget gadget : gadgets) {
//			final int xAnchor = (int) gadget.position().x()*L;
//			final int yAnchor = (int) gadget.position().y()*L;
//			
//			graphics.drawImage(gadget.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
//			
//		}
//		
//	}

	/**
	 * Will move all balls and gadgets on the board to their new positions/stats after all moves/collisions that would occur
	 * within time have occurred.  
	 * @param time length of time in seconds the board should be "played"
	 */
	public void play(double time) {
		//System.out.println(this.balls);
		for (int i = 0; i < this.balls.size(); i++) {
			//System.out.println(balls.get(i).isTrapped());
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
			ball.move(collisionTime, this.gravity, this.friction1, this.friction2);
			nextGadget.reflectBall(ball);
			if (triggers.containsKey(nextGadget)) {
				for (Gadget gadget : triggers.get(nextGadget)) {
					this.takeAction(gadget, gadget.getAction());
				}
			}
			if (boardTriggers.containsKey(nextGadget)) {
				for (Action action : boardTriggers.get(nextGadget)) {
					//System.out.println(action);
					this.takeAction(nextGadget, action);
				}
			}
			if (ball.getVelocity().length() > 0.0 && collisionTime > 0) {
				moveOneBall(ball, time - collisionTime);
			}
		} else {
			ball.move(time, this.gravity, this.friction1, this.friction2);
		}
	}
	
	private void takeAction(Gadget gadget, Board.Action action) {
		switch (action) {
		case ADD_BALL:{
			// TODO Add a random ball to this board
		}
		case ADD_SQUARE:{
			//TODO CHECK REP
			Gadget newGadget = addSquare(findEmptySpot(1,1));
			for (Ball ball : this.balls) {
				if (newGadget.ballOverlap(ball)) {
					takeAction(gadget, action);
				}
			}
			this.gadgets.add(newGadget);
			checkRep();
			break;
		}
		case ADD_CIRCLE:{
			Gadget newGadget = addCircle(findEmptySpot(1,1));
			for (Ball ball : this.balls) {
				if (newGadget.ballOverlap(ball)) {
					takeAction(gadget, action);
				}
			}
			this.gadgets.add(newGadget);
			checkRep();
			break;
		}
		case ADD_TRIANGLE:{
			Gadget newGadget = addTriangle(findEmptySpot(1,1));
			for (Ball ball : this.balls) {
				if (newGadget.ballOverlap(ball)) {
					takeAction(gadget, action);
				}
			}
			this.gadgets.add(newGadget);
			checkRep();
			break;
		}
		case ADD_ABSORBER:{
			//TODO Can't put absorber in top row
			int randomWidth = (int) (Math.random() * 5 + 1);
			Gadget newGadget = addAbsorber(findEmptySpot(randomWidth, 1), randomWidth, 1);
			for (Ball ball : this.balls) {
				if (newGadget.ballOverlap(ball)) {
					takeAction(gadget, action);
				}
			}
			this.gadgets.add(newGadget);
			this.triggers.put(newGadget, new ArrayList<Gadget>(Arrays.asList(newGadget)));
			checkRep();
			break;
		}
		case REVERSE_BALLS:{
			this.reverseAll();
			checkRep();
			break;
		}
		case FIRE_ALL: {
			this.fireAll(gadget);
			checkRep();
			break;
		}
		case DEFAULT:{
			gadget = gadget.takeAction();
			checkRep();
			break;
		}
		default:
			throw new RuntimeException("Should never get here");
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{FLINGBALL BOARD:[" + this.NAME + ", Gravity: " + this.gravity);
		result.append(", friction1: " + this.friction1 + ", friction2: " + this.friction2);
		result.append("Balls:" + this.balls);
		result.append("Gadgets:" + this.gadgets);
		return result.toString();
	}
	
	/**
	 * Adds a trigger and action to the board. 
	 * 
	 * @param trigger - name of the Gadget which triggers the action
	 * @param action - name of the Gadget on whose action will be taken
	 */
	// TODO make this return a Board
	public void addAction(String trigger, String action, Action toTake) {
		Gadget gTrigger = getGadget(trigger);
		if (!triggers.containsKey(gTrigger)) {
			triggers.put(gTrigger, new ArrayList<Gadget>());
		}
		triggers.get(gTrigger).add(getGadget(action));
		//TODO No longer necessary since each gadget only has one action;
		getGadget(action).setAction(toTake);
	}
	
	public void addAction(String trigger, Action action) {
		Gadget gTrigger = getGadget(trigger);
		if (!boardTriggers.containsKey(gTrigger)) {
			boardTriggers.put(gTrigger, new ArrayList<Action>());
		}
		this.boardTriggers.get(getGadget(trigger)).add(action);
	}
	
	private void fireAll(Gadget gadget) {
		for (Ball ball : balls) {
			if (ball.isTrapped()) {
				ball.release();
				ball.setVelocity(new Vect(Math.random() * 10, Math.random()* 10));
				double x = gadget.position().x();
				double y = gadget.position().y();
				double r = ball.getRadius();
				Vect newPosition = new Vect(x + gadget.width() - r, -y - r);
				// TODO calculate newPosition using absorber Data
				ball.setPosition(newPosition);
			}
		}
	}
	
	private void reverseAll() {
		for (Ball ball : balls) {
			ball.setPosition(ball.getVelocity().rotateBy(Angle.DEG_180));
		}
	}
	
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
	
	
	private Gadget addSquare(Vect position) {
		return new SquareBumper("Square"+ position, (int) position.x(), (int) position.y());
	}
	
	private Gadget addCircle(Vect position) {
		return new CircleBumper("Circle"+ position, (int) position.x(), (int) position.y());
	}
	
	private Gadget addAbsorber(Vect position, int width, int height) {
		return new Absorber("Absorber"+ position, (int) position.x(), (int) position.y(), width, height);
	}
	
	public Vect findEmptySpot(int width, int height) {
		int[][] positions = new int [20][20];
		for (Gadget gadget : gadgets) {
			int x = (int) gadget.position().x();
			int y = (int) gadget.position().y();
			for (int i = x; i < x + gadget.width(); i++) {
				positions[y][i] = 1;
			}
			for (int j = y; j < y + gadget.height(); j++) {
				positions[j][x] = 1;
			}
			
		}
		
		whileloop:
		while (true) {
			int newY = (int) (Math.random() * 20);
			int newX = (int) (Math.random() * 20);
			if ((newX + width > this.WIDTH) || (newY + height > this.HEIGHT)) {
				continue whileloop;
			}
			for (int i = newX; i < newX + width; i++) {
				if (positions[newY][i] == 1) {
					continue whileloop;
				}
			}
			for (int j = newY; j < newY + height; j++) {
				if (positions[j][newX] == 1) {
					continue whileloop;
				}
			}
			
			return new Vect (newX, newY);
		}
	}

}


