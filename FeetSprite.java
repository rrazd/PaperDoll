// package paper.doll;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;


public class FeetSprite extends Sprite {

    private Ellipse2D oval = null;
    Point2D anchor;
    private int width;
    private int height;
    private int anchorX;
    private int anchorY;
    
    /**
     * Creates a oval based at the origin with the specified
     * width and height
     */
    public FeetSprite(int x, int y, int width, int height) {
        super();
        this.initialize(x, y, width, height);

    }
    /**
     * Creates a oval based at the origin with the specified
     * width, height, and parent
     */
    public FeetSprite(int x, int y, int width, int height, Sprite parentSprite) {
        super(parentSprite);
        this.initialize(x, y, width, height);
    }
    
    public int getWidth(){
    	return width;
    }
    public int getHeight(){
    	return height;
    }
    
    public int getAnchorY(){
    	return anchorY;
    }
    public int getAnchorX(){
    	return anchorX;
    }
    
    public void initialize(int x, int y, int width, int height) {
        oval = new Ellipse2D.Double(0, -10, width, height);
        this.width = width;
        this.height = height;
        anchorX = x;
        anchorY = y;
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

    protected void drawSprite(Graphics2D g) {
    	g.setColor(new Color(249, 242, 44));
    	g.fill(oval);
        g.draw(oval); //awt method, not our own draw()
        
        //g.draw(this.getFullTransform().createTransformedShape(oval));
    }
	
	public String toString() {
		return "Feet Sprite: " + oval;
	}
}
