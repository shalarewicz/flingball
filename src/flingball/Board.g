@skip whitespace {
	expression ::= BOARD;
	BOARD ::= 'board name''='NAME '\n' ('gravity' '=' GRAVITY)? ('friction1' '=' FRICTION1)? ('friction2' '=' FRICTION2)? (comment |command)*;
	comment ::= '#'.*;
	command ::= (BALL | bumper) '\n';
	BALL ::= 'ball name' '=' NAME 'x' '='FLOAT 'y' '='FLOAT 'xVelocity' '=' FLOAT 'yVelocity' '=' FLOAT;
	bumper ::= (triangleBumper | circleBumper | squareBumper) '\n';
	squareBumper ::= 'name='NAME 'x' '=' INTEGER 'y' '=' INTEGER;
	circleBumper ::= 'name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER;
	triangleBumper ::= 'name' '=' NAME 'x' '=' INTEGER 'y' '=' INTEGER ORIENTATION;
	GRAVITY ::= 'gravity' '='  FLOAT;
	FRICTION1 ::= 'friction1' '='  FLOAT;
	FRICTION2 ::= 'friction2' '='  FLOAT;
	ORIENTATION ::= 'orientation' '='  ('0' | '90' | '180' | '270')?;
}

NAME ::= [A-Za-z_][A-Za-z_0-9]*;
INTEGER ::= [0-9]+;
FLOAT ::= '-'?([0-9]+'.'[0-9]*|'.'?[0-9]+);
whitespace ::= [ \t\r]+;
