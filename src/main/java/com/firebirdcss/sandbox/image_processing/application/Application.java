package com.firebirdcss.sandbox.image_processing.application;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This class is basically just a Java Main that I am using to play around with some ideas I have on
 * the topic of Image Processing using Java. So in other words... This is just kind-of a sandbox.
 * 
 * @author Scott Griffis
 *
 */
public class Application {
	private static final String INPUT_FILE_PATH = "/Users/sgriffis/Pictures/IMG_20161110_105501.jpg";
	private static final String OUTPUT_FILE_PATH = "/Users/sgriffis/Desktop/out.jpg";
	private static boolean enablePreview = false;
	private static boolean enableImageShowThrough = false;
	private static final int MULT_IPASS_COUNT = 1;
	
	private static final int TRACE_COLOR = 6945792; // Decimal value of #00FF00
	
	private static final double THRESHOLD_PERCENT = 93;
	
	/**
	 * APPLICATION MAIN: The main entry point of the application.
	 * 
	 * @param args - Not used.
	 */
	public static void main(String[] args) {
		try {
			File pictureFile = new File(INPUT_FILE_PATH);
			BufferedImage bImage = ImageIO.read(pictureFile);
			
			int width = bImage.getWidth();
			int height = bImage.getHeight();
			
			BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			for (int i = 1; i <= MULT_IPASS_COUNT; i++) {
				if (i != 1) {
					bImage = renderedImage;
					renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				}
				
				
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						Color rColor = new Color(bImage.getRGB(w, h));
						Color xColor = w + 1 < width ? new Color(bImage.getRGB(w + 1, h)) : new Color(0);
						Color yColor = h + 1 < height ? new Color(bImage.getRGB(w, h + 1)) : new Color(0);
						Color zColor = h + 1 < height && w + 1 < width ? new Color(bImage.getRGB(w + 1, h + 1)) : new Color(0);
						if (outTolerance(rColor, xColor, yColor, zColor)) {
							renderedImage.setRGB(w, h, TRACE_COLOR);
						} else {
							if (enableImageShowThrough) {
								renderedImage.setRGB(w, h, rColor.getRGB());
							}
						}
						
					}
				}
			}
			
			/* Entire image has been processed, write to file */
			ImageIO.write(renderedImage, "jpg", new File(OUTPUT_FILE_PATH));
			
			/* Generate preview if enabled */
			if (enablePreview) {
				ImageIcon icon = new ImageIcon(renderedImage);
				JFrame window = new JFrame("Image");
				JLabel label = new JLabel();
				label.setIcon(icon);
				window.add(label);
				window.pack();
				window.setVisible(true);
			}
		} catch (IOException e) {
			System.out.println("OOPS... You broke it: ");
			e.printStackTrace();
		}
		
		System.out.println("~ Application Ended ~");
	}
	
	/**
	 * Used to determine if a particular pixel is more different from it's surrounding pixels than the 
	 * allowed tolerance as determined by {@value #THRESHOLD_PERCENT}.
	 * 
	 * @param rPix - The pixel of reference as {@link Color}
	 * @param xPix - The pixel to the right of the reference pixel as {@link Color}
	 * @param yPix - The pixel below the pixel of reference as {@link Color}
	 * @param zPix - The pixel to the lower right diagonal of the reference pixel as {@link Color}
	 * @return Returns a true if the pixel is out of tolerance with all of its X-Y-Z neighbors, otherwise returns false as {@link Boolean}
	 */
	private static boolean outTolerance(Color rPix, Color xPix, Color yPix, Color zPix) {
		/* Puts X-Y-Z pixels into an array to aid in the reuse of code */
		Color[] compPixs = {
				xPix,
				yPix,
				zPix
		};
		
		boolean outOfTolerance = true;
		
		/* Unpack reference pixel into its RGB decimal values */
		double rRed = rPix.getRed();
		double rGreen = rPix.getGreen();
		double rBlue = rPix.getBlue();
		
		/* Compare reference pixel to its neighbor pixels for Tolerance */
		for (Color cPix : compPixs) {
			double cRed = cPix.getRed();
			double cGreen = cPix.getGreen();
			double cBlue = cPix.getBlue();
			
			/* Check Red for Threshold */
			if (rRed != cRed) {
				if (rRed < cRed) {
					if (!(rRed / cRed * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				} else {
					if (!(cRed / rRed * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				}
			}
			
			/* Check Green for Threshold */
			if (rGreen != cGreen) {
				if (rGreen < cGreen) {
					if (!(rGreen / cGreen * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				} else {
					if (!(cGreen / rGreen * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				}
			}
			
			/* Check Blue for Threshold */
			if (rBlue != cBlue) {
				if (rBlue < cBlue) {
					if (!(rBlue / cBlue * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				} else {
					if (!(cBlue / rBlue * 100 > THRESHOLD_PERCENT)) {
						outOfTolerance = false;
						break;
					}
				}
			}
		}
		
		return outOfTolerance;
	}
}
