package warmup;

import physics.Angle;
import physics.Circle;
import physics.Vect;


public class Ball {
	
	
	// fields
	private Vect velocity;
	private Vect position;

	private Circle ball;

	// Constructors
	
	public Ball() {
		this.position = new Vect(0., 0.);
		this.velocity = new Vect(Angle.DEG_90, 5.);
		this.ball = new Circle(this.position, 1.);
	}
	
	public Ball(Vect position, Vect velocity, double radius) {
		this.position = position;
		this.velocity = velocity;
		this.ball = new Circle(position, radius);
	}
	
	public Ball move(double time) {
		// ToDo support movement in multiple directions. 
		double newX = this.position.x() + this.velocity.toPoint2D().getX() * time;
		double newY = this.position.y() + this.velocity.toPoint2D().getY() * time;
		Vect newPosition = new Vect(newX, newY);
		return new Ball(newPosition, this.velocity, this.ball.getRadius());
	}
	
	public double getX() {
		return this.position.x();
	}
	
	public double getY() {
		return this.position.y();
	}
	
	public Vect getVelocity() {
		//TODO - rep exposure but this class should be mutable? what about part 2 with thread safety?
		return this.velocity;
	}
	
	public Vect getPosition() {
		return this.position;
	}
	
	public double getSpeed() {
		return this.velocity.length();
	}
	
	public Angle getDirection() {
		return this.velocity.angle();
	}
	
	public Circle getBall() {
		return this.ball;
	}
	
	public void setX(double x) {
		this.position = new Vect(x, this.position.y());
	}
	
	public void setY(double y) {
		this.position = new Vect(this.position.x(), y);
	}
	
	public void setSpeed(double speed) {
		this.velocity = new Vect(this.velocity.angle(), speed);
	}
	
	public void setDirection(Angle direction) {
		this.velocity = new Vect(direction, this.velocity.length());
	}

	public void setVelocity(Vect velocity) {
		this.velocity = velocity;
	}
	
}
