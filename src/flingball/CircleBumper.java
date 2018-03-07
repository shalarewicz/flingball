/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import physics.Circle;
import physics.Physics;
import physics.Vect;

public class CircleBumper implements Gadget {
	
	private static final double SPIN_RATE = 5.0;
	/*
	 * AF(x, y, name) ::= A circle with center x, y and radius r named name
	 * Rep Invariant
	 * 		true
	 *  Safety from rep exposure
	 * 		Only final or immutable fields are returned. 
	 */

	private final String trigger = NO_TRIGGER;
	private final int x, y;
	private final double RADIUS = 0.5;
	private String name;
	private double reflectionCoefficient = Gadget.REFLECTION_COEFFICIENT;
	private final Circle bumper;
	private Board.Action action = Board.Action.DEFAULT;

	private double spin = 0;
	
	public CircleBumper(String name, int x, int y) {
		this.x = x;
		this.y = -y;
		this.name = name;
		this.bumper = new Circle(new Vect (x + RADIUS, -y - RADIUS), RADIUS);
		checkRep();
	}
	
	private void checkRep() {
		assert true;
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
		return ball.timeUntilCircleCollision(bumper);
	}

	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public Gadget setAction(Board.Action action) {
		//TODO - new constructor
		
		CircleBumper result = this;
		result.action = action;
		return result;
	}

	@Override
	public Board.Action getAction() {
		return this.action;
	}

	@Override
	public BufferedImage generate(int L) {
		BufferedImage output = new BufferedImage(L, L, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.ORANGE);
        graphics.fillArc(0, 0, L, L, 0, 360);
        
        return output;
	}

	@Override
	public void reflectBall(Ball ball) {
		if (this.spin == 0) {
			ball.reflectCircle(this.bumper);
		} else {
			ball.reflectRotatingCircle(this.bumper, this.spin, this.reflectionCoefficient);
		}
	}
	
	@Override
	public String toString() {
		return "Circle Bumper:" + this.name + " " + this.position() + ", spin=" + this.spin;
	}
	
	@Override
	public int hashCode() {
		return this.x + this.y;
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof CircleBumper && this.samePosition((CircleBumper) that);
	}

	private boolean samePosition(CircleBumper that) {
		return this.x == that.x && this.y == that.y;
 	}

	@Override
	public boolean ballOverlap(Ball ball) {
		double distance = Math.sqrt(Physics.distanceSquared(ball.getBoardCenter(), this.bumper.getCenter()));
		return distance < ball.getRadius() + this.RADIUS;
		
	}

	@Override
	public Gadget takeAction() {
		CircleBumper result = this;
		//if (result.spin == 0) {
			result.spin = SPIN_RATE;
		//}
		return result;
	}
	
	@Override
	public void setCoverage(int[][] coverage) {
		int x = (int) this.position().x();
		int y = (int) this.position().y();
		coverage[y][x] = 1;
	}
}
