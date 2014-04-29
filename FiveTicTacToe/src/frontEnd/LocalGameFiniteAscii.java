package frontEnd;

import java.util.Scanner;

import backEnd.BoardFinite;
import backEnd.Symbol;
import backEnd.Square;

public class LocalGameFiniteAscii {
	
	/**
	 * Runs a local game of Five in a Row Tic Tac Toe,
	 * with input from System.in and output on System.out
	 * @return The symbol of the winning player.
	 */
	public static Symbol runGame(){
		return runGame(new BoardFinite());
	}
	
	/**
	 * Runs a local game of Five in a Row Tic Tac Toe from a specific starting state.
	 * @param gameState - The starting game state.
	 * @return The symbol of the winning player.
	 */
	public static Symbol runGame(BoardFinite gameState){
		Scanner input = new Scanner(System.in);
		//String order;
		//Scanner interpreter;
		int x,y;
		Square fiveInARow[] = null;
		Symbol winner;
		while(fiveInARow == null){
			System.out.println(gameState);
			System.out.println("Enter your move: ");
			while(true){
				//order = input.next();
				//interpreter = new Scanner(order);
				x = input.nextInt();
				y = input.nextInt();
				if(gameState.symbolAt(new Square(x,y)) == Symbol.EMPTY){
					gameState.makeMove(new Square(x,y));
					break;
				}
				System.out.println("Invalid move, please try again...");
			}
			fiveInARow = gameState.fiveInARow();	
		}
		System.out.println(gameState);
		winner = gameState.symbolAt(fiveInARow[0]);
		if(winner == Symbol.X)
			System.out.println("X Wins!");
		else
			System.out.println("O Wins!");
		return gameState.symbolAt(fiveInARow[0]);
	}
	
}
