// package paper.doll;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Random;



public class FaceSprite extends Sprite {

    private Ellipse2D oval = null;
    Arc2D.Double leftEye = null;
    Arc2D.Double rightEye = null;
    Arc2D.Double mouth = null;
    private boolean expressionHappy;
    private int i;
    
    /**
     * Creates a oval based at the origin with the specified
     * width and height
     */
    public FaceSprite(int x, int y, int width, int height, boolean isSmile) {
        super();
    	expressionHappy = isSmile;
        this.initialize(x, y, width, height);
    }
    /**
     * Creates a oval based at the origin with the specified
     * width, height, and parent
     */
    public FaceSprite(int x, int y, int width, int height, Sprite parentSprite) {
        super(parentSprite);
        this.initialize(x, y, width, height);
    	i = randInt(1, 4);
    }
    
  
    
    private void initialize(int x, int y, int width, int height) {
    	
        oval = new Ellipse2D.Double(x, y, width, height);

        if(expressionHappy){
        	rightEye = new Arc2D.Double(10,-60,10,10,0,360,Arc2D.PIE);
        	leftEye = new Arc2D.Double(-20,-60,10,10,0,360,Arc2D.PIE);
        	mouth = new Arc2D.Double(-25,-60,50,50,200,140,Arc2D.CHORD);
    	}
        else{
	    	rightEye = new Arc2D.Double(17,-60,10,15,0,360,Arc2D.PIE);
	    	leftEye = new Arc2D.Double(-27,-60,10,15,0,360,Arc2D.PIE);
        	mouth = new Arc2D.Double(-25,-35,50, 50,160,-140,Arc2D.CHORD);
        }
    }
    
    /**
     * Test if our oval contains the point specified.
     */
    public boolean pointInside(Point2D p) {
        AffineTransform fullTransform = this.getFullTransform();
        AffineTransform inverseTransform = null;
        try {
            inverseTransform = fullTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        Point2D newPoint = (Point2D)p.clone();
        inverseTransform.transform(newPoint, newPoint);
        return oval.contains(newPoint);
    }
    
    
    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
   

    protected void drawSprite(Graphics2D g) {
  
    	g.setColor(new Color(249, 242, 44));
    	g.fill(oval);
        g.draw(oval); //awt method, not our own draw()
       	
   
        g.setColor(Color.red);	  
    	g.fill(mouth);		
    	g.draw(mouth);
    	
        g.setColor(Color.black);	  
       	g.fill(leftEye);		
       	g.draw(leftEye);
       	
        g.setColor(Color.black);	  
       	g.fill(rightEye);		
       	g.draw(rightEye);
       	
   }
	
	public String toString() {
		return "Oval Sprite: " + oval;
	}
}
