
package flingball;

import java.awt.image.BufferedImage;

//TODO Create a separate interface for absorber and wall;
// Gadget interface, Bumper, Absorber, Wall interfaces which extend Gadget

/**
 * A Gadget is an object that can be placed on a flingball board or used to construct a flingball board. 
 * 
 * A gadgets position must have coordinates >= 0;
 */
import physics.*;

public interface Gadget {
	
	/**
	 * @return The anchor position of the gadget on the board
	 */
	public Vect position();
	
	/**
	 * 
	 * @return The name of the gadget
	 */
	public String name();
	
	/**
	 * 
	 * @return The height of the object in L units
	 */
	public int height();
	
	/**
	 * 
	 * @return The width of the object in L units
	 */
	public int width();
	
	public int area();
	/**
	 * 
	 * @return The coefficient of reflection of the gadget
	 */
	public double getReflectionCoefficient();
	
	/**
	 * Sets the reflectionCoefficient for a gadget. The default value is 1.0
	 * 
	 * @param sets the reflection coefficient of a gadget. 
	 * 
	 */
	public void setReflectionCoefficient(double x);
	
	/**
	 * 
	 * @param ball A ball in the same 2D space. 
	 * @return the time in seconds until the ball collides with the gadget. Returns POSITIE_INFINITY
	 * if a collision will not occur. 
	 */
	public double collisionTime(Ball ball);
	
	/**
	 * 
	 * @param ball Ball to be reflected
	 * @return a new ball which has reflected off the Gadget
	 * 
	 */
	public void reflectBall(Ball ball);
	
	
	/**
	 * 
	 * @return the name of the gadget which is the trigger for this gadget
	 */
	public String getTrigger();
	
	/**
	 * 
	 * @return A Gadget that has had it's action taken
	 */
	public void takeAction();
	
	/**
	 * Generates an image of the gadget. 
	 * 
	 * @return a BufferedImage representation of the gadget. 
	 */
	public BufferedImage generate(int L);
	
	//TODO exposes the rep of Board this can be handled when Absorber implements it's own interface
	/**
	 * 
	 * @param coverage
	 */
	void setCoverage(int[][] coverage);
		
	public final static String NO_TRIGGER = "NO_TRIGGER";
	public final static Double REFLECTION_COEFFICIENT = 1.0;
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
	
	@Override
	public boolean equals(Object that);
	
	public boolean ballOverlap(Ball ball);

	/**
	 * Fires all balls trapped in the gadget;
	 */
	public void fireAll();
	
	
}
