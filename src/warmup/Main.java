package warmup;


import java.util.ArrayList;
import java.util.Arrays;

import physics.LineSegment;
import physics.Physics;

public class Main {

    public static void main(String[] args) {
        // TODO: warmup code here (and in other methods and classes as needed)   
    	
    	Ball ball = new Ball();
    	final Board board = new Board();
    	
    	ArrayList<LineSegment> boardObjects = new ArrayList<LineSegment>(Arrays.asList(board.TOP, board.BOTTOM, board.LEFT, board.RIGHT));
    	System.out.println(boardObjects);
    	
    	for (int i = 0; i < 20; i++) {
    		
    		LineSegment nextWall = board.LEFT;
    		double collisionTime = Double.POSITIVE_INFINITY;
    		
    		for (LineSegment object : boardObjects) {
    			//TODO This only works if ball collides with one object withinn one frame rate.
    			// To fix this can give circles with 0 radius highest priority and place them at all intersections. 
    			final double timeToNextCollision = Physics.timeUntilWallCollision(object, ball.getBall(), ball.getVelocity());
    			if (timeToNextCollision < collisionTime) {
    				nextWall = object;
    				collisionTime = timeToNextCollision;
    			}
    			
    		}
    		
    		if (collisionTime < 1) {
    			//TODO: System.out.println("Time until collision = " + collisionTime + " with " + nextWall);
    			//TODO: System.out.println("Ball going to collide currently at Position= " + ball.getPosition() + ", Velocity = " + ball.getVelocity());
    			ball = ball.move(collisionTime);
    			//TODO: System.out.println("Ball at collision point Position= " + ball.getPosition() + ", Velocity = " + ball.getVelocity());
    			ball.setVelocity(Physics.reflectWall(nextWall, ball.getVelocity()));
    			//TODO: System.out.println("Collision! Position= " + ball.getPosition() + ", Velocity = " + ball.getVelocity());
    			ball = ball.move(1 - collisionTime);
    			//TODO: System.out.println("Ball rebounded to Position= " + ball.getPosition() + ", Velocity = " + ball.getVelocity());
    			continue;
    		}
    		
    		ball = ball.move(1);
    		
    		//TODO: System.out.println("Position= " + ball.getPosition() + ", Velocity = " + ball.getVelocity());
    	}
    }
    
}
