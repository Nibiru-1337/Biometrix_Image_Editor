/*
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                    Version 2, December 2004
 *
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 */
package biometrix_image_editor;

import java.awt.image.BufferedImage;

/**
 *
 * @author Nibiru
 */
public abstract class adaptiveThresholding {
    
    
    protected static BufferedImage adaptiveThresholding(BufferedImage img){
        BufferedImage copy = basicImageIO.deepCopy(img);
        //iterate over pixels
        for (int x = 1; x < img.getWidth()-1; x++){
            for (int y = 1; y < img.getHeight()-1; y++){
                int pixel = img.getRGB(x, y);
                int R = basicImageIO.getR(pixel);
                //int G = basicImageIO.getG(pixel);
                //int B = basicImageIO.getB(pixel);
                //int mean = ( R + G + B )/ 3;
                
                
                //find threshold for current pixel
                int cMin = 255;
                int cMax = 0;
                for(int i = x-1; i < x+2; i++ ){
                    for(int j = y-1; j < y+2; j++){
                        int nPixel = img.getRGB(i, j);
                        if (basicImageIO.getR(nPixel) < cMin) cMin = basicImageIO.getR(nPixel);
                        if (basicImageIO.getR(nPixel) > cMax) cMax = basicImageIO.getR(nPixel);
                    }
                }
                int T = (cMax + cMin) / 2;
                //if ( T < 100) T = 100;
                //if ( T > 200) T = 200;
                
                if (R < T)
                    pixel = basicImageIO.toRGB(0,0,0);
                else
                    pixel = basicImageIO.toRGB(255,255,255);
                copy.setRGB(x,y,pixel);
            }
        }
        return copy;
    }
}