/**
 * Author:
 * Date:
 */
package flingball;

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

	private final int x, y;
	private final double RADIUS = 0.5;
	private String name;
	private double reflectionCoefficient = 1.0;
	private final Circle bumper;
	
	public CircleBumper(String name, int x, int y) {
		this.x = x;
		this.y = -y;
		this.name = name;
		this.bumper = new Circle(new Vect (x + RADIUS, y - RADIUS), RADIUS);
		checkRep();
	}
	
	private void checkRep() {
		assert true;
	}

	@Override
	public Vect position() {
		return new Vect(x, y);
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
		return ball.reflectCircle(this.bumper);
	}
}
