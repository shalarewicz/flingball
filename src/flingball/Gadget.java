
package flingball;

import java.awt.image.BufferedImage;
import java.io.InvalidObjectException;

/**
 * A Gadget is an object that can be placed on a flingball board or used to construct a flingball board. 
 * 
 * A gadgets position must have coordinates >= 0;
 */
import physics.*;

public interface Gadget {
	
	/**
	 * @return The anchor position of the gadget as a vector <x, y>
	 */
	public Vect position();
	
	/**
	 * 
	 * @return The name of the gadget
	 */
	public String name();
	
	/**
	 * 
	 * @return The coefficient of reflection of the gadget
	 */
	public double getReflectionCoefficient();
	
	/**
	 * Sets the reflectionCoefficient for a gadget. The default value is 1.0
	 * 
	 * @param sets the relection coefficient of a gadget. 
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
	 * This method is used to determine which gadget a ball should reflect off given equal collision times. 
	 * 
	 * @return The collision priority of an object
	 */
	public int priority();
	
	
	/**
	 * Sets a trigger for the gadget
	 */
	public void setTrigger();
	
	/**
	 * 
	 * @param ball Ball to be reflected
	 * @return a new ball which has reflected off the Gadget
	 * 
	 * TODO: Account for reversed coordinates. in Cartesian flip y coordinate
	 */
	public Ball reflectBall(Ball ball);
	
	
	/**
	 * 
	 * @return the name of the gadget which is the trigger for this gadget
	 */
	public String getTrigger();
	
	
	/**
	 * Sets an action for this gadget. 
	 * 
	 * @throws InvalidObjectException if gadget does not have a trigger;
	 */
	public void setAction() throws InvalidObjectException;
	
	/**
	 * TODO Shoudl this return an action or take the action?
	 */
	public void action();
	
	
	/**
	 * Generates an image of the gadget. 
	 * 
	 * @return a BufferedImage representation of the gadget. 
	 */
	public BufferedImage generate(int L);
	
	public final static String NO_TRIGGER = "NO_TRIGGER";
	public final static Double REFLECTION_COEFFICIENT = 1.0;
	
	
	
}
