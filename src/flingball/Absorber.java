/**
 * Author:
 * Date:
 */
package flingball;

import java.awt.image.BufferedImage;

import physics.Vect;

public class Absorber implements Gadget {
	/*
	 * TODO: AF()
	 * TODO: Rep Invariant
	 * TODO: Safety from rep exposure
	 */

	private void checkRep() {
		// TODO
	}

	@Override
	public Vect position() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getReflectionCoefficient() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setReflectionCoefficient(double x) {
		
	}

	@Override
	public double collisionTime(Ball ball) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}
}
