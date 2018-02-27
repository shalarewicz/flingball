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
	/*
	 * TODO: AF(x, y, name) ::= A circle with center x, y and radius r named name
	 * TODO: Rep Invariant
	 * TODO: Safety from rep exposure
	 */

	private final String trigger = NO_TRIGGER;
	private final int x, y;
	private final double RADIUS = 0.5;
	private String name;
	private double reflectionCoefficient = Gadget.REFLECTION_COEFFICIENT;
	private final Circle bumper;
	
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
        
        graphics.setColor(Color.ORANGE);
        graphics.fillArc(0, 0, L, L, 0, 360);
        
        return output;
	}

	@Override
	public Ball reflectBall(Ball ball) {
		return ball.reflectCircle(this.bumper);
	}
	
	@Override
	public String toString() {
		return "Circle Bumper:" + this.name + " " + this.position();
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
}
