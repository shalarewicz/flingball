/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import physics.Vect;

public class TriangleBumper implements Gadget {
	/*
	 * TODO: AF()
	 * TODO: Rep Invariant
	 * TODO: Safety from rep exposure
	 */
	
	private final int x, y;
	private final Orientation orientation;
	private final List<Wall> walls;
	private String name;
	
	private double reflectionCoefficient;
	
	public enum Orientation{
		ZERO, NINETY, ONEEIGHTY, TWOSEVENTY
	}

	private void checkRep() {
		// TODO
	}
	
	public TriangleBumper(String name, int x, int y, Orientation orientation) {
		this.x = x;
		this.y = -y;
		this.orientation = orientation;
		this.name = name;
		
		switch (this.orientation) {
		case ZERO:{
			final Wall side1 = new Wall(name + " side1", x, -y, x+1, -y);
			final Wall side2 = new Wall(name + " side2", x+1, -y, x+1, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y, x+1, -y-1);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		case NINETY: {
			final Wall side1 = new Wall(name + " side1", x+1, -y, x+1, -y-1);
			final Wall side2 = new Wall(name + " side2", x+1, -y-1, x, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y-1, x+1, -y);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		
		case ONEEIGHTY: {
			final Wall side1 = new Wall(name + " side1", x, -y, x, -y-1);
			final Wall side2 = new Wall(name + " side2", x, -y-1, x+1, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y, x+1, -y-1);
			walls = new ArrayList<Wall>(Arrays.asList(side1, side2, hypotenuse));
			checkRep();
			return;
		}
		
		case TWOSEVENTY: {
			final Wall side1 = new Wall(name + " side1", x, -y, x+1, -y);
			final Wall side2 = new Wall(name + " side2", x, -y, x, -y-1);
			final Wall hypotenuse = new Wall(name + " hypotenuse", x, -y-1, x+1, -y);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTrigger() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ball reflectBall(Ball ball) {
		double collisionTime = this.collisionTime(ball);
		for (Wall wall : walls) {
			if (wall.collisionTime(ball) == collisionTime) {
				return wall.reflectBall(ball);
			}
		}
		
		throw new RuntimeException("Should never get here. Ball did not collide with SquareBumper");
	}
}
