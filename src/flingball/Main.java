package flingball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


import edu.mit.eecs.parserlib.UnableToParseException;

public class Main {
	
	
	public static void main(String[] args) throws IOException{
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");
			final String input = in.readLine();
			
			if (input.isEmpty()) {
				System.exit(0); // exits the program
			}
			try {
				Path filePath = Paths.get(input);
			//	Path filePath = Paths.get("boards/sampleBoard.fb");
				Stream<String> fileIn = Files.lines(filePath);
				StringBuilder boardFile = new StringBuilder();
				fileIn.forEach(s -> boardFile.append(s + "\n"));
				fileIn.close();
				System.out.println("Input: \n" + boardFile);
				final Board board = BoardParser.parse(boardFile.toString());
				System.out.println("The constructed board is " + board);
				new BoardAnimation(board);
			} catch (IOException e) {
				System.out.println(input + " not found");
				continue;
			} catch (UnableToParseException e) {
				System.out.println("Unable to parse " + input);
				continue;
			}
		}
	}
}
