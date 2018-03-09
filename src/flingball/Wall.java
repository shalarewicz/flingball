package flingball;

import java.awt.image.BufferedImage;

import physics.Circle;
import physics.LineSegment;
import physics.Physics;
import physics.Vect;

// Immutable Type representing a wall of a flingball board
public class Wall implements Gadget {
	
	private final double x1, y1, x2, y2;
	
	private final double REFLECTION_COEFFICIENT = 1.0;
	
	private final LineSegment wall;
	private final Circle c1, c2;
	
	private final String name;
	
	public Wall(String name, double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.name = name;
		
		this.wall = new LineSegment(x1, y1, x2, y2);
		this.c1 = new Circle(x1, y1, 0);
		this.c2 = new Circle(x2, y2, 0);
		this.checkRep();
	}
	

	/*
	 * AF(x1, y1, x2, y2, name) = a line segment from (x1, y1) to (x2, y2) with Name name and 
	 * Rep Invariant ::= true
	 * Safety from rep exposure
	 * 	Only final or immutable types are returned. 
	 */
	
	private void checkRep() {
		assert true;
	}
	
	@Override
	public Vect position() {
		final double xAnchor, yAnchor;
		
		if (x1 == x2) {
			xAnchor = x1;
			// line not diagonal
			if (y1 < y2) yAnchor = y2;
			else yAnchor = y1;
			
		}
		else if (y1 == y2) {
			yAnchor = y1;
			if (x1 <  x2) xAnchor = x1;
			else xAnchor = x2;
		}
		else {
			// line is diagonal
			if (x1 < x2 && y1 < y2) {
				xAnchor = x1;
				yAnchor = y2;
			}
			else if (x1 < x2 && y1 > y2) {
				xAnchor = x1;
				yAnchor = y1;
			}
			else if (x1 > x2 && y1 < y2){
				xAnchor = x2;
				yAnchor = y2;
			}
			else {
				xAnchor = x2;
				yAnchor = y1;
			}
		}
		return new Vect((int) xAnchor, (int) yAnchor);
	}
	
	/**
	 * 
	 * @return starting point of the wall
	 */
	Vect start() {
		return new Vect(x1, y1);
	}
	
	/**
	 * 
	 * @return end point of the wall
	 */
	Vect end() {
		return new Vect(x2, y2);
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public int height() {
		return 0;
	}
	
	@Override
	public int width() {
		return (int) Math.sqrt(Physics.distanceSquared(x1, y1, x2, y2));
	}
	
	@Override
	public int area() {
		return 0;
	}
	
	@Override
	public double getReflectionCoefficient() {
		return REFLECTION_COEFFICIENT;
	}
	
	@Override
	public void setReflectionCoefficient(double x) {
		// Do Nothing
	}

	@Override
	public double collisionTime(Ball ball) {
		final double timeToWall = ball.timeUntilLineCollision(this.wall);
		final double timeToc1 = ball.timeUntilCircleCollision(c1);
		final double timeToc2 = ball.timeUntilCircleCollision(c2);
		
		return Math.min(timeToWall, Math.min(timeToc1, timeToc2));
	}

	@Override
	public String getTrigger() {
		return Gadget.NO_TRIGGER;
	}

	@Override
	public BufferedImage generate(int L) {
		return new BufferedImage(0, 0, BufferedImage.TYPE_3BYTE_BGR);
	}

	@Override
	public void reflectBall(Ball ball) {
		final double timeToWall = ball.timeUntilLineCollision(this.wall);
		final double timeToc1 = ball.timeUntilCircleCollision(c1);
		final double timeToc2 = ball.timeUntilCircleCollision(c2);
		
		double collisionTime = Math.min(timeToWall, Math.min(timeToc1, timeToc2));
		
		if (collisionTime == timeToWall) {
			ball.reflectLine(this.wall, this.REFLECTION_COEFFICIENT);	
		}
		else if (collisionTime == timeToc1) {
			ball.reflectCircle(c1, this.REFLECTION_COEFFICIENT);
		}
		else {
			ball.reflectCircle(c2, this.REFLECTION_COEFFICIENT);
			
		}
		
	}
	
	@Override
	public String toString() {
		return "Wall:" + this.name +"[<" + this.x1 + ", " + this.y1 + ">, <"+ this.x2 + ", " + this.y2 +">]";
	}


	@Override
	public boolean ballOverlap(Ball ball) {
		Vect ballCenter = ball.getCartesianCenter();
		Vect perpendicularPoint = Physics.perpendicularPoint(this.wall, ballCenter);
		System.out.println(ballCenter);
		System.out.println(perpendicularPoint);
		return Physics.distanceSquared(ballCenter, perpendicularPoint) >= 0;
	}

	@Override
	public void takeAction() {
		// do nothing
	}
	
	@Override
	public void setCoverage(int[][] coverage) {
		
		// do nothing
	}
	
	@Override
	public void fireAll() {
		// do nothing.
	}
}
