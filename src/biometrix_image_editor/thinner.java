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
public class thinner {
    private static final int deletionArray [] = {3, 5, 7, 12, 13, 14 ,21, 22, 23, 28, 29, 30,
                                                52, 53, 54, 55, 56, 60, 63, 65, 67, 69, 71, 77,
                                                81, 83, 84, 85, 86, 87, 91, 92, 93, 94, 95, 97,
                                                103, 109, 111, 112, 113, 115, 118, 119, 120, 121, 123, 124,
                                                127, 131, 133, 135, 141, 143, 157, 159, 181, 183, 189, 191,
                                                195, 197, 199, 205, 207, 208, 212, 213, 214, 215, 216, 217,
                                                221, 222, 223, 224, 225, 227, 237, 239, 240, 241, 243, 244,
                                                247, 248, 249, 251, 252, 253};
    private static int [][] bitmap;
    private static int width;
    private static int height;
    //An image thinning method implemented(not yet) with KMM algorithm
    protected static BufferedImage thinImg(BufferedImage img){
        width = img.getWidth();
        height = img.getHeight();
        bitmap = new int [width][height];
        //mark all black pixels 1, white pixels 0
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if (basicImageIO.isBlack(img,x,y))
                    bitmap[x][y] = 1;
                else
                    bitmap[x][y] = 0;
            }
        }
        findNums();
        printBitmap(bitmap);
        return null;
    }
    
    private static void printBitmap(int[][] bitmap){
        //pritn all bitmap values
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                    System.out.print(String.valueOf(bitmap[x][y]));
            }
            System.out.print("\n");
        }
    }
    
    private static int[][] findNums(){
        //first iterate over all bits in bitmap (start from 1, end at n-1)
        for (int x = 1; x < width-1; x++){
            for ( int y = 1; y < height-1; y++){
                //if a bit is marked, check it's neighbors
                if ( bitmap[x][y] == 1){
                    
                    //find 3's with only corner sticking out
                    if (bitmap[x+1][y+1] == 0) bitmap[x][y] = 3;
                    else if (bitmap[x+1][y-1] == 0) bitmap[x][y] = 3;
                    else if (bitmap[x-1][y+1] == 0) bitmap[x][y] = 3;
                    else if (bitmap[x-1][y-1] == 0) bitmap[x][y] = 3;
                    //change it to 2 if it also has edges sticking out
                    if (bitmap[x+1][y] == 0) bitmap[x][y] = 2;
                    else if (bitmap[x-1][y] == 0) bitmap[x][y] = 2;
                    else if (bitmap[x][y+1] == 0) bitmap[x][y] = 2;
                    else if (bitmap[x][y-1] == 0) bitmap[x][y] = 2;
                    
                }
            }
        }
        
        return null;
    }
    
}
