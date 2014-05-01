package ticTacToeBot;

import java.util.List;
import backEnd.Square;

/**
 * TicTacToeABTree.java
 * A Minimax tree with alpha-beta pruning and iterative deepening for searching the game tree.
 * This class is mostly copy-pasta'd from the ABTree I wrote for my chessbot.
 * Obviously I changed a number of methods to make it fit.
 * 
 * @author Kelton Finch
 */
public class TicTacToeABTree {

	private int fixedDepth;
	private Square bestMove;
	
	//The switch for verbose mode, and some vars for keeping track of stats to be printed.
	private boolean verbose;
	private int posEvalCount;
	
	public TicTacToeABTree(){
		verbose = false;
	}
	
	public TicTacToeABTree(boolean verbose){
		this.verbose = verbose;
	}

	/**
	 * Searches to the specified depth starting from the specified board position, and returns the best move found.
	 */
	public Square getBestMoveFixed(BoardFiniteBot b, int depth) {
		posEvalCount = 0;
		bestMove = null;
		
		long beginTime, endTime;
		beginTime = System.nanoTime();
		//TODO: Search clearly slows WAY down when nearing OOM without doing much useful work. Investigate possible fixes.
		try{
			//search is progressively deepened, with the best move from previous iterations searched first.
			for(int i=2; i<=depth; i++){
				if(verbose)
					System.out.println("Searching at depth " + i + "...");
				fixedDepth = i;
				treeSearchRecurse(b,i,-Integer.MAX_VALUE,Integer.MAX_VALUE);
				if(verbose)
					System.out.println("Current best move found: " + bestMove);
			}
		//if we OOM during a tree search, just return our best working solution.
		} catch (OutOfMemoryError e){
			if(verbose)
				System.out.println("Ran out of memory! Returning best working solution.");
		}
		endTime = System.nanoTime();
		
		if(verbose){
			System.out.println("Search took " + ((endTime - beginTime)/1000000000) + " seconds");
			System.out.println("Positions evaluated: " + posEvalCount);
		}
		return bestMove;
	}
	
	/*
	 * Recursive helper for getBestMoveFixed. Performs an alpha-beta pruned minimax tree search.
	 * Optimizes search time via a transposition table.
	 * Modifies bestMove as a side effect.
	 */
	private int treeSearchRecurse(BoardFiniteBot b, int depth, int alpha, int beta){
		posEvalCount++;
		
		//This comes before transposition table stuff because attempting to use 
		//transposition tables at depth 0 results in pretty immediate OOM.
		if(depth == 0)
			return b.evaluate();
		
		List<Square> sl = b.getAllReasonableMoves();
		
		if(sl.isEmpty()) //i.e. board is full, so it's a stalemate
			return 0;
		
		if(b.fiveInARow() != null) //i.e. the guy who just moved won, so this is worst position for current player
			return -Integer.MAX_VALUE;
			
		int curr;
		
		//If this is top call, search current best move first.
		//This produces more sensible behavior when the search must be stopped early due to running out of memory or time.
		//In addition, searching the probable best position first usually results in much more AB pruning.
		//This will cause minor amounts of extra work because no transposition table is implemented here.
		if(fixedDepth == depth && bestMove != null){
			curr = -treeSearchRecurse(b.afterMove(bestMove),depth-1,-beta,-alpha);
			if(curr > alpha)
				alpha = curr;
			//no need to set bestMove here: it already is bestMove.
		}
		
		//Recursively searches all possible moves from this position, looking for the best one.
		for(Square s : sl){
			curr = -treeSearchRecurse(b.afterMove(s),depth-1,-beta,-alpha);  
			if(curr > alpha){
				alpha = curr;
				if(fixedDepth == depth)
					bestMove = s;
			}
			if(alpha >= beta)
				break;
		}
		
		return alpha;
	}

	
}

