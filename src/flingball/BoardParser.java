package flingball;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import edu.mit.eecs.parserlib.ParseTree;
import edu.mit.eecs.parserlib.Parser;
import edu.mit.eecs.parserlib.UnableToParseException;
import flingball.TriangleBumper.Orientation;
import physics.Vect;
// import edu.mit.eecs.parserlib.Visualizer;

public class BoardParser {


	public static void main(final String[] args) throws IOException{
		String test = "boards/sampleBoard.fb";
		try {
			Path filePath = Paths.get(test);
		//	Path filePath = Paths.get("boards/sampleBoard.fb");
			Stream<String> fileIn = Files.lines(filePath);
			StringBuilder boardFile = new StringBuilder();
			fileIn.forEach(s -> boardFile.append(s+"\n"));
			fileIn.close();
			System.out.println("Input: \n" + boardFile);
			final Board board = BoardParser.parse(boardFile.toString());
			System.out.println("The constructed board is " + board);
			new BoardAnimation(board);
		} catch (IOException e) {
			System.out.println(test + " not found");
		} catch (UnableToParseException e) {
			System.out.println("Unable to parse " + test);
		}
			
	}
	
	private enum BoardGrammar {
		BOARD, COMMENT, COMMAND, BALL, BUMPER, SQUAREBUMPER, CIRCLEBUMPER, 
		TRIANGLEBUMPER, INTEGER, FLOAT, NAME, WHITESPACE, ORIENTATION, FRICTION2, FRICTION1, 
		GRAVITY, BOARDNAME, ABSORBER
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
		
		// display the parse tree in a web browser, for debugging only
		//Visualizer.showInBrowser(parseTree);

        // make an AST from the parse tree
		final Board board = makeAbstractSyntaxTree(parseTree);
		return board;
	}
	
	private static Board makeAbstractSyntaxTree(final ParseTree<BoardGrammar> parseTree) {
		switch (parseTree.name()) {
        case BOARD: //  board \n (comment | command)*
            {
            	List<ParseTree<BoardGrammar>> children = parseTree.children();
            	
            	Board board = makeAbstractSyntaxTree(children.get(0));
            	for (int i = 1; i < children.size(); i++) {
            		
            		final ParseTree<BoardGrammar> child = children.get(i);
            		List<ParseTree<BoardGrammar>> grandChildren = child.children();
            		
            		switch (children.get(i).name()) {
            		case COMMENT:
            			continue;
            		
            		case COMMAND:{
            			
            			ParseTree<BoardGrammar> grandChild = grandChildren.get(0);
            			List<ParseTree<BoardGrammar>> greatGrandChildren = grandChild.children();
            			switch (grandChild.name()) {
            			
            			case BALL: //BALL ::= 'ball name' '=' NAME 'x' '='FLOAT 'y' '='FLOAT 'xVelocity' '=' FLOAT 'yVelocity' '=' FLOAT;
            			{
            				
            				final String name = greatGrandChildren.get(0).text();
            				final double cx = Double.parseDouble(greatGrandChildren.get(1).text());
            				final double cy = Double.parseDouble(greatGrandChildren.get(2).text());
            				final double vx = Double.parseDouble(greatGrandChildren.get(3).text());
            				final double vy = Double.parseDouble(greatGrandChildren.get(4).text());
            				final Ball ball = new Ball(name, new Vect(cx, cy), new Vect(vx, vy));
            				board.addBall(ball);
            				continue;
            			}
            			
            			case BUMPER: {
            				
            				ParseTree<BoardGrammar> greatGrandChild = greatGrandChildren.get(0);
            				List<ParseTree<BoardGrammar>> bumperProperties = greatGrandChild.children();
            				switch (greatGrandChild.name()) {
            				case SQUAREBUMPER: // name=NAME x=INTEGER y=INTEGER
            				{
            					String name = bumperProperties.get(0).text();
            					final int x = Integer.parseInt(bumperProperties.get(1).text());
            					final int y = Integer.parseInt(bumperProperties.get(2).text());
            					
            					Gadget bumper = new SquareBumper(name, x, y);
            					board = board.addGadget(bumper);
            					continue;
            				}
            				case CIRCLEBUMPER: // name=NAME x=INTEGER y=INTEGER
            				{
            					String name = bumperProperties.get(0).text();
            					final int x = Integer.parseInt(bumperProperties.get(1).text());
            					final int y = Integer.parseInt(bumperProperties.get(2).text());
            					
            					Gadget bumper = new CircleBumper(name, x, y);
            					board = board.addGadget(bumper);
            					continue;
            				}
            				case TRIANGLEBUMPER: // name=NAME x=INTEGER y=INTEGER (orientation=0|90|180|270)?
            				{
            					Gadget bumper;
            					String name = bumperProperties.get(0).text();
            					final int x = Integer.parseInt(bumperProperties.get(1).text());
            					final int y = Integer.parseInt(bumperProperties.get(2).text());
            					if (bumperProperties.size() > 2) {
            						Orientation o = Orientation.ZERO;
            						switch (bumperProperties.get(bumperProperties.size()-1).text()) {
            						case "0": o = Orientation.ZERO; break;
            						case "90": o = Orientation.NINETY; break;
            						case "180": o = Orientation.ONEEIGHTY; break;
            						case "270": o = Orientation.TWOSEVENTY; break;
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
            					System.out.println("Could not match BUMPER " + greatGrandChild.name());
            					throw new RuntimeException("Should never get here");
            				}
            			}
            			case ABSORBER:{
            				String name = greatGrandChildren.get(0).text();
            				int x = Integer.parseInt(greatGrandChildren.get(1).text());
            				int y = Integer.parseInt(greatGrandChildren.get(2).text());
            				int width = Integer.parseInt(greatGrandChildren.get(3).text());
            				int height = Integer.parseInt(greatGrandChildren.get(4).text());
            				board = board.addGadget(new Absorber(name, x, y, width, height));
            				continue;
            			}
            			default:
            				System.out.println("Could not match COMMAND: " + grandChild.name());
        					throw new RuntimeException("Should never get here");
        				}
            			}
            		default:
        				System.out.println("Could not match " + child.name());
    					throw new RuntimeException("Should never get here");
            		}// End Switch
            	} // End for loop
            	
            	return board;
            }
        case BOARDNAME: //board name = NAME (gravity=FLOAT)? (friction1=FLOAT)? (friction2=FLOAT)?
        {
        	List<ParseTree<BoardGrammar>> children = parseTree.children();
        	double gravity = Board.GRAVITY;
        	double friction1 = Board.FRICTION_1;
        	double friction2 = Board.FRICTION_2;
        	String name = children.get(0).text();
        	if (children.size() > 1) {
	        	for (int i = 1; i < children.size(); i++) {
	        		ParseTree<BoardGrammar> child = children.get(i);
	        		switch (child.name()) {
	        		case GRAVITY:{
	        			gravity = Double.parseDouble(child.children().get(0).text());
	        			continue;
	        		}
	        		case FRICTION1:{
	        			friction1 = Double.parseDouble(child.children().get(0).text());
	        			continue;
	        		}
	        		case FRICTION2:{
	        			friction2 = Double.parseDouble(child.children().get(0).text());
	        			continue;
	        		}
	        		default:
	        			makeAbstractSyntaxTree(child);
	    		}
	        	}
        	}
        	return new Board(name, gravity, friction1, friction2);
        }
                	
		default:
			System.out.println("Could not match " + parseTree.name());
			throw new RuntimeException("Should never get here");
		}
	}



}
