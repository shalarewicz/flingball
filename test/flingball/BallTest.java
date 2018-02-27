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
	
	private final Ball STATIONARY_BALL = new Ball(new Vect(1, 1), Vect.ZERO, 0.5);
	@Test
	public void testMove(){
		//TODO
		Ball start = new Ball(new Vect(0,0), new Vect(Angle.DEG_90, 50), 1.0);
		while (start.getBoardCenter().y() < 20) {
			start = start.move(0.1, 25.0, 0.025, 0.025);
		}
	}
	
	@Test
	public void testgetAnchor() {
		final Ball test = STATIONARY_BALL;
		final Vect expected = new Vect(0.5, 0.5);
		assertEquals(expected, test.getAnchor());
	}
}
