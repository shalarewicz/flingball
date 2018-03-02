/**
 * Author:
 * Date:
 */
package flingball;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import physics.*;

/**
 * An immutable class representing a ball for use on a flingball board. All balls must have radius > 0;
 */ 
public class Ball {
	
	private Vect boardCenter, cartesianCenter, boardVelocity, cartesianVelocity, anchor;
	// default diameter is 0.5L
	private double radius = 0.25;
	private String name;
	
	
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
		assert Math.abs(this.boardVelocity.length()) <= 200;
		assert Math.abs(this.boardVelocity.length()) >= 0;
	}
	
	/**
	 * Creates a new Ball for use on a flingBall board with center center
	 * @param center center of the ball
	 * @param velocity velocity of the ball
	 * @param radius radius of the ball. Must be greater than zero
	 */
	public Ball(Vect center, Vect velocity, double radius) {
		this.name = "Test ball";
		this.boardCenter = center;
		this.cartesianCenter = new Vect(this.boardCenter.x(), -this.boardCenter.y());
		this.anchor = new Vect(center.x() - radius, center.y() - radius);
		
		//TODO: Remove if not needed since using BigDecimal
		this.boardVelocity = new Vect(velocity.x(), velocity.y());
		this.cartesianVelocity = new Vect(velocity.x(), -velocity.y());
		
		this.radius = radius;
		checkRep();
	}
	
	/**
	 * Creates a new Ball for use on a flingBall board
	 * @param name name of the ball
	 * @param center center of the ball
	 * @param velocity velocity of the ball
	 */
	public Ball(String name, Vect center, Vect velocity) {
		this.name = name;
		this.boardCenter = center;
		this.cartesianCenter = new Vect(this.boardCenter.x(), -this.boardCenter.y());
		this.anchor = new Vect(center.x() - radius, center.y() - radius);
		
		//TODO: Remove if not needed since using BigDecimal
		this.boardVelocity = new Vect(velocity.x(), velocity.y());
		this.cartesianVelocity = new Vect(velocity.x(), -velocity.y());
		
		checkRep();
	}
	
	/**
	 * Creates a new Ball for use on a flingBall board
	 * @param name name of the ball
	 * @param center center of the ball
	 * @param velocity velocity of the ball
	 * @param radius radius of the ball. Must be greater than zero
	 */
	public Ball(String name, Vect center, Vect velocity, double radius) {
		this.name = name;
		this.boardCenter = center;
		this.cartesianCenter = new Vect(this.boardCenter.x(), -this.boardCenter.y());
		this.anchor = new Vect(center.x() - radius, center.y() - radius);
		
		//TODO: Remove if not needed since using BigDecimal
		this.boardVelocity = new Vect(velocity.x(), velocity.y());
		this.cartesianVelocity = new Vect(velocity.x(), -velocity.y());
		this.radius = radius;
		
		checkRep();
	}
	
	/**
	 * Given the coordinate of the center of a ball on a flingball board. Returns the center in Cartesian space.
	 * 
	 * @param center center of the ball on a flingabll board
	 * @return center of the ball in Cartesian Space. 
	 */
	private static Vect getCartesianCenterFromBoardCenter(Vect boardCenter) {
		return new Vect(boardCenter.x(), -boardCenter.y());
	}
	
	private static Vect getBoardCenterFromCartesianCenter(Vect cartesianCenter) {
		return new Vect(cartesianCenter.x(), -cartesianCenter.y());
	}
	
	/**
	 * Computes the anchor of the bounding box of the ball. The anchor is the top left corner of the bounding box.
	 * @return anchor of the ball on a flingBall board. 
	 */
	private Vect getAnchorFromCartesianCenter(Vect cartesianCenter) {
		return new Vect(cartesianCenter.x() - this.radius, -cartesianCenter.y() - this.radius);
	}
	
	private Vect getAnchorFromBoardCenter(Vect boardCenter) {
		return new Vect(boardCenter.x() - this.radius, boardCenter.y() - this.radius);
	}
	
	/**
	 * Moves the ball the distance it would travel during time time given the current velocity of the ball. Does not account
	 * for the effects of gravity or friction. 
	 * 
	 * @param time time in seconds during which the ball is moving. 
	 * @return A new ball in the end location. 
	 */
	
	//TODO Ball should be mutable? otherwise need to remove and add balls to the board each time they change
	public Ball move(double time) {
		// distance = position + velocity * t
		Vect newCenter = this.cartesianVelocity.times(time).plus(this.cartesianCenter);
		
		//Round Decimal to avoid floating point math errors after ~15 decimal places
		DecimalFormat df = new DecimalFormat("#.###########");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		double newXCenter = Double.parseDouble(df.format(newCenter.x()));
		double newYCenter = Double.parseDouble(df.format(newCenter.y()));
		
		Vect roundedCenter = new Vect(newXCenter, newYCenter);
		
		this.cartesianCenter = roundedCenter;
		this.boardCenter = getBoardCenterFromCartesianCenter(roundedCenter);
		this.anchor = getAnchorFromCartesianCenter(roundedCenter);
		return new Ball(this.name, getBoardCenterFromCartesianCenter(roundedCenter), this.boardVelocity, this.radius);
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
		
		DecimalFormat df = new DecimalFormat("#.###########");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		
		//TODO: Floating point math is messing this up
		double v_initial = this.cartesianVelocity.length();
		
		//Change in velocity due to gravity = gravity * time
		final Vect deltaVGravity = new Vect(Angle.DEG_270, gravity).times(time);
		System.out.println("dv gravity = " + deltaVGravity);
		
		// Final velocity as a result of friction
		double vFinalFriction = v_initial * (1 - mu*time - mu2 * time * Math.abs(v_initial));
		
		//Change in velocity due to friction = a_f * t (where a_f is acceleration due to friction)
		Vect deltaVFriction = new Vect(this.cartesianVelocity.angle().plus(Angle.DEG_180), Math.abs(vFinalFriction - v_initial));
		
		System.out.println("dv friction: " + deltaVFriction);
		
		// Net change in velocity = a*t
		Vect deltaV = deltaVGravity.plus(deltaVFriction);
		System.out.println("dv = " + deltaV);
		
		Vect newVelocity = this.cartesianVelocity.plus(deltaV);
		
		System.out.println("newV = " + newVelocity);
		
		double newVX = Double.parseDouble(df.format(newVelocity.x()));
		double newVY = Double.parseDouble(df.format(newVelocity.y()));
		
		Vect roundedVelocity = new Vect(newVX, newVY);
		
		// displacement = v_i*t + a*t*t = v_i*t + deltaV * t
		Vect displacement = this.cartesianVelocity.times(time).plus(deltaV.times(time));
		
		Vect newCartesianCenter = this.cartesianCenter.plus(displacement);
		double newCX = Double.parseDouble(df.format(newCartesianCenter.x()));
		double newCY = Double.parseDouble(df.format(newCartesianCenter.y()));
		Vect correctedCartesianCenter = new Vect(newCX, newCY);
		
		Vect newBoardCenter = getBoardCenterFromCartesianCenter(correctedCartesianCenter);
		
		return new Ball(this.name, newBoardCenter, getBoardCenterFromCartesianCenter(roundedVelocity), this.radius);
		
	}
	
	
	
	/**
	 * 
	 * @return The position of the origin (top left) of the bounding box of the ball
	 */
	//TODO Does this need to be public?
	public Vect getAnchor() {
		return this.anchor;
	}
	
	/**
	 * 
	 * @return The position of the center of the ball on a flingball board.
	 */
	public Vect getBoardCenter() {
		return this.boardCenter;
	}
	
	/**
	 * 
	 * @return The position of the center of the ball in Cartesian Space.
	 */
	private Vect getCartesianCenter() {
		return this.cartesianCenter;
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
	 * @return The current velocity of the ball on the flingball board.
	 */
	public Vect getVelocity() {
		return this.boardVelocity;
	}
	
	/**
	 * Converts velocity from Cartesian to flingball space and vice versa
	 * @param v velocity to be converted
	 * @return new velocity
	 */
	private static Vect convertVelocity(Vect v) {
		return new Vect(v.x(), -v.y());
	}
	/**
	 * 
	 * @param v The new velocity of the ball
	 * @return A ball with velocity v
	 */
	public Ball setVelocity(Vect v) {
		return new Ball(this.boardCenter, v, this.radius);
	}
	
	/**
	 * @param L the dimension of the board 20L x 20L pixels wide
	 * @return An image of the ball
	 */
	public BufferedImage generate(int L) {
		final int diameter = (int) (2*radius*L);
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
		return Physics.timeUntilWallCollision(line, new Circle(this.cartesianCenter, this.radius), this.cartesianVelocity);
	}
	
	/**
	 * Calculates the time in seconds until the ball may collide with a circle. If no collision will occur Double.POSITIVE_INFINITY is 
	 * returned. 
	 * 
	 * @param circle circle with which the ball may collide
	 * @return Collision time in seconds or POSITIVE_INFINITY if no collision will occur
	 */
	public double timeUntilCircleCollision(Circle circle) {
		//TODO: NOT WORKING
		//TODO: System.out.println("Ball 226: Bumper: " + circle.toString() + " " + this);
		return Physics.timeUntilCircleCollision(circle, new Circle(this.cartesianCenter, this.radius), this.cartesianVelocity);
	}
	
	/**
	 * Returns a ball that has perfectly elastic collision with a line,
	 * 
	 * @param line with which the ball is colliding
	 * @return A ball that has collided with the line. 
	 */
	public Ball reflectLine(LineSegment line) {
		return new Ball(this.boardCenter, convertVelocity(Physics.reflectWall(line, this.cartesianVelocity)), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a line segment accounting for the lines coefficient of reflection
	 * @param line line with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the line
	 * @return A ball which has collided with the line
	 */
	public Ball reflectLine(LineSegment line, Double reflectionCoeff) {
		return new Ball(this.boardCenter, convertVelocity(Physics.reflectWall(line, this.cartesianVelocity, reflectionCoeff)), this.radius);
	}
	
	/**
	 * Returns a ball that has perfect elastic collision with a circle
	 * @param circle circle with which the ball is colliding
	 * @return A ball that has collided with the circle
	 */
	public Ball reflectCircle(Circle circle) {
		return new Ball(this.boardCenter, convertVelocity(Physics.reflectCircle(circle.getCenter(), this.cartesianCenter, cartesianVelocity)), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a circle accounting for the lines coefficient of reflection
	 * @param circle circle with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the circle
	 * @return A ball which has collided with the circle
	 */
	public Ball reflectCircle(Circle circle, Double reflectionCoeff) {
		return new Ball(this.boardCenter, convertVelocity(Physics.reflectCircle(circle.getCenter(), this.cartesianCenter, cartesianVelocity, reflectionCoeff)), this.radius);
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Ball && this.sameParts((Ball) that);
	}

	private boolean sameParts(Ball that) {
		return this.boardVelocity == that.boardVelocity &&
				this.cartesianCenter == that.cartesianCenter &&
				this.radius == that.radius;
	}
	
	@Override
	public String toString() {
		return "Ball{center=" + this.boardCenter + ", velocity=" + this.boardVelocity + ", radius=" + this.radius + "}";
	}
	
	@Override
	public int hashCode() {
		//TODO
		throw new RuntimeException("Not yet implemented");
	}
	
//	public static void main(String[] args) {
//        JFrame frame = new JFrame("this is a ball");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        Ball toDraw = new Ball(new Vect(10,10), Vect.ZERO, 1.0);
//        frame.add(new JLabel(new ImageIcon(toDraw.generate(20))));
//        frame.pack();
//        frame.setVisible(true);
//    }
}

