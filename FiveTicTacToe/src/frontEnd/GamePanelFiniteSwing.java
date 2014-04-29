package frontEnd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import backEnd.BoardFinite;
import backEnd.Square;
import backEnd.Symbol;

public class GamePanelFiniteSwing extends JPanel {

	Dimension size; //The dimensions of the game panel, in pixels
	private int width, height; //the width and height of the game panel, in pixels
	private int boardSize; //the size of the game board, in game squares to a side
	private int boxDim; //the size of each square on the board, in pixels to a side
	private int widthOffset, heightOffset; //offset (in pixels) needed to center the board in the panel
	private static final int pixelMargin = 3; //Difference between size of symbols and size of squares
	
	private BoardFinite b;
	
	private LocalGameFiniteSwing parent;
	private JLabel turnStatus; //A hook to the parent's status bar, allowing it to be updated
	
	public GamePanelFiniteSwing(LocalGameFiniteSwing parent){
		this.parent = parent;
		turnStatus = parent.getTurnStatus();
	}
	
	public void play(){
		
	}
	
	/*
	 * Updates the status bar at the top of the window to reflect whose turn it is.
	 */
	private void updateTurnStatus(){
		if(b.turn == Symbol.X){
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
	
	private void getDimensions(){
		size = getSize();
		width = size.width;
		height = size.height;
		boxDim = Math.min(width, height) / 30;
		//System.out.println(boxDim + "\n" + width + "\n" + height); //TODO: Remove debugging code here
		widthOffset = (width-(boxDim*30))/2;
		heightOffset = (height-(boxDim*30))/2;
		//System.out.println(widthOffset + "\n" + heightOffset); //TODO: Remove debugging code here
	}
	
	private Dimension squareToDim(Square sq){
		int w = widthOffset + (sq.x * boxDim) + (boxDim/2);
		int h = heightOffset + (sq.y * boxDim) + (boxDim/2);
		return new Dimension(w,h);
	}
	
	private Square dimToSquare(Dimension d){
		int x = (d.width-widthOffset)/boxDim;
		int y = (d.height-heightOffset)/boxDim;
		return new Square(x,y);
	}
	
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        getDimensions();

        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1));
        
        for(int i=0; i<=30; i++)
        	g2d.drawLine((i*boxDim)+widthOffset, heightOffset, (i*boxDim)+widthOffset, (30*boxDim)+heightOffset);
        for(int i=0; i<=30; i++)
        	g2d.drawLine(widthOffset, (i*boxDim)+heightOffset, (30*boxDim)+widthOffset, (i*boxDim)+heightOffset);
        
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);
    }
}