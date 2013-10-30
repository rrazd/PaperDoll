//package paper.doll;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SpriteCanvas extends JPanel {

	Point M = new Point(); // mouse point
	/**
	 * Tracks our current interactive mode
	 */
	private enum InteractionMode {
		IDLE, RECORDING, PLAYBACK
	}

	private Vector<Sprite> sprites = new Vector<Sprite>(); // All sprites we're
															// managing
	
	private Vector<Sprite> spritesAtRecording = new Vector<Sprite>();
	private Sprite interactiveSprite = null; // Sprite with which user is
												// interacting
	private InteractionMode interactionMode = InteractionMode.IDLE; // Current
																	// interactive
																	// mode
	private Vector<Serializable> eventStream = null; // Event stream for recording events
	private long lastTime = -1; // Time of last event
	private boolean firstRun = true;
	
	public SpriteCanvas() {
		
		initialize();
	 
	}

	private void initialize() {
		
		// Install our event handlers
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
		    	System.out.println("Click at: " + e.getPoint());
		    	
				handleMousePress(e);
				repaint();
			
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				handleMouseReleased(e);
			
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseDragged(e);
				repaint();
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				if(firstRun){
					repaint();	
				}
			}
		});
		
		
	}
	
	
	/**
	 * Handle mouse press events from user or demo file
	 */
	private void handleMousePress(java.awt.event.MouseEvent e) {
		for (Sprite sprite : sprites) {
			interactiveSprite = sprite.getSpriteHit(e);
			if (interactiveSprite != null) {
				recordEvent(e);
				String currentName = interactiveSprite.getNameOfSprite();

				interactiveSprite.handleMouseDownEvent(e, currentName);
				break;
			}
		}
	}

	/**
	 * Handle mouse released events from user or demo file
	 */
	private void handleMouseReleased(MouseEvent e) {
		if (interactiveSprite != null) {
			recordEvent(e);
			interactiveSprite.handleMouseUp(e);
		}
		interactiveSprite = null;
	}

	/**
	 * Handle mouse dragged events from user or demo file
	 */
	private void handleMouseDragged(MouseEvent e) {
		if (interactiveSprite != null) {
			recordEvent(e);
			//only drag and rotate if on top of some shape, otherwise stop
//			if(interactiveSprite.pointInside(e.getPoint()) ){ 
				interactiveSprite.handleMouseDragEvent(e);
//			}
		}
	}

	

	
	
	/**
	 * Record an event, but only if recording is turned on
	 */
	private void recordEvent(MouseEvent event) {
		if (interactionMode.equals(InteractionMode.RECORDING)) {
			
			long thisTime = System.currentTimeMillis();
			eventStream.add(thisTime - lastTime);
			eventStream.add(event);
			lastTime = thisTime;
		}
	}

	/**
	 * Add a top-level sprite to manage
	 */
	public void addSprite(Sprite s) {
		sprites.add(s);
	}
	
	public void removeAllSprite(){
		sprites.removeAllElements();
	}

	/**
	 * Paint our canvas
	 */
	@Override
	public void paint(Graphics g) {
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 600, 720, 200); // draw the ground 
		g.setColor(new Color(16, 32, 118));
		g.fillRect(0, 0, 720, 600); // draw the sky
		
		for (Sprite sprite : sprites) {
			sprite.draw((Graphics2D) g);
		}
		
	}

	/**
	 * Starts recording of events.
	 */
	public void startRecording() {
		interactionMode = InteractionMode.RECORDING;
		eventStream = new Vector<Serializable>();
		lastTime = System.currentTimeMillis();
	}

	/**
	 * Stops recording events, writes them to a file called "demo"
	 */
	public void stopRecording() {
		interactionMode = InteractionMode.IDLE;
		if (eventStream != null) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream("demo"));
				for (Serializable item : eventStream) {
					out.writeObject(item);
				}
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Starts the demo. Important note: This simply plays back events that were
	 * previously recorded. Thus, it is assumed that the sprites are in the
	 * *exact same state* as when recording started. If not, you will get
	 * unexpected results. This demo code DOES NOT try to reset the sprites to
	 * the initial state they were in when recording started. You may want to
	 * add this feature.
	 * 
	 * A message box will be shown if the demo file cannot be found.
	 */
	public void startDemo() {
		
		Main.reset(this);		//assuming we always start recording at initial position upon start up
		
		interactionMode = InteractionMode.PLAYBACK;
		final Vector<Serializable> playbackStream = new Vector<Serializable>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					"demo"));
			while (true) {
				Serializable event = (Serializable) in.readObject();
				playbackStream.add(event);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Demo file not found.");
		} catch (EOFException e) {
			; // ignore: This is how we know we're done reading the file
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Create and start a thread to play back the events
		Thread playbackThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean hadFirstWait = false;
				Iterator<Serializable> iter = playbackStream.iterator();
				while (iter.hasNext()
						&& interactionMode.equals(InteractionMode.PLAYBACK)) {
					Long waitTime = (Long) iter.next();
					MouseEvent event = (MouseEvent) iter.next();
					try {
						if (hadFirstWait) {
							Thread.sleep(waitTime);
						} else {
							hadFirstWait = true;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					switch (event.getID()) {
					case MouseEvent.MOUSE_PRESSED:
						handleMousePress(event);
						repaint();
						break;
					case MouseEvent.MOUSE_DRAGGED:
						handleMouseDragged(event);
						repaint();
						break;
					case MouseEvent.MOUSE_RELEASED:
						handleMouseReleased(event);
						repaint();
						break;
					}
				}
			}
		});
		playbackThread.start();
	}

	/**
	 * Stops the demo.
	 */
	public void stopDemo() {
		interactionMode = InteractionMode.IDLE;
	}


}
