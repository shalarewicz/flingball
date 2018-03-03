@skip whitespace {
	
	BOARD ::= boardName '\n'* ((comment '\n'*) | (command '\n'*))* ;
	boardName ::='board name''='NAME (GRAVITY)? (FRICTION1)? (FRICTION2)? '\n';
	comment ::= '#' [A-Za-z0-9\.'=]* '\n';
	command ::= BALL | BUMPER | ABSORBER;
	BALL ::= 'ball name' '=' NAME 'x' '='FLOAT 'y' '='FLOAT 'xVelocity' '=' FLOAT 'yVelocity' '=' FLOAT '\n';
	ABSORBER ::= 'absorber' 'name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER 'width' '=' INTEGER 'height' '=' INTEGER '\n';
	BUMPER ::= triangleBumper | circleBumper | squareBumper;
	squareBumper ::= 'squareBumper name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER '\n';
	circleBumper ::= 'circleBumper name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER '\n';
	triangleBumper ::= 'triangleBumper name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER ('orientation' '=' ORIENTATION)? '\n';
	GRAVITY ::= 'gravity' '='  FLOAT;
	FRICTION1 ::= 'friction1' '='  FLOAT;
	FRICTION2 ::= 'friction2' '='  FLOAT;
	ORIENTATION ::= '0' | '90' | '180' | '270';
}

NAME ::= [A-Za-z_][A-Za-z_0-9]*;
INTEGER ::= [0-9]+;
FLOAT ::= '-'?([0-9]+'.'[0-9]*|'.'?[0-9]+);
whitespace ::= [' '\t\r]+;
