// package paper.doll;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Vector;


public abstract class Sprite {



	/**
	 * Tracks our current interaction mode after a mouse-down
	 */
	protected enum InteractionMode {
		IDLE,
		DRAGGING,
		SCALING,
		ROTATING
	}

	private     Vector<Sprite>      children            = new Vector<Sprite>();     // Holds all of our children
	private     Sprite              parent              = null;                     // Pointer to our parent
	private     AffineTransform     transform           = new AffineTransform();    // Our transformation matrix

	protected   Point2D             lastPoint           = null;                     // Last mouse point
	protected   InteractionMode     interactionMode     = InteractionMode.IDLE;     // Current interaction mode
	protected 	double				startAngle;
	private double accumulator = 0;
	private double stretchAmount = 1;
	private String name;

	private double angleDifference = 0;
	private static int scaleCount = 0;
	public static int lowerLegScaleCount = 0;
	public static int upperLegScaleCount = 0;
	float markPosition = 0;

	public Sprite() {
		; // no-op
	}

	public Sprite(Sprite parent) {
		if (parent != null) {
			parent.addChild(this);
		}
	}



	public void setNameOfSprite(String name){
		this.name = name;
	}

	public String getNameOfSprite(){
		return name;
	}

	public void addChild(Sprite s) {
		children.add(s);
		s.setParent(this);
	}
	public Sprite getParent() {
		return parent;
	}
	private void setParent(Sprite s) {
		this.parent = s;
	}

	/**
	 * Test whether a point, in world coordinates, is within our sprite.
	 */
	public abstract boolean pointInside(Point2D p);


	public double getAngle(Point2D pointOfinterest){
		double angle;
		//rotation point for head, upper arms, lower arms
		double origin_y = 0;
		double origin_x = 0;

		double distance_y = 0;
		double distance_x = 0;


		distance_y = Math.abs(getInversePoint(pointOfinterest).getY() - origin_y);
		distance_x = Math.abs(getInversePoint(pointOfinterest).getX() - origin_x);

		//special case, right on top of origin
		if(distance_x == 0) 
		{
			return Math.PI/2;
		}

		angle = Math.atan(distance_y / distance_x);  		   
		return angle;
	}

	/**
	 * Handles a mouse down event, assuming that the event has already
	 * been tested to ensure the mouse point is within our sprite.
	 */
	protected void handleMouseDownEvent(MouseEvent e, String currentSprite) {  

		lastPoint = e.getPoint();
		//left click for drag, right click for rotate
		if (e.getButton() == MouseEvent.BUTTON1 && currentSprite == "torso") {
			interactionMode = InteractionMode.DRAGGING;
		}
		else if(e.getButton() == MouseEvent.BUTTON1 && currentSprite != "torso"){
			interactionMode = InteractionMode.ROTATING;
			startAngle = getAngle(lastPoint);
		}   
		else if(e.getButton() == MouseEvent.BUTTON3 && (name == "rLowerLeg" || name == "lLowerLeg"|| name == "lUpperLeg" || name == "rUpperLeg")){
			interactionMode = InteractionMode.SCALING;
		}
	}

	protected boolean checkIfExceedsDeviationLimit(){
		if(name == "head"){
			if(accumulator + angleDifference >= Math.toRadians(50)){
				angleDifference = Math.toRadians(50) - accumulator;
				return true;
			}	
			else if((accumulator + angleDifference) <= Math.toRadians(-50)){
				//see how much we can deviate by
				angleDifference = Math.toRadians(-50) - accumulator;
				return true;
			}

			return false;
		}
		if(name == "rLowerLeg" || name == "lLowerLeg" || name == "rUpperLeg" || name == "lUpperLeg"){
			if(accumulator + angleDifference >= Math.toRadians(90)){
				angleDifference = Math.toRadians(90) - accumulator;
				return true;
			}	
			else if((accumulator + angleDifference) <= Math.toRadians(-90)){
				//see how much we can deviate by
				angleDifference = Math.toRadians(-90) - accumulator;
				return true;
			}

			return false;
		}



		if(name == "rLowerArm" || name == "lLowerArm"){
			if(accumulator + angleDifference >= Math.toRadians(135)){
				angleDifference = Math.toRadians(135) - accumulator;
				return true;
			}	
			else if((accumulator + angleDifference) <= Math.toRadians(-135)){
				//see how much we can deviate by
				angleDifference = Math.toRadians(-135) - accumulator;
				return true;
			}

			return false;
		}

		if(name == "lHand" || name == "rHand" || name == "lfoot" || name == "rfoot"){
			if(accumulator + angleDifference >= Math.toRadians(35)){
				angleDifference = Math.toRadians(35) - accumulator;
				return true;
			}	
			else if((accumulator + angleDifference) <= Math.toRadians(-35)){
				//see how much we can deviate by
				angleDifference = Math.toRadians(-35) - accumulator;
				return true;
			}

			return false;
		}

		return false;
	}



	protected void handleMouseDragEvent(MouseEvent e) {

		Point2D oldPoint = lastPoint;
		Point2D newPoint = e.getPoint();

		switch (interactionMode) {
		case IDLE:
			; // no-op (shouldn't get here)
			break;
		case DRAGGING:    	
			double x_diff = newPoint.getX() - oldPoint.getX();
			double y_diff = newPoint.getY() - oldPoint.getY();
			this.transform(AffineTransform.getTranslateInstance(x_diff, y_diff));
			break;
		case ROTATING:
			//NOTE: positive angle is CW rotation and neg angle is CCW rotation
			double angleNow = getAngle(newPoint);

			if(name == "head" ){
				int cycles = (int)(accumulator / (Math.toRadians(360)));
				System.out.println(cycles);

				if(accumulator < 0){
					accumulator = accumulator + Math.abs(cycles * (Math.toRadians(360)));
				}
				else{
					accumulator = accumulator - Math.abs(cycles * (Math.toRadians(360)));
				}

				if(newPoint.getX() - oldPoint.getX() > 0){
					angleDifference = Math.abs(angleNow - startAngle + Math.toRadians(3));
					if(checkIfExceedsDeviationLimit()){
						//if true then add this amount will make us go beyond deviation limit so we rotate only up to the deviation limit and not beyond that
						//alter angleDifference value
					}
					accumulator = accumulator + angleDifference;

				}
				else if(newPoint.getX() - oldPoint.getX() < 0){
					angleDifference = -(Math.abs(angleNow - startAngle + Math.toRadians(5)));
					if(checkIfExceedsDeviationLimit()){
						//if true then add this amount will make us go beyond deviation limit so we rotate only up to the deviation limit and not beyond that
						//alter angleDifference value
					}
					accumulator = accumulator + angleDifference; 

				}  
				else{
					angleDifference = 0;

				}
			}
			else{

				newPoint = getInversePoint(newPoint);
				Point2D axis = new Point2D.Double();
				Point2D vectorA;
				Point2D vectorB;
				if(this instanceof OvalSprite){
					axis.setLocation(((OvalSprite)this).getWidth(), ((OvalSprite)this).getAnchorY());

					//find out the vector to mouse point
					vectorA = new Point2D.Double(newPoint.getX() - ((OvalSprite)this).getAnchorX(), newPoint.getY() - ((OvalSprite)this).getAnchorY());
					//find out the vector along axis of rotation
					vectorB = new Point2D.Double(axis.getX() - ((OvalSprite)this).getAnchorX(), axis.getY() - ((OvalSprite)this).getAnchorY());
				}
				else{
					axis.setLocation(((FeetSprite)this).getWidth(), ((FeetSprite)this).getAnchorY());

					//find out the vector to mouse point
					vectorA = new Point2D.Double(newPoint.getX() - ((FeetSprite)this).getAnchorX(), newPoint.getY() - ((FeetSprite)this).getAnchorY());
					//find out the vector along axis of rotation
					vectorB = new Point2D.Double(axis.getX() - ((FeetSprite)this).getAnchorX(), axis.getY() - ((FeetSprite)this).getAnchorY());
				}
				//get the dot product
				double dotProductResult = (vectorA.getX() * vectorB.getX()) + (vectorA.getY() * vectorB.getY());

				double magnAnchorLine = Math.sqrt(Math.pow(vectorB.getX(), 2) + Math.pow(vectorB.getY(), 2));

				double magnDistToMousePoint = Math.sqrt(Math.pow(vectorA.getX(), 2) + Math.pow(vectorA.getY(), 2));

				//find alpha, can do this because of equation A dot B = magA * magB * cos(alpha)
				double cosineTerm = dotProductResult / (magnAnchorLine * magnDistToMousePoint);

				double alpha = Math.acos(cosineTerm);

				if(newPoint.getY() < axis.getY()){
					alpha = -1 * alpha;
				}

				angleDifference = alpha / 10.0;
				if(checkIfExceedsDeviationLimit()){
					//if true then add this amount will make us go beyond deviation limit so we rotate only up to the deviation limit and not beyond that
					//alter angleDifference value
				}
				accumulator = accumulator + (angleDifference * 2);

				if(this instanceof OvalSprite){
					transform.rotate(angleDifference, ((OvalSprite)this).getAnchorX(), ((OvalSprite)this).getAnchorY());
				}
				else{
					transform.rotate(angleDifference, ((FeetSprite)this).getAnchorX(), ((FeetSprite)this).getAnchorY());
				}
			}  

			System.out.println("Accumulator value: " + Math.toDegrees(accumulator));
			System.out.println("AngleDifference: " + Math.toDegrees(angleDifference));
			//update the angle we are at right now
			startAngle = angleNow;
			markPosition++;
			break;
		case SCALING:

			/*
			 * SCALING CODE
			 */
			if(newPoint.getY() - oldPoint.getY() > 0){
				if((name == "rLowerLeg" || name == "lLowerLeg") && lowerLegScaleCount >= 2){
					return;
				}
				if((name == "rUpperLeg" || name == "lUpperLeg") && upperLegScaleCount >= 2){
					return;
				}
				if(name == "rLowerLeg" || name == "lLowerLeg"){
					lowerLegScaleCount++;
				}
				else{
					upperLegScaleCount++;
				}

				//set the stretch amount
				stretchAmount = stretchAmount * 2;;


				if(name == "rLowerLeg"){

					this.parent.parent.children.get(4).children.get(0).stretchAmount = this.stretchAmount;            		
				}
				else if(name == "lLowerLeg"){

					this.parent.parent.children.get(3).children.get(0).stretchAmount = this.stretchAmount;            		
				}

				//scale other side too
				if(name == "rUpperLeg"){
					this.parent.children.get(4).stretchAmount = this.stretchAmount;
				}
				else if(name == "lUpperLeg"){
					this.parent.children.get(3).stretchAmount = this.stretchAmount;
				}


				/* Trying to keep foot and/or lower leg size the same */

				if(name == "rLowerLeg"){
					System.out.println("trying to stop Modifying foot");
					this.children.get(0).transform.scale(1, 0.5);
					//other side
					this.parent.parent.children.get(4).children.get(0).children.get(0).transform.scale(1, 0.5);
				}
				else if(name == "lLowerLeg"){
					System.out.println("trying to stop Modifying foot");
					this.children.get(0).transform.scale(1, 0.5);
					//other side
					this.parent.parent.children.get(3).children.get(0).children.get(0).transform.scale(1, 0.5);
				}
				else if(name == "rUpperLeg"){
					System.out.println("trying to stop Modifying foot and lower leg");
					this.children.get(0).transform.scale(0.5, 1);
					//other side 
					this.parent.children.get(4).children.get(0).transform.scale(0.5, 1);
				}
				else if(name == "lUpperLeg"){
					System.out.println("trying to stop Modifying foot and lower leg");
					this.children.get(0).transform.scale(0.5, 1);
					//other side 
					this.parent.children.get(3).children.get(0).transform.scale(0.5, 1);
				}

			}
			else if(newPoint.getY() - oldPoint.getY() < 0){

				if((name == "rLowerLeg" || name == "lLowerLeg") && lowerLegScaleCount <= -2){
					return;
				}
				if((name == "rUpperLeg" || name == "lUpperLeg") && upperLegScaleCount <= -2){
					return;
				}

				if(name == "rLowerLeg" || name == "lLowerLeg"){
					lowerLegScaleCount--;
				}
				else{
					upperLegScaleCount--;
				}


				//set the stretch amount
				stretchAmount = stretchAmount * (0.5);


				if(name == "rLowerLeg"){

					this.parent.parent.children.get(4).children.get(0).stretchAmount = this.stretchAmount;            		
				}
				else if(name == "lLowerLeg"){

					this.parent.parent.children.get(3).children.get(0).stretchAmount = this.stretchAmount;            		
				}

				//scale other side too
				if(name == "rUpperLeg"){
					this.parent.children.get(4).stretchAmount = this.stretchAmount;
				}
				else if(name == "lUpperLeg"){
					this.parent.children.get(3).stretchAmount = this.stretchAmount;
				}




				/* Trying to keep foot and/or lower leg size the same */

				if(name == "rLowerLeg"){
					System.out.println("trying to stop Modifying foot");
					this.children.get(0).transform.scale(1, 2);
					//other side
					this.parent.parent.children.get(4).children.get(0).children.get(0).transform.scale(1, 2);
				}
				else if(name == "lLowerLeg"){
					System.out.println("trying to stop Modifying foot");
					this.children.get(0).transform.scale(1, 2);
					//other side
					this.parent.parent.children.get(3).children.get(0).children.get(0).transform.scale(1, 2);
				}
				else if(name == "rUpperLeg"){
					System.out.println("trying to stop Modifying foot and lower leg");
					this.children.get(0).transform.scale(2, 1);
					//other side 
					this.parent.children.get(4).children.get(0).transform.scale(2, 1);
				}
				else if(name == "lUpperLeg"){
					System.out.println("trying to stop Modifying foot and lower leg");
					this.children.get(0).transform.scale(2, 1);
					//other side 
					this.parent.children.get(3).children.get(0).transform.scale(2, 1);
				}

			}     
			System.out.println("scale count: " + scaleCount);


			//don't rotate while in scale mode, this should not occur particularly since it will skew the accumulator value
			angleDifference = 0;
			break;
		}
		// Save our last point, if it's needed next time around
		lastPoint = e.getPoint();

		//perform rotate transformation
		transform.rotate(angleDifference);
	}                


	protected void handleMouseUp(MouseEvent e) {
		interactionMode = InteractionMode.IDLE;
		// Do any other interaction handling necessary here
	}


	public Sprite getSpriteHit(MouseEvent e) {
		for (Sprite sprite : children) {
			Sprite s = sprite.getSpriteHit(e);

			if (s != null) {
				return s;
			}
		}
		if (this.pointInside(e.getPoint())) {
			return this;
		}
		return null;
	}



	/*
	 * Important note: How transforms are handled here are only an example. You will
	 * likely need to modify this code for it to work for your assignment.
	 */

	/**
	 * Returns the full transform to this object from the root
	 */
	public AffineTransform getFullTransform() {
		AffineTransform returnTransform = new AffineTransform();
		Sprite curSprite = this;
		while (curSprite != null) {
			returnTransform.preConcatenate(curSprite.getLocalTransform());
			curSprite = curSprite.getParent();
		}
		return returnTransform;
	}

	/**
	 * Returns our local transform
	 */
	public AffineTransform getLocalTransform() {
		//always apply scaling right before paint
		AffineTransform local = (AffineTransform)transform.clone();
		//this is actually seen as scaling about y axis as needed, since the shape is originally drawn horizontally in inverse space

		local.scale(stretchAmount, 1);
		return local;
	}

	/**
	 * Performs an arbitrary transform on this sprite
	 */
	public void transform(AffineTransform t) {
		transform.concatenate(t);
	}

	/**
	 * Draws the sprite. This method will call drawSprite after
	 * the transform has been set up for this sprite.
	 */
	public void draw(Graphics2D g) {
		AffineTransform oldTransform = g.getTransform();

		if(name == "head"){
			float marks[] = {10.0f};
			g.setColor(Color.MAGENTA);
			g.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 5.0f, marks, markPosition));
		}
		else{
			g.setStroke(new BasicStroke(3));
		}
		// Set to our transform
		g.setTransform(this.getFullTransform());

		// Draw the sprite (delegated to sub-classes)
		this.drawSprite(g);

		// Restore original transform
		g.setTransform(oldTransform);
		// Draw children
		for (Sprite sprite : children) {
			sprite.draw(g);
		}
	}

	/**
	 * The method that actually does the sprite drawing. This method
	 * is called after the transform has been set up in the draw() method.
	 * Sub-classes should override this method to perform the drawing.
	 */
	protected abstract void drawSprite(Graphics2D g);


	protected Point2D getInversePoint(Point2D currentPoint){
		AffineTransform tmp = new AffineTransform(getFullTransform());

		try {
			tmp = tmp.createInverse();
		} catch (NoninvertibleTransformException e){
			e.printStackTrace();
		}

		Point2D outputPoint = (Point2D)currentPoint.clone();
		tmp.transform(outputPoint, outputPoint);
		return outputPoint;
	}
}
