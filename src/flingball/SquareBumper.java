/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import physics.Physics;
import physics.Vect;

public class SquareBumper implements Gadget {
	
	private final String trigger = NO_TRIGGER;
	private final String name;
	
	private double reflectionCoefficient = 1.0;
	
	private final int xAnchor, yAnchor;
	
	private final List<Wall> walls;
	/*
	 * TODO: AF()
	 * TODO: Rep Invariant
	 * 	position >= 0
	 * TODO: Safety from rep exposure
	 */
	
	private void checkRep() {
		// TODO
	}
	
	public SquareBumper(String name, int x, int y) {
		this.name = name;
		this.xAnchor = x;
		this.yAnchor = -y;
		
		//Bounding walls
		Wall top = new Wall(name + " top", x, -y, x + 1, -y);
		Wall bottom = new Wall(name + " bottom", x, -y-1, x+1, -y-1);
		Wall left = new Wall(name + " left", x, -y, x, -y-1);
		Wall right = new Wall(name + " right", x+1, -y, x+1, -y-1);
		
		this.walls = new ArrayList<Wall>(Arrays.asList(top, bottom, left, right));
		
	}

	@Override
	public Vect position() {
		return new Vect(xAnchor, -yAnchor);
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
	public int priority() {
		// TODO Auto-generated method stub
		return 0;
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
	public void setTrigger() {
		// TODO not supported throw an exception?

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
