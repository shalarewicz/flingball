/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import physics.Vect;

public class Absorber implements Gadget {
	final private int height, width, x, y;
	private String name;
	private String trigger = NO_TRIGGER;
	private Deque<Ball> balls = new LinkedList<Ball>();
	private int ballCount = 0;
	private List<Wall> walls = new ArrayList<Wall>();

	/*
	 * AF(x, y, width, height, name) ::= An absorber (ball return mechanism) covering the rectangle bounded by (x,y)*L
	 * (x + width, y + height)*L with name name holding all balls in balls
	 * Rep Invariant
	 * 	walls.size() == 4
	 *  for each wall in walls each of its end points are shared by two unique walls
	 *  ballCount >= 0
	 * Safety from rep exposure
	 * 	only final or immutable fields are returned. 
	 */

	private void checkRep() {
		assert ballCount >= 0;
		assert walls.size() == 4;
		Set<Vect> endPoints = new HashSet<Vect>();
		for (Wall wall : walls) {
			Vect start = wall.start();
			Vect end = wall.end();
			endPoints.add(start);
			endPoints.add(end);
		}
//		System.out.println(this);
//		System.out.println("End points " + endPoints);
		assert endPoints.size() == 4;
	}
	
	public Absorber(String name, int x, int y, int width, int height) {
		this.x = x;
		this.y = -y;
		this.height = height;
		this.width = width;
		this.name = name;
		
		final Wall top = new Wall(name + " top", x, - y, x + width, -y);
		final Wall bottom = new Wall(name + " bottom", x, -y - height, x + width, -y-height);
		final Wall left = new Wall(name + " left", x, - y, x, -y-height);
		final Wall right = new Wall(name + " right", x + width, -y, x + width, -y-height);
		
		this.walls = new ArrayList<Wall>(Arrays.asList(top, bottom, left, right));
		this.checkRep();
	}

	@Override
	public Vect position() {
		return new Vect(this.x, -this.y);
	}

	@Override
	public String name() {
		return this.name;
	}
	
	@Override 
	public int height() {
		return this.height;
	}
	
	@Override 
	public int width() {
		return this.width;
	}
	
	@Override 
	public int area() {
		return this.width * this.height + 1;
	}

	@Override
	public double getReflectionCoefficient() {
		//TODO Create a checked exception Fixed when Absorber has its' own interface
		throw new RuntimeException("Not supported");
	}
	
	@Override
	public void setReflectionCoefficient(double x) {
		//TODO Create a checked exception Fixed when Absorber has its' own interface
		throw new RuntimeException("Not supported");
	}

	//Checking Absorber{Absorber<13.0,5.0> <13.0,5.0> width=2 height=2[]} with Ball{name=ball4, center=<13.8,5.5>, velocity=<20.0,75.0>, radius=0.25}
	private boolean ballInside(Ball ball) {
		final double x = ball.getBoardCenter().x();
		final double y = ball.getBoardCenter().y();
		final double radius = ball.getRadius();
		final double aX = this.position().x();
		final double aY = this.position().y();
		
		return (x - radius >= aX) && (x + radius <= aX + this.width) && 
				(y - radius >= aY) && (y + radius <= aY + this.height);
		
	}
	
	@Override
	public double collisionTime(Ball ball) {
		double collisionTime = Double.POSITIVE_INFINITY;
		if (!ballInside(ball)) {
			for (Wall wall : walls) {
				collisionTime = Math.min(collisionTime, wall.collisionTime(ball));
			}
		}
		return collisionTime;
	}

	/**
	 * 
	 * @param trigger
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;

	}

	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public BufferedImage generate(int L) {
		BufferedImage output = new BufferedImage(L*this.width, L*this.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.PINK);
        graphics.fillRect(0, 0, width*L, height*L);
        
        if (ballCount > 0) {
        	graphics.setColor(Color.BLUE);
        	Ball toDraw = balls.getFirst();
        	final double diameter = toDraw.getRadius() * 2;
        	
        	final int xAnchor = (int) ((this.width - diameter) * L);
        	final int yAnchor = (int) ((this.height - diameter) * L);
        	
        	final ImageObserver NO_OBSERVER_NEEDED = null;
        	
        	graphics.drawImage(toDraw.generate(L), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
        	

        }
        
        return output;
	}
	
	@Override
	public void reflectBall(Ball ball) {
		ball.setVelocity(new Vect(0, 0));
		final double r = ball.getRadius();
		ball.setPosition(new Vect(x + width - r, -y + height - r));
		ball.trap();
		balls.addLast(ball);
		ballCount++;
	}
	
	@Override
	public void takeAction() {
		Ball toFire = balls.removeFirst();
		toFire.release();
		toFire.setVelocity(new Vect(0, -50));
		double r = toFire.getRadius();
		toFire.setPosition(new Vect(x + width - r, -y - r));
		ballCount--;
	}
	
	@Override
	public String toString() {
		return "Absorber{" + this.name + " " + this.position() + " width=" + this.width + " height=" + this.height() + this.balls + "}";
	}
	
	@Override
	public int hashCode() {
		return this.x + this.y;
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Absorber && this.samePosition((Absorber) that);
	}

	private boolean samePosition(Absorber that) {
		return this.x == that.x && this.y == that.y && this.width == that.width && this.height == that.height;
 	}

	@Override
	public boolean ballOverlap(Ball ball) {
		final double x = ball.getBoardCenter().x();
		final double y = ball.getBoardCenter().y();
		final double radius = ball.getRadius();
		final double aX = this.position().x();
		final double aY = this.position().y();
		
		return ((x + radius > aX && x + radius < aX + this.width) ||
				(x - radius > aX && x - radius < aX + this.width) ) &&
				((y + radius > aY && y + radius < aY + this.height) ||
				(y - radius > aY && y - radius < aY + this.height));
		
		
	}
	
	@Override
	public void setCoverage(int[][] coverage) {
		int x = (int) this.position().x();
		int y = (int) this.position().y();
		
		for (int j = y; j < y + this.height; j++) {
			for (int i = x; i < x + this.width; i++) {
			coverage[j][i] = 1;
			}
		}
		coverage[y - 1][x + this.width() - 1] = 1;
	}

	
	@Override
	public void fireAll() {
		for (Ball ball :this.balls) {
			ball.setPosition(new Vect(this.x + this.width - ball.getRadius(), this.y - ball.getRadius()));
			ball.setVelocity(new Vect(Math.random() * 100, -Math.random()* 100));
			ball.release();
		}
		ballCount = 0;
	}
}
