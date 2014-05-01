package frontEnd;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import backEnd.BoardFinite;
import backEnd.Square;
import backEnd.Symbol;

public class LocalGameFiniteSwing extends JFrame implements ActionListener {

	private JLabel turnStatus;
	private GamePanelFiniteSwing gamePanel;
	
	public LocalGameFiniteSwing(){
		initUI();
	}
	
	private void initUI(){
		
		//initialize main window
		setTitle("Five in a Row Tic Tac Toe");
		//this is a reasonable default size, any size chosen should be roughly 1:1, with ~90 extra pixels on height
        setSize(750, 840);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
        //initialize the "whose turn is it" bar
		turnStatus = new JLabel();
		turnStatus.setFont(new Font("Sans_Serif", Font.BOLD, 16));
		turnStatus.setHorizontalAlignment(SwingConstants.CENTER);
		turnStatus.setOpaque(true);
		turnStatus.setText("Choose a game mode!"); //TODO: Write something better for starting text..
        add(turnStatus, BorderLayout.NORTH);

        //add button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
        add(buttonPanel, BorderLayout.SOUTH);
        
        //add buttons
        JButton newHotseatGameButton = new JButton("New Hotseat Game");
        JButton newBotGameButton = new JButton("New Bot Game");
        JButton quitButton = new JButton("Quit");
        newHotseatGameButton.addActionListener(this);
        newBotGameButton.addActionListener(this);
        quitButton.addActionListener(this);
        buttonPanel.add(newHotseatGameButton);
        buttonPanel.add(newBotGameButton);
        buttonPanel.add(quitButton);
        
        //add game panel
        gamePanel = new GamePanelFiniteSwing(this);
        gamePanel.setBorder(BorderFactory.createEtchedBorder(Color.black, Color.black));
        gamePanel.setSize(750, 850);
        add(gamePanel, BorderLayout.CENTER);
        //setResizable(false); //TODO: Leave this here? The board handles being resized, it's probably not needed...
	}
	
	public JLabel getTurnStatus(){
		return turnStatus;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("New Hotseat Game")){
			gamePanel.playNewHotseatGame();
		}
		if(command.equals("New Bot Game")){
			gamePanel.playNewBotGame();
		}
		if(command.equals("Quit")){
			System.exit(0);
		}
	}

	public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LocalGameFiniteSwing game = new LocalGameFiniteSwing();
                game.setVisible(true);
            }
        });
    }
	
}
