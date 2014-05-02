package frontEnd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ticTacToeBot.BoardFiniteBot;
import ticTacToeBot.TicTacToeABTree;
import backEnd.Square;
import backEnd.Symbol;

public class GamePanelFiniteSwing extends JPanel implements ActionListener {

	private BoardFiniteBot gameBoard; //the back end's game board!
	private Square lastMove; //keeps track of the most recent move
	
	private TicTacToeABTree searchTree; //a minimax search tree used by the bot
	private static final int searchDepth = 4; //depth bot searches to while looking for move.
	
	private boolean isStarted; //true iff there is a game on
	private boolean isBotGame; //true iff the current game is against the bot
	private Symbol botsTurn; //which player the bot is.
	
	Dimension size; //The dimensions of the game panel, in pixels
	private int width, height; //the width and height of the game panel, in pixels
	private int boardXSize; //the size of the game board, in game squares to the x side
	private int boardYSize; //the size of the game board, in game squares to the y side
	private int boxDim; //the size of each square on the board, in pixels to a side
	private int widthOffset, heightOffset; //offset (in pixels) needed to center the board in the panel
	private static final int pixelMargin = 3; //Difference between size of symbols and size of squares
	
	private LocalGameFiniteSwing parent;
	private JLabel turnStatus; //A hook to the parent's status bar, allowing it to be updated
	private InputAdapter input; //for reading mouse input
	
	public GamePanelFiniteSwing(LocalGameFiniteSwing parent){
		this.parent = parent;
		turnStatus = parent.getTurnStatus();
		input = new InputAdapter();
		addMouseListener(input);
		
		gameBoard = new BoardFiniteBot();
		lastMove = null;
		
		searchTree = new TicTacToeABTree(true);//TODO: Verbose mode on for debugging
		isStarted = false;
		botsTurn = Symbol.O; //for now, Bot only plays as O
	}
	
	/**
	 * Clears the board and starts a new hotseat game of five-in-a-row tic-tac-toe.
	 */
	public void playNewHotseatGame(){
		gameBoard.startNewGame();
		updateTurnStatus();
		isStarted = true;
		isBotGame = false;
		repaint();
	}
	
	/**
	 * Clears the board and starts a new game vs. the bot of five-in-a-row tic-tac-toe.
	 */
	public void playNewBotGame(){
		gameBoard.startNewGame();
		updateTurnStatus();
		isStarted = true;
		isBotGame = true;
		repaint();
	}
	
	/**
	 * Bot calculates best move and then acts on it
	 */
	//Currently this method pegs the thread, preventing other user options from being chosen while bot is calculating
	//TODO: add multithreading, so I can shunt this method off to another thread
	public void botMove(){
		System.out.println("Bot is thinking!"); //TODO: Remove debugging code
		//doesn't give bot the actual gameBoard reference so the master copy can't be messed with
		BoardFiniteBot carbonCopy = new BoardFiniteBot(gameBoard);
		Square move = searchTree.getBestMoveFixed(carbonCopy, searchDepth);
		
		if(!gameBoard.isEmpty(move)){ //checks to make sure bot returned a legal move!
			System.out.println("Bot tried an illegal move!"); //TODO: Remove debugging code
			return;
		}
			
		gameBoard.makeMove(move);
		lastMove = move; //update most recent move
		updateTurnStatus();
		System.out.println("Bot moved!"); //TODO: Remove debugging code
		repaint();
	}
	
	/*
	 * Updates the status bar at the top of the window to reflect whose turn it is.
	 */
	private void updateTurnStatus(){
		if(gameBoard.turn() == Symbol.X){
			turnStatus.setText("<html><font color='red'>X to play</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.red, Color.red));
			turnStatus.setBackground(new Color(1.0f,0.7f,0.7f));
		}
		else{
			turnStatus.setText("<html><font color='blue'>O to play</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.blue, Color.blue));
			turnStatus.setBackground(new Color(0.7f,0.7f,1.0f));
		}
	}
	
	private void updateDimensions(){
		size = getSize();
		width = size.width;
		height = size.height;
		boardXSize = gameBoard.xDim;
		boardYSize = gameBoard.yDim;
		boxDim = Math.min(width/boardXSize, height/boardYSize);
		widthOffset = (width-(boxDim*boardXSize))/2;
		heightOffset = (height-(boxDim*boardYSize))/2;
	}
	
	/*
	 * Helper method takes a square on the board and returns the pixel coordinates of the top left corner of that square.
	 * If Window dimensions are out of date, this method will return incorrect dimensions. Call updateDimensions() first!
	 */
	private Dimension squareToDim(Square sq){
		int w = widthOffset + (sq.x * boxDim);
		int h = heightOffset + (sq.y * boxDim);
		return new Dimension(w,h);
	}
	
	/*
	 * Helper method takes pixel dimensions and returns the square that pixel is a part of. Returns null if outside board.
	 * If Window dimensions are out of date, this method will return incorrect dimensions. Call updateDimensions() first!
	 */
	private Square dimToSquare(Dimension d){
		int x = (d.width-widthOffset)/boxDim;
		int y = (d.height-heightOffset)/boxDim;
		if(x >= boardXSize || x < 0 || y >= boardYSize || y < 0) //TODO: will this be handled on the side of BoardFinite?
			return null;
		return new Square(x,y);
	}
	
	/*
	 * Given an array representing a five in a row, highlights those boxes, prints a victory message, and ends the game.
	 */
	private void handleVictory(Square fiveInARow[], Graphics2D g2d){
		Symbol winner = gameBoard.symbolAt(fiveInARow[0]);
		Dimension loc;
		switch(winner){
		case X :
			for(int i=0; i<5; i++){
				loc = squareToDim(fiveInARow[i]);
				g2d.setColor(new Color(1.0f,0.7f,0.7f));
				g2d.fillRect(loc.width, loc.height, boxDim, boxDim);
				//drawSymbol(fiveInARow[i], g2d);
			}
			if(isBotGame && winner == botsTurn)
				turnStatus.setText("<html><font color='red'>BOT WINS!</font></html>");
			else if(isBotGame && winner != botsTurn)
				turnStatus.setText("<html><font color='red'>HUMAN WINS!</font></html>");
			else
				turnStatus.setText("<html><font color='red'>X WINS!</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.red, Color.red));
			turnStatus.setBackground(new Color(1.0f,0.7f,0.7f));
			break;
		case O :
			for(int i=0; i<5; i++){
				loc = squareToDim(fiveInARow[i]);
				g2d.setColor(new Color(0.7f,0.7f,1.0f));
				g2d.fillRect(loc.width, loc.height, boxDim, boxDim);
				//drawSymbol(fiveInARow[i], g2d);
			}
			if(isBotGame && winner == botsTurn)
				turnStatus.setText("<html><font color='blue'>BOT WINS!</font></html>");
			else if(isBotGame && winner != botsTurn)
				turnStatus.setText("<html><font color='blue'>HUMAN WINS!</font></html>");
			else
				turnStatus.setText("<html><font color='blue'>O WINS!</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.blue, Color.blue));
			turnStatus.setBackground(new Color(0.7f,0.7f,1.0f));
			break;
		}
		isStarted = false;
	}
	
	private void drawLastMove(Graphics2D g2d){
		if(lastMove == null) //if no one has moved yet, don't need to draw anything
			return;
		Dimension loc = squareToDim(lastMove);
		switch(gameBoard.symbolAt(lastMove)){
		case X : 
			g2d.setColor(new Color(1.0f,0.7f,0.7f));
			g2d.fillRect(loc.width, loc.height, boxDim, boxDim);
			break;
		case O : 
			g2d.setColor(new Color(0.7f,0.7f,1.0f));
			g2d.fillRect(loc.width, loc.height, boxDim, boxDim);
			break;
		}
	}
	
	private void drawSymbol(Square sq, Graphics2D g2d){
		Dimension loc = squareToDim(sq);
		int x = loc.width;
		int y = loc.height;
		int symbolSize = boxDim - (pixelMargin*2);
		
		switch(gameBoard.symbolAt(sq)){
		case X : 
			g2d.setColor(Color.red);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawLine(x+pixelMargin, y+pixelMargin, x+symbolSize+pixelMargin, y+symbolSize+pixelMargin);
			g2d.drawLine(x+pixelMargin, y+symbolSize+pixelMargin, x+symbolSize+pixelMargin, y+pixelMargin);
			break;
		case O :
			g2d.setColor(Color.blue);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawOval(x+pixelMargin, y+pixelMargin, symbolSize, symbolSize);
		case EMPTY : break;
		}
	}
	
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        updateDimensions(); //ensures window dimensions are up to date

        drawLastMove(g2d);
        
        Square fiveInARow[] = gameBoard.fiveInARow();
        if(fiveInARow != null)
        	handleVictory(fiveInARow, g2d);
        
        //draws the board grid
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1));
        for(int i=0; i<=boardXSize; i++)
        	g2d.drawLine((i*boxDim)+widthOffset, heightOffset, (i*boxDim)+widthOffset, (boardYSize*boxDim)+heightOffset);
        for(int i=0; i<=boardXSize; i++)
        	g2d.drawLine(widthOffset, (i*boxDim)+heightOffset, (boardXSize*boxDim)+widthOffset, (i*boxDim)+heightOffset);
        
        //draws the symbols on the board
        Set<Square> squareSet = gameBoard.squareSet();
        for(Square s : squareSet)
        	drawSymbol(s,g2d);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);
    }

	@Override
	public void actionPerformed(ActionEvent e) {}

	class InputAdapter extends MouseAdapter{
	
		@Override
		public void mouseClicked(MouseEvent e){
			if(!isStarted || (isBotGame && gameBoard.turn() == botsTurn)) //can't make moves before game starts or during bot's turn
				return;
			
			Dimension loc = new Dimension(e.getX(),e.getY());
			Square move = dimToSquare(loc);
			
			if(move == null) //if click wasn't on board, do nothing
				return;
			
			if(!gameBoard.isEmpty(move)) //is square is occupied, then not a legal move
				return;
				
			gameBoard.makeMove(move);
			lastMove = move; //update most recent move
			updateTurnStatus();
			repaint();
			paintImmediately(0, 0, width, height); //force the repaint before the bot move pegs the thread.
			
			//if we're playing against a bot, and this move didn't just end the game, tell the bot to move.
			if(isStarted && isBotGame)
				botMove();
		}
	}
	
}