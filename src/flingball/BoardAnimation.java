package flingball;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.Timer;
import java.util.TimerTask;

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

	private void checkRep() {
		// TODO
	}
	
	    public static void main(String[] args) {
	    	Board toDraw = new Board();
	    	//TODO Ball appears on row above bumpers despite having same y position. 
	    	//TODO: Ball goes out top left corner? Ball toPlay = new Ball(new Vect(4.5,5.5), new Vect(-1,2), 0.5);
	    	//TODO: Ball get stuck Ball toPlay = new Ball(new Vect(4.5, 5.5), new Vect(0,-1), 0.5);
	    	Ball toPlay = new Ball(new Vect(5.5, 4.5), new Vect(1,0), 0.5);
	    	Gadget squareBumper = new SquareBumper("Square Bumper", 5, 5);
	    	Gadget circleBumper = new CircleBumper("Circle Bumper", 10, 5);
	        toDraw.addBall(toPlay);
	        toDraw = toDraw.addGadget(squareBumper);
	        toDraw = toDraw.addGadget(circleBumper);
	        System.out.println("BoardAnimation 40: " + toDraw.getGadgets());

	        
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
	        checkRep();
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
	            timer.schedule(play, 0, 100);//TODO
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
	    			final Vect anchor = ball.getAnchor().times(20);
	    			System.out.println("BoardAnimation 105: Ball center is " + ball.getBoardCenter());
	    			
	    			
	    			g2d.drawImage(ball.generate(20), (int) anchor.x(), (int) anchor.y(), NO_OBSERVER_NEEDED);
	    					
	    		}
	    		
	    		for (Gadget gadget : this.board.getGadgets()) {
	    			final int xAnchor = (int) gadget.position().x()*20;
	    			final int yAnchor = (int) gadget.position().y()*20;
	    			
	    			g2d.drawImage(gadget.generate(20), xAnchor, yAnchor, NO_OBSERVER_NEEDED);
	    			
	    		}
	    		
	    		for (int i = 1; i <= 20; i++) {
	    			g2d.setColor(Color.GREEN);
	    			g2d.drawLine(0, i*20, 20 * 20, i*20);
	    			g2d.drawLine(i*20, 0, i*20, 20 * 20);
	    			
	    			
	    		}
	    			
//	            g2d.setColor(Color.RED);
//	            g2d.fillOval(x, y, 30, 30);
	            g2d.dispose();
	        }

	    }

	}
