/**
 * Author:
 * Date:
 */
package flingball;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import physics.*;

/**
 * An immutable class representing a ball for use on a flingball board. All balls must have radius > 0;
 */
public class Ball {
	
	private Vect position, velocity;
	private double radius;
	
	/*
	 * AF(position, velocity, radius) ::= A ball with radius r, center = position, moving with velocity velocity. )
	 * Rep Invariant
	 * 	radius > 0 - A ball with 0 radius cannot exist as it would not collide with a gadget
	 * 	0 <= |velocity| <= 200
	 * Safety from rep exposure
	 * 	timeUntilCollision and reflect methods return new Ball objects
	 * 	getVelocity and getPosition return immutable Vect
	 */

	private void checkRep() {
		assert radius >= 0;
		assert Math.abs(this.velocity.length()) <= 200;
		assert Math.abs(this.velocity.length()) >= 0;
	}
	
	/**
	 * Creates a new Ball. 
	 * @param position center of the ball
	 * @param velocity velocity of the ball
	 * @param radius radius of the ball. Must be greater than zero
	 */
	public Ball(Vect position, Vect velocity, double radius) {
		this.position = new Vect(position.x(), -position.y());
		this.velocity = velocity;
		this.radius = radius;
		checkRep();
	}
	
	/**
	 * Moves the ball the distance it would travel during time time given the current velocity of the ball. Does not account
	 * for the effects of gravity or friction. 
	 * 
	 * @param time time in seconds during which the ball is moving. 
	 * @return A new ball in the end location. 
	 */
	
	//TODO Ball should be mutable? otherwise need to remove and add balls to the board each time they change
	public void move(double time) {
		// distance = position + velocity * t
		Vect newPosition = this.position.plus(this.velocity.times(time));
		this.position = newPosition;
	//	return new Ball(newPosition, this.velocity, this.radius);
	}
	
	/**
	 * Moves the ball the distance it would travel during time time given the current velocity of the ball while accounting for both gravity and friction. 
	 * 
	 * @param time time in seconds during which the ball will travel. 
	 * @param gravity - Gravity constant for the gives space in L / s^-2
	 * @param mu - Coefficient of friction for the flingball board in s^-1
	 * @param mu2 - Coefficient of friction for the flingball board in L^-1
	 * @return A new ball in the end location moving with velocity affected by gravity and friction
	 */
	public Ball move(double time, double gravity, double mu, double mu2) {
		// let a = acceleration
		// displacement = position + velocity * t + at^2
		// delta_v = at
		// v_new_f = v_old * (1 - mu*delta_t - mu2*|v_old|*delta_t) 
		
		double v_initial = this.velocity.length();
		
		//Change in velocity due to gravity = gravity * time
		final Vect deltaVGravity = new Vect(Angle.DEG_270, gravity).times(time);
		
		// Final velocity as a result of friction
		double vFinalFriction = v_initial * (1 - mu*time - mu2 * time * Math.abs(v_initial));
		
		//Change in velocity due to friction = a_f * t (where a_f is acceleration due to friction)
		Vect deltaVFriction = new Vect(this.velocity.angle().plus(Angle.DEG_180), Math.abs(vFinalFriction - v_initial));
		
		// Net change in velocity = a*t
		Vect deltaV = deltaVGravity.plus(deltaVFriction);
		
		Vect newVelocity = this.velocity.plus(deltaV);
		
		// displacement = v_i*t + a*t*t = v_i*t + deltaV * t
		Vect displacement = this.velocity.times(time).plus(deltaV.times(time));
		
		Vect newPosition = this.position.plus(displacement);
		
		return new Ball(newPosition, newVelocity, this.radius);
		
	}
	
	
	
	/**
	 * 
	 * @return The position of the origin of the bounding box of the ball
	 */
	public Vect getPosition() {
		
		final double xAnchor = this.position.x() - radius;
		final double yAnchor = -this.position.y() - radius;
		Vect result = new Vect(xAnchor, yAnchor);
		return result;
	}
	
	/**
	 * 
	 * @return Radius of the ball
	 */
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * 
	 * @return The current velocity of the ball
	 */
	public Vect getVelocity() {
		return this.velocity;
	}
	
	/**
	 * 
	 * @param v The new velocity of the ball
	 * @return A ball with velocity v
	 */
	public Ball setVelocity(Vect v) {
		return new Ball(this.position, v, this.radius);
	}
	
	/**
	 * @param L the dimension of the board 20L x 20L pixels wide
	 * @return An image of the ball
	 */
	public BufferedImage generate(int L) {
		final int diameter = (int) (radius*L);
		BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.BLUE);
        graphics.fillArc(0, 0, diameter, diameter, 0, 360);
        
		return output;
	}
	

	/**
	 * Calculates the time in seconds until the ball may collide with a line segment. If no collision will occur Double.POSITIVE_INFINITY is 
	 * returned. 
	 * 
	 * @param line Line with which the ball may collide
	 * @return Collision time in seconds or POSITIVE_INFINITY if no collision will occur
	 */
	public double timeUntilLineCollision(LineSegment line) {
		return Physics.timeUntilWallCollision(line, new Circle(this.position, this.radius), this.velocity);
	}
	
	/**
	 * Calculates the time in seconds until the ball may collide with a circle. If no collision will occur Double.POSITIVE_INFINITY is 
	 * returned. 
	 * 
	 * @param circle circle with which the ball may collide
	 * @return Collision time in seconds or POSITIVE_INFINITY if no collision will occur
	 */
	public double timeUntilCircleCollision(Circle circle) {
		return Physics.timeUntilCircleCollision(circle, new Circle(this.position, this.radius), this.velocity);
	}
	
	/**
	 * Returns a ball that has perfect elastic collision with a line,
	 * 
	 * @param line with which the ball is colliding
	 * @return A ball that has collided with the line. 
	 */
	public Ball reflectLine(LineSegment line) {
		return new Ball(new Vect(this.position.x(), -this.position.y()), Physics.reflectWall(line, this.velocity), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a line segment accounting for the lines coefficient of reflection
	 * @param line line with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the line
	 * @return A ball which has collided with the line
	 */
	public Ball reflectLine(LineSegment line, Double reflectionCoeff) {
		return new Ball(new Vect(this.position.x(), -this.position.y()), Physics.reflectWall(line, this.velocity, reflectionCoeff), this.radius);
	}
	
	/**
	 * Returns a ball that has perfect elastic collision with a circle
	 * @param circle circle with which the ball is colliding
	 * @return A ball that has collided with the circle
	 */
	public Ball reflectCircle(Circle circle) {
		return new Ball(new Vect(this.position.x(), -this.position.y()), Physics.reflectCircle(circle.getCenter(), this.position, velocity), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a circle accounting for the lines coefficient of reflection
	 * @param circle circle with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the circle
	 * @return A ball which has collided with the circle
	 */
	public Ball reflectCircle(Circle circle, Double reflectionCoeff) {
		return new Ball(new Vect(this.position.x(), -this.position.y()), Physics.reflectCircle(circle.getCenter(), this.position, velocity, reflectionCoeff), this.radius);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Ball && this.sameParts((Ball) that);
	}

	private boolean sameParts(Ball that) {
		return this.velocity == that.velocity &&
				this.position == that.position &&
				this.radius == that.radius;
	}
	
	@Override
	public String toString() {
		return "Ball{center=" + this.position + ", velocity=" + this.velocity + ", radius=" + this.radius + "}";
	}
	
	@Override
	public int hashCode() {
		//TODO
		throw new RuntimeException("Not yet implemented");
	}
	public static void main(String[] args) {
        JFrame frame = new JFrame("this is a ball");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ball toDraw = new Ball(new Vect(10,10), Vect.ZERO, 1.0);
        frame.add(new JLabel(new ImageIcon(toDraw.generate(20))));
        frame.pack();
        frame.setVisible(true);
    }
}

