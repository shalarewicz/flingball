/**
 * Author:
 * Date:
 */
package flingball;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import physics.*;

/**
 * An immutable class representing a ball for use on a flingball board. All balls must have radius > 0;
 */ 
public class BallC {
	
	private int x, boardY, cartY, vx, boardVY, cartVY, anchorX, anchorY;
	private final int doubleToInt = 1000;
	// default diameter is 0.5L
	private int radius = (int) (0.25 * doubleToInt);
	private String name;
	private boolean trapped = false;
	
	//Used to translate doubles to ints to avoid floating point errors in math
	
	
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
		assert Math.sqrt(vx^2 + boardVY^2) <= 200;
		assert Math.sqrt(vx^2 + boardVY^2) >= 0;
	}
	
	/**
	 * Creates a new Ball for use on a flingBall board
	 * @param name name of the ball
	 * @param center center of the ball
	 * @param velocity velocity of the ball
	 */
	public BallC(String name, Vect center, Vect velocity) {
		this.name = name;
		this.x = (int) Math.round(center.x()*doubleToInt);
		this.boardY = (int) Math.round(center.y()*doubleToInt);
		this.cartY = -this.boardY;
		
		this.anchorX = this.x - this.radius;
		this.anchorY = this.boardY - this.radius;
		
		this.vx= (int) Math.round(velocity.x() * doubleToInt);
		this.boardVY = (int) Math.round(velocity.y() * doubleToInt);
		this.cartVY = -this.boardVY;
		
		checkRep();
	}
	
	/**
	 * Creates a new Ball for use on a flingBall board
	 * @param name name of the ball
	 * @param center center of the ball
	 * @param velocity velocity of the ball
	 * @param radius radius of the ball. Must be greater than zero
	 */
	public BallC(String name, Vect center, Vect velocity, double radius) {
		this.name = name;
		this.radius = (int) radius * doubleToInt;
		
		this.x = (int) Math.round(center.x()*doubleToInt);
		this.boardY = (int) Math.round(center.y()*doubleToInt);
		this.cartY = -this.boardY;
		
		this.anchorX = this.x - this.radius;
		this.anchorY = this.boardY - this.radius;
		
		this.vx= (int) Math.round(velocity.x() * doubleToInt);
		this.boardVY = (int) Math.round(velocity.y() * doubleToInt);
		this.cartVY = -this.boardVY;
		
		checkRep();
	}
	
	//TODO Deprecate
//	/**
//	 * Given the coordinate of the center of a ball on a flingball board. Returns the center in Cartesian space.
//	 * 
//	 * @param center center of the ball on a flingabll board
//	 * @return center of the ball in Cartesian Space. 
//	 */
//	private static Vect getCartesianCenterFromBoardCenter(Vect boardCenter) {
//		return new Vect(boardCenter.x(), -boardCenter.y());
//	}
//	
//	private static Vect getBoardCenterFromCartesianCenter(Vect cartesianCenter) {
//		return new Vect(cartesianCenter.x(), -cartesianCenter.y());
//	}
	
//	/**
//	 * Computes the anchor of the bounding box of the ball. The anchor is the top left corner of the bounding box.
//	 * @return anchor of the ball on a flingBall board. 
//	 */
//	private Vect getAnchorFromCartesianCenter(Vect cartesianCenter) {
//		return new Vect(cartesianCenter.x() - this.radius, -cartesianCenter.y() - this.radius);
//	}
//	
//	private Vect getAnchorFromBoardCenter(Vect boardCenter) {
//		return new Vect(boardCenter.x() - this.radius, boardCenter.y() - this.radius);
//	}
	
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
		this.x= this.x + this.vx * (int) (Math.round(time * doubleToInt));
		this.boardY = this.boardY + this.boardVY * (int) (Math.round(time * doubleToInt));
		this.cartY = this.cartY + this.cartVY * (int) (Math.round(time * doubleToInt));
		
		this.anchorX = this.x - this.radius;
		this.anchorY = this.boardY - this.radius;
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
	public void move(double time, double gravity, double mu, double mu2) {
		// let a = acceleration
		// displacement = position + velocity * t + at^2
		// delta_v = at
		// v_new_f = v_old * (1 - mu*delta_t - mu2*|v_old|*delta_t) 
		
		final int TIME = (int) Math.round(time * doubleToInt);
		
		int vx_initial = this.vx;
		int vy_initial = this.boardVY;
		
		//Change in velocity due to gravity = gravity * time
		final int deltaVGravity = (int) Math.round(-gravity * time * doubleToInt);
		
		// Final velocity as a result of friction
		final int vxFinalFriction = vx_initial * ( (int) Math.round((1 - mu*time - mu2 * time) * doubleToInt) * Math.abs(vx_initial));
		final int vyFinalFriction = vy_initial * ( (int) Math.round((1 - mu*time - mu2 * time) * doubleToInt) * Math.abs(vy_initial));
		
		// Net change in vy = vfFriction + deltaVGravity
		this.vx = vxFinalFriction;
		this.cartVY = vyFinalFriction + deltaVGravity;
		this.boardVY = -this.cartVY;
		
		//delta v
		final int delta_vx = vxFinalFriction - vx_initial;
		final int delta_vy = this.cartVY - vy_initial;
		
		
		// displacement ::= dx = (vi + vf)/2 * t Constant acceleration is assumed for sufficiently smmall time
		this.x = this.x + (vx_initial + delta_vx) / 2 * TIME;
		this.cartY = this. cartY + (vy_initial + delta_vy) / 2 * TIME;
		this.boardY = -this.cartY;
		
		this.anchorX = this.x - this.radius;
		this.anchorY = this.boardY - this.radius;
		
	}
	
	/**
	 * 
	 * @return The position of the origin (top left) of the bounding box of the ball
	 */
	Vect getAnchor() {
		return new Vect(this.anchorX / doubleToInt, this.anchorY / this.doubleToInt);
	}
	
	/**
	 * 
	 * @return The position of the center of the ball on a flingball board.
	 */
	public Vect getBoardCenter() {
		return new Vect(this.x / doubleToInt, this.boardY / this.doubleToInt);
	}
	
	/**
	 * 
	 * @return The position of the center of the ball in Cartesian Space.
	 */
	Vect getCartesianCenter() {
		return new Vect(this.x / doubleToInt, this.cartY / this.doubleToInt);
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
		return new Vect(this.vx / doubleToInt, this.boardVY / this.doubleToInt);
	}
	
	/**
	 * 
	 * @param v The new velocity of the ball
	 * @return A ball with velocity v
	 */
	public void setVelocity(Vect v) {
		this.vx = (int) Math.round(v.x() * doubleToInt);
		this.boardVY = (int) Math.round(v.y() * doubleToInt);
		this.cartVY = -this.boardVY;
	}
	
	/**
	 * @param L the dimension of the board 20L x 20L pixels wide
	 * @return An image of the ball
	 */
	public BufferedImage generate(int L) {
		final int diameter = (int) (2*radius*L / doubleToInt);
		BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) output.getGraphics();
        
        graphics.setColor(Color.BLUE); //TODO Allow users to set color
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
		return Physics.timeUntilWallCollision(line,
				new Circle(new Vect (this.x / doubleToInt, this.cartY / doubleToInt), this.radius / doubleToInt), 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt));
	}
	
	/**
	 * Calculates the time in seconds until the ball may collide with a circle. If no collision will occur Double.POSITIVE_INFINITY is 
	 * returned. 
	 * 
	 * @param circle circle with which the ball may collide
	 * @return Collision time in seconds or POSITIVE_INFINITY if no collision will occur
	 */
	public double timeUntilCircleCollision(Circle circle) {
		return Physics.timeUntilCircleCollision(circle, 
				new Circle(new Vect (this.x / doubleToInt, this.cartY / doubleToInt), this.radius / doubleToInt), 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt));
	}
	
	/**
	 * Returns a ball that has perfectly elastic collision with a line,
	 * 
	 * @param line with which the ball is colliding
	 * @return A ball that has collided with the line. 
	 */
	public void reflectLine(LineSegment line) {
		Vect newVelocity = Physics.reflectWall(line, new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt));
		
		this.vx = (int) Math.round(newVelocity.x() * doubleToInt);
		this.cartVY = (int) Math.round(newVelocity.y() * doubleToInt);
		this.boardVY = -this.cartVY;
		checkRep();
		//return new Ball(this.name, this.boardCenter, convertVelocity(Physics.reflectWall(line, this.cartesianVelocity)), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a line segment accounting for the lines coefficient of reflection
	 * @param line line with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the line
	 * @return A ball which has collided with the line
	 */
	public void reflectLine(LineSegment line, Double reflectionCoeff) {
		Vect newVelocity  = Physics.reflectWall(line, 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt), 
				reflectionCoeff);
		
		this.vx = (int) Math.round(newVelocity.x() * doubleToInt);
		this.cartVY = (int) Math.round(newVelocity.y() * doubleToInt);
		this.boardVY = -this.cartVY;
		checkRep();
		//return new Ball(this.name, this.boardCenter, convertVelocity(Physics.reflectWall(line, this.cartesianVelocity, reflectionCoeff)), this.radius);
	}
	
	/**
	 * Returns a ball that has perfect elastic collision with a circle
	 * @param circle circle with which the ball is colliding
	 * @return A ball that has collided with the circle
	 */
	public void reflectCircle(Circle circle) {
		Vect newVelocity = Physics.reflectCircle(circle.getCenter(), 
				new Vect (this.x / doubleToInt, this.cartY / doubleToInt), 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt));
		
		this.vx = (int) Math.round(newVelocity.x() * doubleToInt);
		this.cartVY = (int) Math.round(newVelocity.y() * doubleToInt);
		this.boardVY = -this.cartVY;
		checkRep();
		//return new Ball(this.name, this.boardCenter, convertVelocity(Physics.reflectCircle(circle.getCenter(), this.cartesianCenter, cartesianVelocity)), this.radius);
	}
	
	/**
	 * Returns a ball that has collided with a circle accounting for the lines coefficient of reflection
	 * @param circle circle with which the ball collides
	 * @param reflectionCoeff - Coefficient of reflection for the circle
	 * @return A ball which has collided with the circle
	 */
	public void reflectCircle(Circle circle, Double reflectionCoeff) {
		Vect newVelocity  = Physics.reflectCircle(circle.getCenter(), 
				new Vect (this.x / doubleToInt, this.cartY / doubleToInt), 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt), 
				reflectionCoeff);
		
		this.vx = (int) Math.round(newVelocity.x() * doubleToInt);
		this.cartVY = (int) Math.round(newVelocity.y() * doubleToInt);
		this.boardVY = -this.cartVY;
		checkRep();	
		//return new Ball(this.name, this.boardCenter, convertVelocity(Physics.reflectCircle(circle.getCenter(), this.cartesianCenter, cartesianVelocity, reflectionCoeff)), this.radius);
	}
	
	public void reflectRotatingCircle(Circle circle, double angularVelocity, double reflectionCoeff) {
		Vect newVelocity = Physics.reflectRotatingCircle(
				circle, circle.getCenter(), angularVelocity, 
				new Circle(new Vect (this.x / doubleToInt, this.cartY / doubleToInt), this.radius), 
				new Vect (this.vx / doubleToInt, this.cartVY / doubleToInt), 
				reflectionCoeff);
		
		this.vx = (int) Math.round(newVelocity.x() * doubleToInt);
		this.cartVY = (int) Math.round(newVelocity.y() * doubleToInt);
		this.boardVY = -this.cartVY;
		checkRep();
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof BallC && this.sameParts((BallC) that);
	}

	private boolean sameParts(BallC that) {
		return this.boardVY == that.boardVY &&
				this.vx == that.vx &&
				this.x == that.x &&
				this.boardY == that.boardY &&
				this.radius == that.radius &&
				this.name == that.name;
	}
	
	@Override
	public String toString() {
		return "Ball{name=" + this.name + ", center=(" + this.x + ", " + this.boardY + "), velocity=(" + this.vx + ", " + this.boardVY + "), radius=" + this.radius + "}";
	}
	
	@Override
	public int hashCode() {
		//TODO
		return name.hashCode();
	}

	public String name() {
		return this.name;
	}

	public boolean isTrapped() {
		return this.trapped ;
	}
	
	public void trap() {
		this.trapped = true;
	}
	
	public void release() {
		this.trapped = false;
	}
	
	
	// TODO was ballInside - deprecate
	public boolean insideGadget(Gadget g) {
		final double x = this.x;
		final double y = this.boardY;
		final double gX = g.position().x();
		final double gY = g.position().y();
		
		return x - radius > gX && x + radius < gX + g.width() && 
				y - radius > gY && y + radius < gY + g.height();
			
	}

	public void setPosition(Vect vect) {
		this.x = (int) Math.round(vect.x() * doubleToInt);
		this.boardY = (int) Math.round(vect.y() * doubleToInt);
		this.cartY = - this.boardVY;
		
		this.anchorX = this.x - this.radius;
		this.anchorY = this.boardY - this.radius;
		checkRep();
	}

}

