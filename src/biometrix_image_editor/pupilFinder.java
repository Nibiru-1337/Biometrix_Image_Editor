/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
 *
 *  Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 */
package biometrix_image_editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Nibiru
 */
public abstract class pupilFinder {
    
    //Find pupil method that uses BFS to find the biggest blob of black pixels
    //For this to work the image had to have been previously binarized with a threshold
    //that best outlines the oval shape of the pupil
    //Assumptions: The pupil is the biggest blob of black pixels
    protected static BufferedImage findPupil(BufferedImage img, BufferedImage originalImg){
        
        BufferedImage finalImg = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        BufferedImage tmpImg = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        int black = basicImageIO.toRGB(0,0,0);
        int white = basicImageIO.toRGB(255,255,255);
        //variables for comparing blob sizes
        int pixelCount = 0;
        int lastPixelCount = 0;
        //variables for storing bounding rectangle for biggest blob
        int topmost = img.getHeight();
        int bottommost = 0;
        int leftmost = img.getWidth();
        int rightmost = 0;
        int tmpTopmost = img.getHeight();
        int tmpBottommost = 0;
        int tmpLeftmost = img.getWidth();
        int tmpRightmost = 0;
        //matrix for storing already visited pixels
        boolean[][] marked = new boolean[img.getWidth()][img.getHeight()];

        //iterate over img pixels
        for(int x = 0 ; x < img.getWidth() ; x++){
            for(int y = 0 ; y < img.getHeight() ; y++) {
                //check if pixel is black and has not been already marked
                if(basicImageIO.isBlack(img,x,y) && !marked[x][y]){
                    //create a new queue of points
                    Queue<Point> queue = new LinkedList<>();
                    //add (black) pixel [x,y] to quere
                    queue.add(new Point(x,y));
                    //update pixel count and final_img if bigger blob is found
                    //and get its bounding rectangle
                    if (pixelCount >= lastPixelCount)
                    {
                        finalImg = basicImageIO.deepCopy(tmpImg);
                        lastPixelCount = pixelCount;
                        topmost = tmpTopmost;
                        bottommost = tmpBottommost;
                        leftmost = tmpLeftmost;
                        rightmost = tmpRightmost;
                    }
                    //reset tmp_img
                    tmpImg = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
                    //reset pixel count
                    pixelCount = 0;
                    //reset tmp's
                    tmpTopmost = img.getHeight();
                    tmpBottommost = 0;
                    tmpLeftmost = img.getWidth();
                    tmpRightmost = 0;
                    //go throught entire queue
                    while(!queue.isEmpty()){
                        //get current point and remove it from queue
                        Point p = queue.remove();
                        //make sure we are within bounds of image
                        if((p.x >= 0) && (p.x < img.getWidth() && (p.y >= 0) && (p.y < img.getHeight()))){
                            //check if pixel has not already been marked and is black
                            if(!marked[p.x][p.y] && basicImageIO.isBlack(img,p.x,p.y)){
                                //mark it
                                marked[p.x][p.y] = true;
                                //increase pixel count
                                pixelCount++;
                                //put the pixel in our tmp image
                                tmpImg.setRGB(p.x,p.y,black);
                                //update topmost/bottommost, leftmost/rightmost
                                if (tmpTopmost > p.y) tmpTopmost = p.y;
                                if (tmpBottommost < p.y) tmpBottommost = p.y;
                                if (tmpLeftmost > p.x) tmpLeftmost = p.x;
                                if (tmpRightmost < p.x) tmpRightmost = p.x;

                                //add neighbors above, below, to the left, and to the right of the current pixel
                                queue.add(new Point(p.x + 1,p.y)); queue.add(new Point(p.x - 1,p.y));
                                queue.add(new Point(p.x,p.y + 1)); queue.add(new Point(p.x,p.y - 1));
                            }
                            else
                                //if pixel is not black or has been marked then set it to white 
                                tmpImg.setRGB(p.x,p.y,white);
                        }
                    }
                }
            }
        }
        //draw over the pupil and display it on original image
        //img = finalImg;
        Graphics2D ghx = originalImg.createGraphics();
        ghx.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ghx.setPaint(Color.green);
        ghx.fillOval(leftmost, topmost, rightmost-leftmost,bottommost - topmost );
        ghx.dispose();
        return originalImg;
    }
}
