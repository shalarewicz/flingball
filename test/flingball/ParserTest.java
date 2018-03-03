package flingball;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

public class ParserTest {
	
	
	// -ea Test assertions enabled
	@Test(expected=AssertionError.class)
	public void testAssertionsEnabled() {
		assert false;
	}
	
	
	@Test
	public void testParser() {
		File test = new File("src/flingball/sampleBoard.fb");
		try {
			BufferedReader testReader = new BufferedReader(new FileReader(test));
			String result = "";
			String next = testReader.readLine();
			//TODO do this better
			while (next != null) {
				result = result + next + "\n";
				next = testReader.readLine();
			}
			System.out.println("Input: \n" + result);
			final Board board = BoardParser.parse(result);
			System.out.println("The constructed board is " + board);
			testReader.close(); 
			new BoardAnimation(board);
			
		} catch (Exception e) {
			System.out.println(e);
		} 
	}
	
}
