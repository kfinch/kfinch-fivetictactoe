package backEnd;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Kelton Finch
 * A representation of the game state.
 * State is implemented as a sparse array representing a hash map.
 */
public class Board {

	private HashMap<Square,Symbol> board;
	
	public Board(){
		board = new HashMap<Square,Symbol>();
	}
	
	public Board(int initialCapacity){
		board = new HashMap<Square,Symbol>(initialCapacity);
	}
	
	/**
	 * Adds (or changes) a space on the board.
	 * @param sq The coordinates of the space to be modified.
	 * @param s The new symbol to be added.
	 * @return The old symbol at the space.
	 */
	public Symbol add(Square sq, Symbol s){
		Symbol result = board.put(sq, s);
		if(result == null)
			return Symbol.EMPTY;
		return result;
	}
	
	/**
	 * Makes a space on the board empty.
	 * @param sq The coordinates of the space to be emptied.
	 * @return The old symbol at the space.
	 */
	public Symbol remove(Square sq){
		Symbol result = board.remove(sq);
		if(result == null)
			return Symbol.EMPTY;
		return result;
	}
	
	/**
	 * Inspects a space.
	 * @param sq The coordinates of the space to be inspected.
	 * @return The symbol at the space.
	 */
	public Symbol symbolAt(Square sq){
		if(!board.containsKey(sq))
			return Symbol.EMPTY;
		return board.get(sq);
	}
	
	public Set<Square> squareSet(){
		return board.keySet();
	}
}
