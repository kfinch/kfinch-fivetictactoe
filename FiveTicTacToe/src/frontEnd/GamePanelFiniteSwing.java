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

import backEnd.BoardFinite;
import backEnd.Square;
import backEnd.Symbol;

public class GamePanelFiniteSwing extends JPanel implements ActionListener {

	BoardFinite gameBoard;
	private boolean isStarted;
	
	Dimension size; //The dimensions of the game panel, in pixels
	private int width, height; //the width and height of the game panel, in pixels
	private int boardXSize; //the size of the game board, in game squares to the x side
	private int boardYSize; //the size of the game board, in game squares to the y side
	private int boxDim; //the size of each square on the board, in pixels to a side
	private int widthOffset, heightOffset; //offset (in pixels) needed to center the board in the panel
	private static final int pixelMargin = 3; //Difference between size of symbols and size of squares
	
	private LocalGameFiniteSwing parent;
	private JLabel turnStatus; //A hook to the parent's status bar, allowing it to be updated
	private InputAdapter input;
	
	public GamePanelFiniteSwing(LocalGameFiniteSwing parent){
		this.parent = parent;
		turnStatus = parent.getTurnStatus();
		input = new InputAdapter();
		addMouseListener(input);
		
		gameBoard = new BoardFinite();
		isStarted = false;
	}
	
	public void playNewGame(){
		gameBoard.clear();
		gameBoard.turn = Symbol.X;
		updateTurnStatus();
		isStarted = true;
		repaint();
	}
	
	/*
	 * Updates the status bar at the top of the window to reflect whose turn it is.
	 */
	private void updateTurnStatus(){
		if(gameBoard.turn == Symbol.X){
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
		//System.out.println(boxDim + "\n" + width + "\n" + height); //TODO: Remove debugging code here
		widthOffset = (width-(boxDim*boardXSize))/2;
		heightOffset = (height-(boxDim*boardYSize))/2;
		//System.out.println(widthOffset + "\n" + heightOffset); //TODO: Remove debugging code here
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
				drawSymbol(fiveInARow[i], g2d);
			}
			turnStatus.setText("<html><font color='red'>X WINS!</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.red, Color.red));
			turnStatus.setBackground(new Color(1.0f,0.7f,0.7f));
			break;
		case O :
			for(int i=0; i<5; i++){
				loc = squareToDim(fiveInARow[i]);
				g2d.setColor(new Color(0.7f,0.7f,1.0f));
				g2d.fillRect(loc.width, loc.height, boxDim, boxDim);
				drawSymbol(fiveInARow[i], g2d);
			}
			turnStatus.setText("<html><font color='blue'>O WINS!</font></html>");
			turnStatus.setBorder(BorderFactory.createEtchedBorder(Color.blue, Color.blue));
			turnStatus.setBackground(new Color(0.7f,0.7f,1.0f));
			break;
		}
		isStarted = false;
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
        
        Square fiveInARow[] = gameBoard.fiveInARow();
        if(fiveInARow != null)
        	handleVictory(fiveInARow, g2d);
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
			if(!isStarted) //can't make moves before game starts
				return;
			
			Dimension loc = new Dimension(e.getX(),e.getY());
			Square move = dimToSquare(loc);
			
			if(move == null) //if click wasn't on board, do nothing
				return;
			
			if(!gameBoard.isEmpty(move)) //is square is occupied, then not a legal move
				return;
				
			gameBoard.makeMove(move);
			updateTurnStatus();
			repaint();
		}
	}
	
}