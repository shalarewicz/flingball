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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
	public double getReflectionCoefficient() {
		//TODO Create a checked exception
		throw new RuntimeException("Not supported");
	}
	
	@Override
	public void setReflectionCoefficient(double x) {
		//TODO Create a checked exception
		throw new RuntimeException("Not supported");
	}

	private boolean ballInside(Ball ball) {
		final double x = ball.getBoardCenter().x();
		final double y = ball.getBoardCenter().y();
		final double radius = ball.getRadius();
		final double aX = this.position().x();
		final double aY = this.position().y();
		
		return x - radius > aX && x + radius < aX + this.width && 
				y - radius > aY && y + radius < aY + this.height;
		
	}
	@Override
	public double collisionTime(Ball ball) {
		double collisionTime = Double.POSITIVE_INFINITY;
		if (ballInside(ball)) {
			for (Wall wall : walls) {
				collisionTime = Math.min(collisionTime, wall.collisionTime(ball));
			}
		}
		return collisionTime;
	}

	@Override
	public int priority() {
		// TODO Auto-generated method stub
		return 0;
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
	public void setAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

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
	
	//TODO This mutates the absorber
	private Ball storeBall(Ball ball) {
		final double r = ball.getRadius();
		final Vect newCenter = new Vect(x + width - r, -y + height - r);
		Ball toStore = new Ball(ball.name(), newCenter, new Vect(0, 0)).trap();
		balls.addLast(toStore);
		return toStore;
		
	}

	@Override
	public Ball reflectBall(Ball ball) {
		Ball toReturn = storeBall(ball);
		if (ballCount > 0) {
			return toReturn.setVelocity(new Vect(0, -50)).release();
		}
		else {
			ballCount++;
			return toReturn;
		}
		
	}
	
	@Override
	public String toString() {
		return "Absorber{" + this.name + " " + this.position() + "}";
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
	
//	public static void main(String[] args) {
//		Gadget toDraw = new Absorber("test", 1, 1, 10, 5);
//		JFrame frame = new JFrame("this is ab absorber");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(new JLabel(new ImageIcon(toDraw.generate(20))));
//        frame.pack();
//        frame.setVisible(true);
//	}
}
