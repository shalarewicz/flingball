package warmup;

import physics.LineSegment;

public class Board {
	
	public final int HEIGHT = 20;
	public final int WIDTH = 20;
	
	public final LineSegment TOP = new LineSegment(0., 6., 0, 10);
	public final LineSegment BOTTOM = new LineSegment(0., 10., 0., 20.);
	public final LineSegment LEFT = new LineSegment(0., 0., 0., 20.);
	public final LineSegment RIGHT = new LineSegment(20., 0., 20., 20.);
	
}
