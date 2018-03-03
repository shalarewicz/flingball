/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import physics.Vect;

public class TriangleBumper implements Gadget {
	/*
	 * AF(x, y , orientation) - Triangle pumper with anchor x, y and orientation 
	 * Rep Invariant
	 * walls.size() == 3
	 * for each endpoint of a wall there exists only one other wall with that endpoint
	 * Safety from rep exposure
	 * 		only final or immutable fields returend. 
	 */
	
	private final int x, y;
	private Orientation orientation = Orientation.ZERO;
	private final List<Wall> walls;
	private String name;
	private String trigger = Gadget.NO_TRIGGER;
	
	private double reflectionCoefficient;
	
	public enum Orientation{
		ZERO, NINETY, ONEEIGHTY, TWOSEVENTY
	}

	private void checkRep() {
		assert this.walls.size() == 3;
		Set<Vect> endPoints = new HashSet<Vect>();
		for (Wall wall : walls) {
			Vect start = wall.start();
			Vect end = wall.end();
			endPoints.add(start);
			endPoints.add(end);
		}
		assert endPoints.size() == 3;
	}
	
	public TriangleBumper(String name, int x, int y) {
		this.x = x;
		this.y = -y;
		this.name = name;
		
		final Wall side1 = new Wall(name + " side1", x, -y, x+1, -y);
		final Wall side2 = new Wall(name + " side2", x+1, -y, x+1, -y-1);
		final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y, x+1, -y-1);
		walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
		checkRep();
	}
	
	public TriangleBumper(String name, int x, int y, Orientation orientation) {
		this.x = x;
		this.y = -y;
		this.orientation = orientation;
		this.name = name;
		
		switch (this.orientation) {
		case ZERO:{
			final Wall side1 = new Wall(name + " side1", x, -y, x+1, -y);
			final Wall side2 = new Wall(name + " side2", x, -y, x, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y-1, x+1, -y);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		case NINETY: {
			final Wall side1 = new Wall(name + " side1", x+1, -y, x+1, -y-1);
			final Wall side2 = new Wall(name + " side2", x+1, -y, x, -y);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y, x+1, -y-1);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		
		case ONEEIGHTY: {
			final Wall side1 = new Wall(name + " side1", x+1, -y-1, x+1, -y);
			final Wall side2 = new Wall(name + " side2", x+1, -y-1, x, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y-1, x+1, -y);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		
		case TWOSEVENTY: {
			final Wall side1 = new Wall(name + " side1", x, -y-1, x, -y);
			final Wall side2 = new Wall(name + " side2", x, -y-1, x+1, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y, x+1, -y-1);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		
		default: throw new RuntimeException("Should never get here. Cannot make triangle bumper");
		}
	}

	@Override
	public Vect position() {
		return new Vect(x, -y);
	}

	@Override
	public String name() {
		return this.name;
	}
	
	@Override 
	public int height() {
		return 1;
	}
	
	@Override 
	public int width() {
		return 1;
	}

	@Override
	public double getReflectionCoefficient() {
		return this.reflectionCoefficient;
	}
	
	@Override
	public void setReflectionCoefficient(double x) {
		this.reflectionCoefficient = x;
	}

	@Override
	public double collisionTime(Ball ball) {
		double collisionTime = Double.POSITIVE_INFINITY;
		for (Wall wall : walls) {
			collisionTime = Math.min(collisionTime, wall.collisionTime(ball));
		}
		return collisionTime;
	}

	@Override
	public int priority() {
		// TODO 
		throw new RuntimeException("Not yet implemeted");
	}


	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public void setAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public BufferedImage generate(int L) {
		BufferedImage output = new BufferedImage(L, L, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.GREEN);
        switch (this.orientation) {
        case ZERO:{
        	final int[] xPoints = {0, L, 0};
        	final int[] yPoints = {0, 0, L};
        	graphics.fillPolygon(xPoints, yPoints, 3);
        	return output;
        }
        case NINETY:{
        	final int[] xPoints = {0, L, L};
        	final int[] yPoints = {0, 0, L};
        	graphics.fillPolygon(xPoints, yPoints, 3);
        	return output;
        }
        case ONEEIGHTY:{
        	final int[] xPoints = {L, L, 0};
        	final int[] yPoints = {0, L, L};
        	graphics.fillPolygon(xPoints, yPoints, 3);
        	return output;
        }
        case TWOSEVENTY:{
        	final int[] xPoints = {0, L, 0};
        	final int[] yPoints = {0, L, L};
        	graphics.fillPolygon(xPoints, yPoints, 3);
        	return output;
        }
        default:
        	throw new RuntimeException("Should never get here. Cannot generate triangle bunper");
        
        }
        
	}

	@Override
	public Ball reflectBall(Ball ball) {
		double collisionTime = this.collisionTime(ball);
		for (Wall wall : walls) {
			if (wall.collisionTime(ball) == collisionTime) {
				return wall.reflectBall(ball);
			}
		}
		
		throw new RuntimeException("Should never get here. Ball did not collide with Triangle Bumper");
	}
	
	@Override
	public String toString() {
		return "Triangle Bumper{" + this.name + " " + this.position() + " " + this.orientation +"}";
	}
}
