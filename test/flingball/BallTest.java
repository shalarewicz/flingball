package flingball;

import static org.junit.Assert.*;

import org.junit.Test;

import physics.Angle;
import physics.Vect;

public class BallTest {
	
	@Test(expected=AssertionError.class)
	public void testAssertionsEnabled() {
		assert false;
	}
	
	// TODO - Partitions
	// 	radius n
	// 	velocity vector - polar | Cartesian shouldn't neet to test as this is guaranteed by Physics
	// 	position vector - polar | Cartesian
	//  vector addition - guaranteed by physics? but i wrote my own reflect methods
	
	
	// TODO - Testing Strategy
	// reflections. by testing ball = ball.reflectwall(wall).reflect(wall)
	
	@Test
	public void testMove(){
		Ball start = new Ball(new Vect(0,0), new Vect(Angle.DEG_90, 50), 1.0);
		while (start.getPosition().y() < 20) {
			start = start.move(0.1, 25.0, 0.025, 0.025);
		}
	}
}
