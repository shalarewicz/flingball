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

public class SquareBumper implements Gadget {
	
	private final String trigger = Gadget.NO_TRIGGER;
	private final String name;
	
	private double reflectionCoefficient = Gadget.REFLECTION_COEFFICIENT;
	
	private final int x, y;
	
	private final int WIDTH = 1;
	private final int HEIGHT = 1;
	
	private final List<Wall> walls;
	

	/*
	 * AF(name, xAnchor, yAnchor) ::= A square bumper at (xAnchor, yAnchor) on a flingBall board. 
	 * Rep Invariant
	 * 	walls.size() == 4
	 *  for each wall in walls each of its end points are shared by two unique walls
	 * Safety from rep exposure
	 * 	only final or immutable fields returned
	 */
	
	private void checkRep() {
		assert walls.size() == 4;
		Set<Vect> endPoints = new HashSet<Vect>();
		for (Wall wall : walls) {
			Vect start = wall.start();
			Vect end = wall.end();
			endPoints.add(start);
			endPoints.add(end);
		}
		assert endPoints.size() == 4;
	}
	
	public SquareBumper(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = -y;
		
		//Bounding walls
		final Wall top = new Wall(name + " top", x, - y, x + 1, -y);
		final Wall bottom = new Wall(name + " bottom", x, -y-1, x+1, -y-1);
		final Wall left = new Wall(name + " left", x, - y, x, -y-1);
		final Wall right = new Wall(name + " right", x+1, -y, x+1, -y-1);
		
		this.walls = new ArrayList<Wall>(Arrays.asList(top, bottom, left, right));
		this.checkRep();
		
	}
	
	public SquareBumper(String name, Vect position) {
		this.name = name;
		int x = (int) position.x();
		int y = (int) -position.y();
		this.x = x;
		this.y = y;
		
		//Bounding walls
		final Wall top = new Wall(name + " top", x, - y, x + 1, -y);
		final Wall bottom = new Wall(name + " bottom", x, -y-1, x+1, -y-1);
		final Wall left = new Wall(name + " left", x, - y, x, -y-1);
		final Wall right = new Wall(name + " right", x+1, -y, x+1, -y-1);
		
		this.walls = new ArrayList<Wall>(Arrays.asList(top, bottom, left, right));
		this.checkRep();
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
		return this.HEIGHT;
	}
	
	@Override 
	public int width() {
		return this.WIDTH;
	}

	@Override 
	public int area() {
		return this.WIDTH * this.HEIGHT;
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
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public BufferedImage generate(int L) {
		
		BufferedImage output = new BufferedImage(L, L, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, L, L);
        
        return output;
		
	}

	@Override
	public void reflectBall(Ball ball) {
		double collisionTime = this.collisionTime(ball);
		for (Wall wall : walls) {
			if (wall.collisionTime(ball) == collisionTime) {
				wall.reflectBall(ball);
			}
		}
		//throw new RuntimeException("Should never get here. Ball did not collide with SquareBumper");
	}
	
	
	@Override
	public String toString() {
		return "Square Bumper:" + this.name + " " + this.position();
	}
	
	@Override
	public int hashCode() {
		// Does this create a problem for a square and circle bumper in the same spot?
		return this.x + this.y;
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof SquareBumper && this.samePosition((SquareBumper) that);
	}

	private boolean samePosition(SquareBumper that) {
		return this.x == that.x && this.y == that.y;
 	}
	
	
	@Override
	public boolean ballOverlap(Ball ball) {
		final double x = ball.getBoardCenter().x();
		final double y = ball.getBoardCenter().y();
		final double radius = ball.getRadius();
		final double aX = this.position().x();
		final double aY = this.position().y();
		
		if (((x + radius > aX && x + radius < aX + WIDTH) ||
				(x - radius > aX && x - radius < aX + this.WIDTH) ) &&
				((y + radius > aY && y + radius < aY + this.HEIGHT) ||
				(y - radius > aY && y - radius < aY + this.HEIGHT))) {
		}
		
		return ((x + radius > aX && x + radius < aX + this.width()) ||
				(x - radius > aX && x - radius < aX + this.width()) ) &&
				((y + radius > aY && y + radius < aY + this.height()) ||
				(y - radius > aY && y - radius < aY + this.height()));
	}

	@Override
	public void takeAction() {
		// do nothing
	}

	@Override
	public void setCoverage(int[][] coverage) {
		int x = (int) this.position().x();
		int y = (int) this.position().y();
		coverage[y][x] = 1;
	}
	
	@Override
	public void fireAll() {
		// do nothing.
	}

}
