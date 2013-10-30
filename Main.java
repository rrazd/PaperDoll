// package paper.doll;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.FlowLayout;

public class Main {

	private static boolean isSmile = true;

	public static void main(String[] args) {		

		SpriteCanvas canvas = new SpriteCanvas();
		canvas.addSprite(Main.makeSprite1());

		JFrame f = new JFrame("Rag Doll");
		
		f.setJMenuBar(Main.makeMenuBar(canvas));

		f.getContentPane().setLayout(new GridLayout(1, 1));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBackground(Color.BLACK);
		f.setLayout(new BorderLayout());

		JPanel buttonPanel = Main.buttonActivity(canvas);
		
		f.add(canvas);
		f.add(buttonPanel,BorderLayout.SOUTH);

		f.setResizable(false);
		f.setSize(720, 740);
		f.setVisible(true);
		
	}
	
	private static JPanel buttonActivity(final SpriteCanvas canvas){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton smileButton = new JButton("Smile");
		JButton frownButton = new JButton("Frown");
		buttonPanel.add(smileButton);
		buttonPanel.add(frownButton);

		smileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String cmd = e.getActionCommand();
				if(cmd.equals("Smile"))
				{
					isSmile = true;
					reset(canvas);
				}
			}
		});

		frownButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				String cmd = e.getActionCommand();
				if(cmd.equals("Frown"))
				{
					isSmile = false;
					reset(canvas);
				}
			}
		});

		return buttonPanel;
	}

	
	/* Make a sample sprite for testing purposes. */
	private static Sprite makeSprite1() {
		Sprite torso = new OvalSprite(0, 0, 120, 180);
		torso.setNameOfSprite("torso");
		Sprite head = new FaceSprite(-40, -80, 80, 80, isSmile);
		head.setNameOfSprite("head");
		
		Sprite rUpperArm = new OvalSprite(0, 35, 80, 40);
		rUpperArm.setNameOfSprite("rUpperArm");
		Sprite rLowerArm = new OvalSprite(0, 35, 80, 30);
		rLowerArm.setNameOfSprite("rLowerArm");
		Sprite lUpperArm = new OvalSprite(0, 35, 80, 40);
		lUpperArm.setNameOfSprite("lUpperArm");
		Sprite lLowerArm = new OvalSprite(0, 35, 80, 30);
		lLowerArm.setNameOfSprite("lLowerArm");
		Sprite rHand = new OvalSprite(0, 20, 40, 20);
		rHand.setNameOfSprite("rHand");
		Sprite lHand = new OvalSprite(0, 20, 40, 20);
		lHand.setNameOfSprite("lHand");
		Sprite rUpperLeg = new OvalSprite(0, 35, 100, 30);
		rUpperLeg.setNameOfSprite("rUpperLeg");
		Sprite lUpperLeg = new OvalSprite(0, 35, 100, 30);
		lUpperLeg.setNameOfSprite("lUpperLeg");
		Sprite rLowerLeg = new OvalSprite(0, 35, 100, 30);
		rLowerLeg.setNameOfSprite("rLowerLeg");
		Sprite lLowerLeg = new OvalSprite(0, 35, 100, 30);
		lLowerLeg.setNameOfSprite("lLowerLeg");
		FeetSprite lfoot = new FeetSprite(0, 10, 50, 20);
		lfoot.setNameOfSprite("lfoot");
		FeetSprite rfoot = new FeetSprite(0, 10, 50, 20);
		rfoot.setNameOfSprite("rfoot");

		torso.transform(AffineTransform.getTranslateInstance(300, 125));
		head.transform(AffineTransform.getTranslateInstance(60, 0));
		rUpperArm.transform(AffineTransform.getTranslateInstance(110, 10));
		rLowerArm.transform(AffineTransform.getTranslateInstance(80, 5));
		lUpperArm.transform(AffineTransform.getRotateInstance(Math.PI));
		lUpperArm.transform(AffineTransform.getTranslateInstance(-10, -50));
		lLowerArm.transform(AffineTransform.getTranslateInstance(80, 5));
		rHand.transform(AffineTransform.getTranslateInstance(80, 5));
		lHand.transform(AffineTransform.getTranslateInstance(80, 4));
		rUpperLeg.transform(AffineTransform.getTranslateInstance(100, 170));
		rUpperLeg.transform(AffineTransform.getRotateInstance(Math.PI * 0.5));
		lUpperLeg.transform(AffineTransform.getTranslateInstance(50, 170));
		lUpperLeg.transform(AffineTransform.getRotateInstance(Math.PI * 0.5));
		rLowerLeg.transform(AffineTransform.getTranslateInstance(100, -1));
		lLowerLeg.transform(AffineTransform.getTranslateInstance(100, -1));
		rfoot.transform(AffineTransform.getTranslateInstance(100, 15));
		rfoot.transform(AffineTransform.getRotateInstance(-Math.PI * 0.5));
		lfoot.transform(AffineTransform.getTranslateInstance(100, 10));
		lfoot.transform(AffineTransform.getRotateInstance(Math.PI * 0.5));
	
		torso.addChild(head);
		torso.addChild(rUpperArm);
		rUpperArm.addChild(rLowerArm);
		torso.addChild(lUpperArm);
		lUpperArm.addChild(lLowerArm);
		rLowerArm.addChild(rHand);
		lLowerArm.addChild(lHand);
		torso.addChild(rUpperLeg);
		torso.addChild(lUpperLeg);
		rUpperLeg.addChild(rLowerLeg);
		lUpperLeg.addChild(lLowerLeg);
		lLowerLeg.addChild(lfoot);
		rLowerLeg.addChild(rfoot);
		
		rUpperLeg.upperLegScaleCount = 0;
		rLowerLeg.lowerLegScaleCount = 0;
		lLowerLeg.lowerLegScaleCount = 0;
		lUpperLeg.upperLegScaleCount = 0;
		
		return torso;

	}


	
	
	
	public static void reset(SpriteCanvas canvas){
		canvas.removeAllSprite();
		canvas.addSprite(makeSprite1());
		canvas.repaint();
	}
	
	/* Menu with recording and playback. */
	private static JMenuBar makeMenuBar(final SpriteCanvas canvas) {
		JMenuBar mbar = new JMenuBar();
		JMenu script = new JMenu("Scripting");
		JMenu file = new JMenu("File");
		final JMenuItem record = new JMenuItem("Start recording");
		final JMenuItem play = new JMenuItem("Start script");
		final JMenuItem reset = new JMenuItem("Reset (Ctrl-R)");
		final JMenuItem quit = new JMenuItem("Quit");

		script.add(record);
		script.add(play);
		file.add(reset);
		file.addSeparator();
		file.add(quit);

		
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				if(quit.getText().equals("Quit")){
					System.exit(0);
				}

				else{
					assert false;
				}
			}
		});
		
	
		reset.setAccelerator(KeyStroke.getKeyStroke("control R"));
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				if(reset.getText().equals("Reset (Ctrl-R)")){
					reset(canvas);
				}

				else{
					assert false;
				}
			}
		});
		
		record.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (record.getText().equals("Start recording")) {
					reset(canvas);
					record.setText("Stop recording");
					canvas.startRecording();
				} else if (record.getText().equals("Stop recording")) {
					record.setText("Start recording");
					canvas.stopRecording();
				}
				
				else {
					assert false;
				}
			}
		});

		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (play.getText().equals("Start script")) {
					play.setText("Stop script");
					record.setEnabled(false);
					canvas.startDemo();
				} else if (play.getText().equals("Stop script")) {
					play.setText("Start script");
					record.setEnabled(true);
					canvas.stopRecording();
				} else {
					assert false;
				}
			}
		});

		mbar.add(file);
		mbar.add(script);
		return mbar;
	}
}
