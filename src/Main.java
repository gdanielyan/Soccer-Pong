import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/*
 * Author: Georgiy Danielyan
 * Assignment 6 For Java Course (GCC 139)
 * Completion date: June 9, 2015
 */


public class Main {

	public static void main(String[] args){
		//Frame to hold pong board
		JFrame frame = new JFrame("Soccer Pong");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PongBoard pongPanel = new PongBoard();
		frame.add(pongPanel);
		
		frame.setSize(PongBoard.field.getWidth(null),PongBoard.field.getHeight(null)+20);
		frame.setVisible(true);

	}
}
