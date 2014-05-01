package ticTacToeBot;

import java.util.ArrayList;
import java.util.List;

import backEnd.BoardFinite;
import backEnd.Square;
import backEnd.Symbol;

/**
 * An extension of BoardFinite with additional methods for use by an AI player of the game.
 * 
 * This class includes a static evaluator function, and it's current implementation is janky as fuck.
 * It works by looking at how many streak in a row each player has, and evaluating based on how long
 * those streak are and how much open space they have around them. This could cause some weird evals,
 * but it's the best I have for now. The evaluation function can easily be changed if I think of something better.
 * 
 * @author Kelton Finch
 */
public class BoardFiniteBot extends BoardFinite {

	//I have to redeclare these for some reason t_t
	public final int xDim;
	public final int yDim;
	
	private boolean hasChecked[][];
	
	private static final int DIRECTIONS[] = {0,1,1,1,1,0,1,-1}; //used by evaluator helper
	
	//this is the value of a given number of same symbols in a row with a given amount of open space around it
	//the first index is number of open spaces (0 to 4) and the second index is number in a row minus one (1 to 5)
	//TODO: Refine these numbers. What I have here is a guess as to what's good.
	private static final int ROW_SCORES[][] = {{0, 0,  0,   0,    1000000},
										   	   {0, 0,  0,   120,  1000000},
										   	   {0, 0,  60,  1000, 1000000},
										   	   {0, 10, 150, 1000, 1000000},
										   	   {0, 40, 250, 1000, 1000000}};
	
	
	//I have to repeat initialization of xDim and yDim on all these constructors. Not entirely sure why.
	
	/**
	 * Initializes as a copy of the supplied board.
	 */
	public BoardFiniteBot(BoardFiniteBot b){
		this.xDim = b.xDim;
		this.yDim = b.yDim;
		this.board = new Symbol[xDim][yDim];
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				this.board[x][y] = b.board[x][y];
			}
		}
		this.turn = b.turn;
		hasChecked = new boolean[xDim][yDim];
	}
	
	public BoardFiniteBot(){
		super();
		this.xDim = super.xDim;
		this.yDim = super.yDim;
		hasChecked = new boolean[xDim][yDim];
	}
	
	public BoardFiniteBot(int xDim, int yDim){
		super(xDim,yDim);
		this.xDim = super.xDim;
		this.yDim = super.yDim;
		hasChecked = new boolean[xDim][yDim];
	}
	
	public BoardFiniteBot(Symbol[][] board, Symbol turn, int xDim, int yDim){
		super(board,turn,xDim,yDim);
		this.xDim = super.xDim;
		this.yDim = super.yDim;
		hasChecked = new boolean[xDim][yDim];
	}
	
	/**
	 * Evaluates the strength of the board position for the current player.
	 * Normally an evaluation function for minimax search is supposed to evaluate only static position,
	 * for example a material advantage in Chess. Unfortunately, there is no such thing as a material advantage
	 * in Tic Tac Toe, so this function has to look at 2, 3, and 4 in a rows to provide an estimate.
	 * @return an integer evaluation of the strength of the current position.
	 * 		   Positive is better for the current player.
	 */
	public int evaluate(){
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				hasChecked[x][y] = false;
			}
		}
		
		int result = 0;
		Symbol s;
		//This function could be made simpler but slower by use of squareSet()
		//Unfortunately, speed matters for a minimax evaluator function, so I'll be rewriting a lot here.
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				if(hasChecked[x][y])
					continue;
				s = board[x][y];
				if(s == Symbol.EMPTY)
					continue;
				else if(s == turn)
					result += evaluateSquare(x,y);
				else
					result -= evaluateSquare(x,y);
			}
		}
		
		return result;
	}
	
	/*
	 * Helper for evaluate() 
	 */
	private int evaluateSquare(int x, int y){
		int result = 0;
		hasChecked[x][y] = true;
		Symbol s = board[x][y];
		Symbol curr;
		int sameInARow;
		int openSpaces;
		boolean onOpenSpace;
		int dx,dy;
		
		//cycle through the 4 directions represented by the 'directions' array.
		//the other 4 cardinal directions handled because this loop looks at 'lines' (both directions).
		for(int i=0; i<DIRECTIONS.length; i+=2){ 
			sameInARow = 1;
			openSpaces = 0;
			onOpenSpace = false;
			dx = DIRECTIONS[i];
			dy = DIRECTIONS[i+1];
			for(int ex = x+dx, ey = y+dy; ex>=0 && ex<xDim && ey>=0 && ey<yDim; ex+=dx, ey+=dy){
				curr = board[ex][ey];
				if(onOpenSpace && (curr == Symbol.EMPTY || curr == s)){ //counts same symbol after a gap as open for evaluation
					openSpaces++;
					break;
				}
				
				if(curr == Symbol.EMPTY){
					openSpaces++;
					onOpenSpace = true;
				}
				else if(curr == s){
					sameInARow++;
					hasChecked[ex][ey] = true;
				}
				else{ //ran into opposite color symbol
					break;
				}
			}
			onOpenSpace = false;
			//here's the repeat in the opposite direction
			for(int ex = x-dx, ey = y-dy; ex>=0 && ex<xDim && ey>=0 && ey<yDim; ex-=dx, ey-=dy){
				curr = board[ex][ey];
				if(onOpenSpace && (curr == Symbol.EMPTY || curr == s)){ //counts same symbol after a gap as open for evaluation
					openSpaces++;
					break;
				}
				
				if(curr == Symbol.EMPTY){
					openSpaces++;
					onOpenSpace = true;
				}
				else if(curr == s){
					sameInARow++;
					hasChecked[ex][ey] = true;
				}
				else{ //ran into opposite color symbol
					break;
				}
			}
			result += ROW_SCORES[openSpaces][sameInARow-1];
		}
		
		return result;
	}
	
	public BoardFiniteBot afterMove(Square move){
		BoardFiniteBot result = new BoardFiniteBot(this);
		result.makeMove(move);
		return result;
	}
	
	/**
	 * Provides a list of all "reasonable" squares to use as the current player's move,
	 * which is to say all empty squares within a line of length two of an already existing symbol.
	 * If the board is empty, instead gives a list containing only a square at/near the center of the board.
	 * 
	 * @return A list of Squares representing legal moves near the current area of play. 
	 */
	public List<Square> getAllReasonableMoves(){
		List<Square> result = new ArrayList<Square>(50); //TODO: Check if this is reasonable starting size
		
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				hasChecked[x][y] = false;
			}
		}
		
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				if(board[x][y] != Symbol.EMPTY){
					getReasonableMovesFromSquare(x,y,result);
				}
			}
		}
		
		return result;
	}
	
	/*
	 * Helper method for getAllReasonableMoves()
	 * adds to l as a side effect
	 */
	private void getReasonableMovesFromSquare(int x, int y, List<Square> l){
		int ex,ey;
		//check all directions
		for(int dx=-1; dx<2; dx++){
			for(int dy=-1; dy<2; dy++){
				if(dx == 0 && dy == 0) //nowhere isn't a direction!
					continue;
				ex = x+dx;
				ey = y+dy;
				//can't move off the board or on an occupied square
				if(ex<0 || ex>=xDim || ey<0 || ey>=yDim || board[ex][ey] != Symbol.EMPTY)
					continue;
				if(!hasChecked[ex][ey]){
					l.add(new Square(ex,ey));
					hasChecked[ex][ey] = true;
				}
				//and check one more in the same direction. Sorry about the repeated code )=
				ex+=dx;
				ey+=dy;
				//can't move off the board or on an occupied square
				if(ex<0 || ex>=xDim || ey<0 || ey>=yDim || board[ex][ey] != Symbol.EMPTY)
					continue;
				if(!hasChecked[ex][ey]){
					l.add(new Square(ex,ey));
					hasChecked[ex][ey] = true;
				}
			}
		}
	}
	
}
