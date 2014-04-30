package backEnd;

import java.util.HashSet;
import java.util.Set;

public class BoardFinite {

	public Symbol board[][];
	public Symbol turn;
	public final int xDim;
	public final int yDim;
	
	public BoardFinite(){
		this(30,30);
	}
	
	public BoardFinite(int xDim, int yDim){
		this.xDim = xDim;
		this.yDim = yDim;
		board = new Symbol[xDim][yDim];
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				board[x][y] = Symbol.EMPTY;
			}
		}
		turn = Symbol.X;
	}
	
	public BoardFinite(Symbol[][] board, Symbol turn, int xDim, int yDim){
		this.board = board;
		this.turn = turn;
		this.xDim = xDim;
		this.yDim = yDim;
	}
	
	/**
	 * Adds (or changes) a space on the board.
	 * @param sq The coordinates of the space to be modified.
	 * @param s The new symbol to be added.
	 * @return The old symbol at the space.
	 */
	public Symbol add(Square sq, Symbol s){
		//if(sq.x > xDim || sq.x < 0 || sq.y > yDim || sq.y < 0)
		//	throw new Exception();
		Symbol result = board[sq.x][sq.y];
		board[sq.x][sq.y] = s;
		return result;
	}
	
	/**
	 * Makes a space on the board empty.
	 * @param sq The coordinates of the space to be emptied.
	 * @return The old symbol at the space.
	 */
	public Symbol remove(Square sq){
		Symbol result = board[sq.x][sq.y];
		board[sq.x][sq.y]= Symbol.EMPTY; 
		return result;
	}
	
	/**
	 * Inspects a square.
	 * @param sq The coordinates of the space to be inspected.
	 * @return The symbol at the space.
	 */
	public Symbol symbolAt(Square sq){
		return board[sq.x][sq.y];
	}
	
	/**
	 * Checks is a square is empty.
	 * @param sq The square to be checked
	 * @return True iff the symbol at sq equals Symbol.EMPTY
	 */
	public boolean isEmpty(Square sq){
		return board[sq.x][sq.y] == Symbol.EMPTY; 
	}
	
	/**
	 * Adds the current player's symbol to a square, then changes turn appropriately.
	 * Does not check for move legality.
	 * @param sq The target sqaure for the new symbol
	 */
	public void makeMove(Square sq){
		add(sq,turn);
		if(turn == Symbol.X)
			turn = Symbol.O;
		else
			turn = Symbol.X;
	}
	
	/**
	 * Builds a set of all occupied squares.
	 * @return A set containing the squares of all occupied (non-empty) spaces.
	 */
	public Set<Square> squareSet(){
		HashSet<Square> ss = new HashSet<Square>();
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				if(board[x][y] != Symbol.EMPTY)
					ss.add(new Square(x,y));
			}
			
		}
		return ss;
	}
	
	/**
	 * Searches the board for five in a row.
	 * @return An array of five squares, in order, that represent a five in a row for a player,
	 * 		   or null if there is no five in a row.
	 * 		   If there are multiple five in a rows, does not specify which will be returned.
	 */
	public Square[] fiveInARow(){
		Set<Square> ss = squareSet();
		Square result[] = new Square[5];
		Symbol curr;
		int numInARow;
		for(Square s : ss){ //checks every occupied square
			curr = symbolAt(s);
			for(int dx = -1; dx < 2; dx++){ //checks all 8 directions
				for(int dy = -1; dy < 2; dy++){
					if(dx == 0 && dy == 0) //going nowhere isn't a valid direction!
						continue;
					numInARow = 0;
					for(int x = s.x, y = s.y; x < xDim && x >= 0 && y < yDim && y >= 0; x+=dx, y+=dy){
						if(board[x][y] != curr) //must be consecutive of same symbol!
							break;
						result[numInARow] = new Square(x,y);
						numInARow++;
						if(numInARow == 5)
							return result;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Clears the board of all symbols. (Everything is set to Symbol.EMPTY)
	 */
	public void clear(){
		for(int x=0; x<xDim; x++){
			for(int y=0; y<yDim; y++){
				board[x][y] = Symbol.EMPTY;
			}
		}
	}
	
	public String toString(){
		String result = "";
		if(turn == Symbol.X)
			result += "X to play...\n";
		else
			result += "O to play...\n";
		result += "* ";
		for(int x=0; x<xDim; x++)
			result += (x%10) + " ";
		result += "*\n";
		
		for(int y=0; y<yDim; y++){
			result += (y%10) + " ";
			for(int x=0; x<xDim; x++){
				switch(board[x][y]){
				case X : 	 result += "X "; break;
				case O :	 result += "O "; break;
				case EMPTY : result += ". "; break;
				}
			}
			result += (y%10) + "\n";
		}
		
		result += "* ";
		for(int x=0; x<xDim; x++)
			result += (x%10) + " ";
		result += "*\n";
		
		return result;
	}
	
}
