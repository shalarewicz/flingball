package flingball;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;
//import javax.swing.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import physics.Vect;

public class BoardAnimation {
	
	private Board board1;
	/*
	 * TODO: AF()
	 * TODO: Rep Invariant
	 * TODO: Safety from rep exposure
	 */

//	private void checkRep() {
//		// TODO
//	}
//	
	
	    public static void main(String[] args) {
	    	Board toDraw = new Board();
	    	Ball toPlay = new Ball(new Vect(10,10), new Vect(1,2), 0.5);
	        toDraw.addBall(toPlay);
	        new BoardAnimation(toDraw);
	    }

	    public BoardAnimation(Board board) {
	    	this.board1 = board;
	        EventQueue.invokeLater(new Runnable() {
	            private Board board = board1;

				@Override
	            public void run() {
	                try {
	                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }

	                JFrame frame = new JFrame("Testing");
	                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                frame.add(new TestPane(this.board));
	                frame.pack();
	                frame.setLocationRelativeTo(null);
	                frame.setVisible(true);
	            }
	        });
	    }

	    public class TestPane extends JPanel {

	        private Board board;

	        public TestPane(Board board) {
	        	this.board= board;
	            Timer timer = new Timer();
	            TimerTask play = new TimerTask() {
//	                @Override
	                public void run() {
	                    board.play(1);
	                    repaint();
	                }
	            };
	            timer.schedule(play, 0, 25);
	        }


	        @Override
	        public Dimension getPreferredSize() {
	            return new Dimension(this.board.getHeight() * 20, this.board.getWidth() * 20);
	        }

	        @Override
	        protected void paintComponent(Graphics graphics) {
	            super.paintComponent(graphics);
	            Graphics2D g2d = (Graphics2D) graphics.create();
	            graphics.setColor(Color.BLACK);
	    		graphics.fillRect(0, 0, this.board.getHeight() * 20, this.board.getWidth() * 20);
	    		
	    		final ImageObserver NO_OBSERVER_NEEDED = null;
	    		
	    		graphics.setColor(Color.BLUE);
	    		for (Ball ball : this.board.getBalls()) {
	    			final int xAnchor = (int) ball.getPosition().x() * 20;
	    			final int yAnchor = (int) ball.getPosition().y() * 20;
	    			
	    			
	    			g2d.drawImage(ball.generate(20), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
	    					
	    		}
	    		
	    		for (Gadget gadget : this.board.getGadgets()) {
	    			final int xAnchor = (int) gadget.position().x()*20;
	    			final int yAnchor = (int) gadget.position().y()*20;
	    			
	    			g2d.drawImage(gadget.generate(20), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
	    			
	    		}
//	            g2d.setColor(Color.RED);
//	            g2d.fillOval(x, y, 30, 30);
	            g2d.dispose();
	        }

	    }

	}
