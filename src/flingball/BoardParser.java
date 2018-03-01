package flingball;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import flingball.TriangleBumper.Orientation;
import physics.Vect;

public class BoardParser {


	public static void main(final String[] args) throws UnableToParseException{
		final String input = "board name= test \n#c";
		//final String input = "board name= test\nball name = test x=1.0 y = 1.0 xVelocity =1.0 yVelocity = 2.0";
		System.out.println(input);
        final Board expression = BoardParser.parse(input);
        System.out.println(expression);
	}
	
	private enum BoardGrammar {
		EXPRESSION, BOARD, COMMENT, COMMAND, BALL, BUMPER, SQUAREBUMPER, CIRCLEBUMPER, 
		TRIANGLEBUMPER, INTEGER, FLOAT, NAME, WHITESPACE, ORIENTATION, FRICTION2, FRICTION1, 
		GRAVITY
	}

	private static Parser<BoardGrammar> parser = makeParser();
	
	private static Parser<BoardGrammar> makeParser() {
		try {
			final File grammarFile = new File("src/flingball/Board.g");
			return Parser.compile(grammarFile, BoardGrammar.BOARD);
		} catch (IOException e) {
			throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }
		
		
	
	public static Board parse(final String input) throws UnableToParseException{
		final ParseTree<BoardGrammar> parseTree = parser.parse(input);
		
		final Board board = makeAbstractSyntaxTree(parseTree);
		return board;
	}
	
	private static Board makeAbstractSyntaxTree(final ParseTree<BoardGrammar> parseTree) {
		switch (parseTree.name()) {
        case EXPRESSION: //  board \n (comment | command)*
            {
            	List<ParseTree<BoardGrammar>> children = parseTree.children();
            	
            	Board board = makeAbstractSyntaxTree(children.get(0));
            	for (int i = 1; i < children.size(); i++) {
            		
            		final ParseTree<BoardGrammar> child = children.get(i);
            		List<ParseTree<BoardGrammar>> grandChildren = child.children();
            		
            		switch (children.get(i).name()) {
            		case COMMENT:
            			continue;
            		
            		case BALL: //BALL ::= 'ball name' '=' NAME 'x' '='FLOAT 'y' '='FLOAT 'xVelocity' '=' FLOAT 'yVelocity' '=' FLOAT;
            		{
            			final String name = grandChildren.get(0).text();
            			final double cx = Double.parseDouble(grandChildren.get(1).text());
            			final double cy = Double.parseDouble(grandChildren.get(2).text());
            			final double vx = Double.parseDouble(grandChildren.get(3).text());
            			final double vy = Double.parseDouble(grandChildren.get(4).text());
            			final Ball ball = new Ball(name, new Vect(cx, cy), new Vect(vx, vy));
            			board.addBall(ball);
            			continue;
            		}
            		
            		case BUMPER: {
            			switch (child.name()) {
            			case SQUAREBUMPER: // name=NAME x=INTEGER y=INTEGER
            	        {
            	        	String name = grandChildren.get(0).text();
            	        	final int x = Integer.parseInt(grandChildren.get(1).text());
            	        	final int y = Integer.parseInt(grandChildren.get(2).text());
            	        	
            	        	Gadget bumper = new SquareBumper(name, x, y);
            	        	board = board.addGadget(bumper);
            	        	continue;
            	        }
            			 case CIRCLEBUMPER: // name=NAME x=INTEGER y=INTEGER
            			 {
             	        	String name = grandChildren.get(0).text();
             	        	final int x = Integer.parseInt(grandChildren.get(1).text());
             	        	final int y = Integer.parseInt(grandChildren.get(2).text());
             	        	
             	        	Gadget bumper = new CircleBumper(name, x, y);
             	        	board = board.addGadget(bumper);
             	        	continue;
             	        }
            		     case TRIANGLEBUMPER: // ame=NAME x=INTEGER y=INTEGER (orientation=0|90|180|270)?
            		     {
             	        	Gadget bumper;
             	        	String name = grandChildren.get(0).text();
             	        	final int x = Integer.parseInt(grandChildren.get(1).text());
             	        	final int y = Integer.parseInt(grandChildren.get(2).text());
             	        	if (grandChildren.size() > 2) {
             	        		Orientation o = Orientation.ZERO;
             	        		switch (grandChildren.get(3).text()) {
             	        		case "0": o = Orientation.ZERO;
             	        		case "90": o = Orientation.NINETY;
             	        		case "180": o = Orientation.ONEEIGHTY;
             	        		case "270": o = Orientation.TWOSEVENTY;
             	        		}
             	        		bumper = new TriangleBumper(name, x, y, o);
             	        	}
             	        	else {            	        	
             	        		bumper = new TriangleBumper(name, x, y, Orientation.ZERO);
             	        	}
             	        	board = board.addGadget(bumper);
             	        	continue;
             	        }
						default:
							throw new RuntimeException("Should never get here");
            			}
            		}
					default:
						throw new RuntimeException("Should never get here");
            		}// End Switch
            	} // End for lope
            	
            	return board;
            }
        case BOARD: //board name = NAME (gravity=FLOAT)? (friction1=FLOAT)? (friction2=FLOAT)?
        {
        	List<ParseTree<BoardGrammar>> children = parseTree.children();
        	double gravity = Board.GRAVITY;
        	double friction1 = Board.FRICTION_1;
        	double friction2 = Board.FRICTION_2;
        	String name = children.get(0).text();
        	System.out.println(children);
        	if (children.size() > 1) {
	        	for (int i = 1; i < children.size(); i++) {
	        		ParseTree<BoardGrammar> child = children.get(i);
	        		switch (child.name()) {
	        		case GRAVITY:{
	        			gravity = Double.parseDouble(child.text());
	        			continue;
	        		}
	        		case FRICTION1:{
	        			friction1 = Double.parseDouble(child.text());
	        			continue;
	        		}
	        		case FRICTION2:{
	        			friction2 = Double.parseDouble(child.text());
	        			continue;
	        		}
	        		default:
	        			makeAbstractSyntaxTree(child);
	    		}
	        	}
        	}
        	
        	return new Board(name, gravity, friction1, friction2);
        	
        	//TOOD - handle case for gravity and friction values specified
        	
        }
                	
		default:
			throw new RuntimeException("Should never get here");
		}
	}



}
