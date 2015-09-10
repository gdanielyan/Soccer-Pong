import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.applet.*;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.io.File;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.JPanel;

/*
 * Author: Georgiy Danielyan
 */

public class PongBoard extends JPanel implements ActionListener, KeyListener {

	Thread delayThread;
	Timer ballTimer;
	Image ball, goalieLeft, goalieRightOne, goalieRightTwo, goalieRightThree,
			goalieRightFour, goalieRightFive;
	static Image field;
	
	//AudioFile
	File audioFile = new File("kick.wav");
	File cheerFile = new File("cheer.wav");
	File booFile = new File("boo.wav");
	URI kick = audioFile.toURI();
	URI cheer = cheerFile.toURI();
	URI boo = booFile.toURI();
	AudioClip kickSound;
	AudioClip cheerSound;
	AudioClip booSound;
	
	// features to implement
	boolean homeDisplay = true;
	boolean start = false;

	private int score = 0;
	private int lives = 3;

	// Starting coordinates of the ball
	// The playing field is adjustable
	private int diameterOfBall = 15;
	private int xCoordinateOfBall = 225;
	private int yCoordinateOfBall = 240;

	private int xSpeedOfBall = 5;
	private int ySpeedOfBall = 2;

	boolean moveUp = false;
	boolean moveDown = false;
	boolean moveRight = false;
	boolean moveLeft = false;

	// Coordinates of left goalie box
	private int yTopLeftGoalieBox = 65;
	private int yBottomLeftGoalieBox = 395;

	private int yTopOfLeftGoalPost = 150;
	private int yBottomOfLeftGoalPost = 300;

	// Coordinate of our right side goalie box
	private int yTopOfRightGoalieBox = 65;
	private int yBottomOfRightGoalieBox = 380;

	// Coordinates of right goal post
	private int yTopOfRightGoalPost = 150;
	private int yBottomOfRightGoalPost = 300;
	private int goalNetOfRight = 670;

	// Coordinates of our two goalies on the right side
	private int xOfGoalieRightOne = 450;
	private int yOfGoalieRightOne = 180;

	private int xOfGoalieRightTwo = 500;
	private int yOfGoalieRightTwo = 230;

	private int xOfGoalieRightThree = 550;
	private int yOfGoalieRightThree = 280;

	private int xOfGoalieRightFour = 600;
	private int yOfGoalieRightFour = 265;
	
	private int xOfGoalieRightFive = 650;
	private int yOfGoalieRightFive = 265;

	// The speed of which goalies will oscillate
	private int ySpeedOfRightGoalieOne = -3;
	private int ySpeedOfRightGoalieTwo = -2;
	private int ySpeedOfRightGoalieThree = -3;
	private int ySpeedOfRightGoalieFour = 2;
	private int ySpeedOfRightGoalieFive = 1;

	// Left goalie that we will be controlling.
	private int xOfLeftGoalie = 15;
	private int yOfLeftGoalie = 225;

	private int speedOfLeftGoalie = 5;
	boolean ballBehindLeftGoalie = false;
	
	//false is left
	//true is right
	boolean ballLeftOrRight;


	public PongBoard() {
		setFocusable(true);
		addKeyListener(this);
		//addMouseMotionListener(ls);
		try {
			ball = ImageIO.read(new File("ball.png"));
			field = ImageIO.read(new File("field.png"));
			goalieLeft = ImageIO.read(new File("goalieLeft.png"));
			goalieRightOne = ImageIO.read(new File("goalieRight.png"));
			goalieRightTwo = ImageIO.read(new File("goalieRight.png"));
			goalieRightThree = ImageIO.read(new File("goalieRight.png"));
			goalieRightFour = ImageIO.read(new File("goalieRight.png"));
			goalieRightFive = ImageIO.read(new File("goalieRight.png"));
			kickSound = Applet.newAudioClip(kick.toURL());
			cheerSound = Applet.newAudioClip(cheer.toURL());
			booSound = Applet.newAudioClip(boo.toURL());
		} catch (Exception ex) {
			System.out.println(ex);
		}
		ballTimer = new Timer(1000 / 50, this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);

		if (homeDisplay) {
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
			g.setColor(Color.BLACK);
			g.drawString("Welcome to Soccer Pong", 150, 100);
			g.drawString("Press 'Space' to play", 155, 150);
			g.drawString("Use Arrow Keys For Movement", 115, 200);
			g.drawString("Good Luck :)", 225, 300);
			g.drawImage(ball, 250, 350, null);
			g.drawImage(ball, 300, 350, null);
			g.drawImage(ball, 350, 350, null);

		} else if (start) {
			// drawing the field image
			g.drawImage(field, 0, 0, null);

			// drawing the ball
			g.drawImage(ball, xCoordinateOfBall, yCoordinateOfBall, null);

			// drawing our two right goalies
			g.drawImage(goalieRightOne, xOfGoalieRightOne, yOfGoalieRightOne, null);
			g.drawImage(goalieRightTwo, xOfGoalieRightTwo, yOfGoalieRightTwo, null);
			g.drawImage(goalieRightThree, xOfGoalieRightThree, yOfGoalieRightThree, null);
			g.drawImage(goalieRightFour, xOfGoalieRightFour, yOfGoalieRightFour, null);
			g.drawImage(goalieRightFive, xOfGoalieRightFive, yOfGoalieRightFive, null);
			// draw our left goalie
			g.drawImage(goalieLeft, xOfLeftGoalie, yOfLeftGoalie, null);
		}

	}

	// Action listener that fires for each timer 'tick'
	public void actionPerformed(ActionEvent event) {

		moveBall();
		checkDeflectionOrScore();
		moveLeftGoalie();
		moveRightGoalies();

	}

	private void moveBall() {

		int nextBallTop = yCoordinateOfBall + ySpeedOfBall;
		int nextBallBottom = yCoordinateOfBall + diameterOfBall + ySpeedOfBall;
		int nextBallRight = xCoordinateOfBall + xSpeedOfBall + diameterOfBall;
		int nextBallLeft = xCoordinateOfBall + xSpeedOfBall;

		xCoordinateOfBall += xSpeedOfBall;
		yCoordinateOfBall += ySpeedOfBall;

		if (nextBallLeft < 0 || nextBallRight > field.getWidth(null)) {
			xSpeedOfBall *= -1;
		}
		else if (nextBallTop < 0 || nextBallBottom > field.getHeight(null)) {
			ySpeedOfBall *= -1;
		}

		repaint();
	}

	// Move our goalie
	private void moveLeftGoalie() {

		if (moveUp && yOfLeftGoalie + speedOfLeftGoalie > yTopLeftGoalieBox) {
			yOfLeftGoalie -= speedOfLeftGoalie;
		} 
		else if (moveDown && yOfLeftGoalie + goalieLeft.getHeight(null) + speedOfLeftGoalie < yBottomLeftGoalieBox) {
			yOfLeftGoalie += speedOfLeftGoalie;
		} 
		else if (moveRight && xOfLeftGoalie + goalieLeft.getWidth(null) + speedOfLeftGoalie < 135) {
			xOfLeftGoalie += speedOfLeftGoalie;
		} 
		else if (moveLeft && xOfLeftGoalie + speedOfLeftGoalie > 5) {
			xOfLeftGoalie -= speedOfLeftGoalie;
		}

		repaint();
	}

	// Oscillate our two goalies
	public void moveRightGoalies() {
		yOfGoalieRightOne += ySpeedOfRightGoalieOne;
		yOfGoalieRightTwo += ySpeedOfRightGoalieTwo;
		yOfGoalieRightThree += ySpeedOfRightGoalieThree;
		yOfGoalieRightFour += ySpeedOfRightGoalieFour;
		yOfGoalieRightFive += ySpeedOfRightGoalieFive;

		if (yOfGoalieRightOne + ySpeedOfRightGoalieOne < yTopOfRightGoalPost - 50
				|| yOfGoalieRightOne + ySpeedOfRightGoalieOne > yBottomOfRightGoalPost + 50) {
			ySpeedOfRightGoalieOne *= -1;
		}
		if (yOfGoalieRightTwo + ySpeedOfRightGoalieTwo < yTopOfRightGoalPost - 20
				|| yOfGoalieRightTwo + ySpeedOfRightGoalieTwo > yBottomOfRightGoalPost + 20) {
			ySpeedOfRightGoalieTwo *= -1;
		}
		if (yOfGoalieRightThree + ySpeedOfRightGoalieThree < yTopOfRightGoalPost
				|| yOfGoalieRightThree + ySpeedOfRightGoalieThree > yBottomOfRightGoalPost - 20) {
			ySpeedOfRightGoalieThree *= -1;
		}
		if (yOfGoalieRightFour + ySpeedOfRightGoalieFour < yTopOfRightGoalPost
				|| yOfGoalieRightFour + ySpeedOfRightGoalieFour > yBottomOfRightGoalPost - 20) {
			ySpeedOfRightGoalieFour *= -1;
		}
		if (yOfGoalieRightFive + ySpeedOfRightGoalieFive < yTopOfRightGoalPost
				|| yOfGoalieRightFive + ySpeedOfRightGoalieFive > yBottomOfRightGoalPost - 20) {
			ySpeedOfRightGoalieFive *= -1;
		}

		repaint();
	}

	public void checkDeflectionOrScore() {

		int nextBallTop = yCoordinateOfBall + ySpeedOfBall;
		int nextBallBottom = yCoordinateOfBall + diameterOfBall + ySpeedOfBall;
		int nextBallRight = xCoordinateOfBall + xSpeedOfBall + diameterOfBall;
		int nextBallLeft = xCoordinateOfBall + xSpeedOfBall;
		
		// Error checking so the balls dont bounce back and fourth between the left goalie!
		if (xCoordinateOfBall + diameterOfBall < xOfLeftGoalie)
			ballBehindLeftGoalie = true;
		else if (xCoordinateOfBall > xOfLeftGoalie)
			ballBehindLeftGoalie = false;

		if (nextBallLeft < xOfLeftGoalie && !ballBehindLeftGoalie) {
			if (nextBallBottom >= yOfLeftGoalie
					&& nextBallTop <= yOfLeftGoalie + goalieLeft.getHeight(null)) {
				kickSound.play();
				if(yOfLeftGoalie < 150){
					if(ySpeedOfBall <= 0){
						if(ySpeedOfBall == 0){
							ySpeedOfBall += 1;
						}
						ySpeedOfBall *= -1;
					}
				}
				else if(yOfLeftGoalie > 300){
					if(ySpeedOfBall >= 0){
						if(ySpeedOfBall == 0){
							ySpeedOfBall += 1;
						}
						ySpeedOfBall *= -1;						
					}
				}
				else{
					if(ySpeedOfBall == 0){
						ySpeedOfBall += 1;
					}
					else{
						ySpeedOfBall = 0;
						ySpeedOfBall *= -1;													
					}
				}
				xSpeedOfBall *= -1;
				ballLeftOrRight = true;
			}
		}

		// Now check to see if you got scored on
		if (nextBallTop > yTopOfLeftGoalPost
				&& nextBallBottom < yBottomOfLeftGoalPost) {

			if (xCoordinateOfBall < 0) {
				System.out.println("You got Scored on!");
				booSound.play();
				xSpeedOfBall *= -1;
				ySpeedOfBall *= -1;
				xCoordinateOfBall = 225;
				yCoordinateOfBall = 225;
			}
		}
		

		if(nextBallRight > xOfGoalieRightOne && xSpeedOfBall > 0){	
			if(nextBallBottom > yOfGoalieRightOne && nextBallTop < yOfGoalieRightOne + goalieRightOne.getHeight(null)
					&& xCoordinateOfBall < xOfGoalieRightOne){
				xSpeedOfBall *= -1;
				ySpeedOfBall = 0;
				kickSound.play();
				System.out.println("One deflected it!");
			}
		}
		
		if(nextBallRight > xOfGoalieRightTwo && xSpeedOfBall > 0){
			if(nextBallBottom > yOfGoalieRightTwo  && nextBallTop < yOfGoalieRightTwo + goalieRightTwo.getHeight(null)
					&& xCoordinateOfBall < xOfGoalieRightTwo){
				xSpeedOfBall *= -1;
				kickSound.play();
				System.out.println("Two deflected it!");
			}
		}
		
		if(nextBallRight > xOfGoalieRightThree && xSpeedOfBall > 0){
			if(nextBallBottom > yOfGoalieRightThree && nextBallTop < yOfGoalieRightThree + goalieRightThree.getHeight(null)
					&& xCoordinateOfBall < xOfGoalieRightThree){
				xSpeedOfBall *= -1;
				kickSound.play();
				System.out.println("Three deflected it!");
			}
		}
		
		if(nextBallRight > xOfGoalieRightFour && xSpeedOfBall > 0){
			if(nextBallBottom > yOfGoalieRightFour  && nextBallTop < yOfGoalieRightFour + goalieRightFour.getHeight(null)
					&& xCoordinateOfBall < xOfGoalieRightFour){
				xSpeedOfBall *= -1;
				kickSound.play();
				System.out.println("Four deflected it!");
			}
		}
		
		if(nextBallRight > xOfGoalieRightFive && xSpeedOfBall > 0){
			if(nextBallBottom > yOfGoalieRightFive  && nextBallTop < yOfGoalieRightFive + goalieRightFive.getHeight(null)
					&& xCoordinateOfBall < xOfGoalieRightFive){
				xSpeedOfBall *= -1;
				kickSound.play();
				System.out.println("Five deflected it!");
			}
		}

		// now check if you scored between the goal lines!
		if (nextBallTop > yTopOfRightGoalPost
				&& nextBallBottom < yBottomOfRightGoalPost) {
			if (xCoordinateOfBall + diameterOfBall > goalNetOfRight) {
				System.out.println("Scored");
				cheerSound.play();
				xSpeedOfBall *= 1;
				ySpeedOfBall *= -1;
				xCoordinateOfBall = 225;
				yCoordinateOfBall = 225;
			}

		}
		
		repaint();

	}

	// To add pause and resume functionality
	@Override
	public void keyTyped(KeyEvent e) {

	}

	// Up & down arrows for paddle movement.
	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			homeDisplay = false;
			ballTimer.start();
			start = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP)
			moveUp = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			moveDown = true;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			moveRight = true;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			moveLeft = true;

	}

	// In order to add smooth movement of paddle
	// boolean values flag to false after the key has
	// been relased.
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
			moveUp = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			moveDown = false;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			moveRight = false;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			moveLeft = false;

	}

}
