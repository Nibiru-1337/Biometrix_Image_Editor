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

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Nibiru
 */
public abstract class thinner {
    private static int [][] bm;
    private static int width;
    private static int height;
    //array look up tables for KMM algorithm
    private static final int[] deletionArray = {3, 5, 7, 12, 13, 14, 15, 20,
                                                21, 22, 23, 28, 29, 30, 31, 48,
                                                52, 53, 54, 55, 56, 60, 61, 62,
                                                63, 65, 67, 69, 71, 77, 79, 80,
                                                81, 83, 84, 85, 86, 87, 88, 89,
                                                91, 92, 93, 94, 95, 97, 99, 101,
                                                103, 109, 111, 112, 113, 115, 116, 117,
                                                118, 119, 120, 121, 123, 124, 125, 126,
                                                127, 131, 133, 135, 141, 143, 149, 151,
                                                157, 159, 181, 183, 189, 191, 192, 193,
                                                195, 197, 199, 205, 207, 208, 209, 211,
                                                212, 213, 214, 215, 216, 217, 219, 220,
                                                221, 222, 223, 224, 225, 227, 229, 231,
                                                237, 239, 240, 241, 243, 244, 245, 246,
                                                247, 248, 249, 251, 252, 253, 254, 255};
    private static final int[] fourArray = {3, 6, 12, 24, 48, 96, 192, 129, 
                                            7, 14, 28,  56, 112, 224, 193, 131,
                                            15, 30, 60, 120, 240, 225, 195, 135};
    //array look up tables for K3M algorithm
    private static final int[] A0 = {3, 6, 7, 12, 14, 15, 24, 28, 30, 31, 48, 56, 60,
                                    62, 63, 96, 112, 120, 124, 126, 127, 129, 131, 135,
                                    143, 159, 191, 192, 193, 195, 199, 207, 223, 224,
                                    225, 227, 231, 239, 240, 241, 243, 247, 248, 249,
                                    251, 252, 253, 254};
    private static final int[] A1 = {7, 14, 28, 56, 112, 131,  192  ,193, 224};
    private static final int[] A2 = {7, 14, 15, 28, 30, 56, 60, 112, 120, 131, 135,
                                    193, 195, 224, 225, 240};
    private static final int[] A3 = {7, 14, 15, 28, 30, 31, 56, 60, 62, 112, 120,
                                    124, 131, 135, 143, 193, 195, 199, 224, 225, 227,
                                    240, 241, 248};
    private static final int[] A4 = {7, 14, 15, 28, 30, 31, 56, 60, 62, 63, 112, 120,
                                    124, 126, 131, 135, 143, 159, 193, 195, 199, 207,
                                    224, 225, 227, 231, 240, 241, 243, 248, 249, 252};
    private static final int[] A5 = {7, 14, 15, 28, 30, 31, 56, 60, 62, 63, 112, 120,
                                    124, 126, 131, 135, 143, 159, 191, 193, 195, 199,
                                    207, 224, 225, 227, 231, 239, 240, 241, 243, 248,
                                    249, 251, 252, 254};
    private static final int[] A1pix = {3, 6, 7, 12, 14, 15, 24, 28, 30, 31, 48, 56,
                                        60, 62, 63, 96, 112, 120, 124, 126, 127, 129, 131,
                                        135, 143, 159, 191, 192, 193, 195, 199, 207, 223,
                                        224, 225, 227, 231, 239, 240, 241, 243, 247, 248,
                                        249, 251, 252, 253, 254};
    private static final int [][] phaseArray = new int[7][];
    private static boolean K3MrepeatFlag;
    private static boolean KMMrepeatFlag;
    
    /*********************K3M ALGORITHM METHODSS*******************************/
    //An image thinning method implemented with K3M algorithm
    protected static BufferedImage thinImgK3M(BufferedImage img){
        prepare(img);
        K3MrepeatFlag = false;
        phaseArray[0] = A0;
        phaseArray[1] = A1;
        phaseArray[2] = A2;
        phaseArray[3] = A3;
        phaseArray[4] = A4;
        phaseArray[5] = A5;
        phaseArray[6] = A1pix;

        for (int i = 0; i <= 7; i++){
            //i = 6 only used for checking if we need another iteration
            //printBitmap();
            if (i != 7){
                phase(i);
            }
            //when we are done with iterations check if there was a change
            //reset if there was a change ( i will be changed to 0 on next iteration start)
            if (K3MrepeatFlag && i == 7){
                i = -1;
                K3MrepeatFlag = false;
                unmarkBorders();
            }
        }
        //thin to 1 pixel width line
        //phase(6);
        
        return bitMapToImg(bm);
    }
    //phases for K3M algorithm
    protected static void phase(int phase){
        
        //iterate over all bits in bm (start from 1, end at n-1)
        for (int x = 1; x < width-1; x++){
            for ( int y = 1; y < height-1; y++){
                //check if we are in phase 0 and bit is part of image
                if ( phase == 0 && bm[y][x] == 1){
                    //change it to 2(marked as border bit) if its sum is in A0 look-up array
                    if ( contains(phaseArray[phase], sumNeighbors(bm, x, y)) ){
                        bm[y][x] = 2;
                    }
                }
                //check if we are not at phase 0 and we are at border bit
                //delete it accorting to look up phase we are in
                if ( phase != 0 && bm[y][x] == 2){
                    //change it to 2 if its sum is in appropriate look-up array
                    if ( contains(phaseArray[phase], sumNeighbors(bm, x, y)) ){
                        bm[y][x] = 0;
                        //there was a change so we need another iteration
                        if (!K3MrepeatFlag) K3MrepeatFlag = true;
                    }
                }
            }
        }
    }
    //unmark border pixels for next iteration of K3M
    protected static void unmarkBorders(){
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if ( bm[y][x] == 2)
                    bm[y][x] = 1;
            }
        }
    }
    
    /*********************KMM ALGORITHM METHODSS*******************************/
    //An image thinning method implemented with KMM algorithm
    protected static BufferedImage thinImgKMM(BufferedImage img){
        prepare(img);
        doTests();
        KMMrepeatFlag = true;
        while (KMMrepeatFlag){
            KMMrepeatFlag = false;
            find2and3();
            find4();
            delete4();

            for (int N = 2; N <= 3; N++){
                for (int x = 0; x < width; x++){
                    for( int y = 0; y < height; y++){
                        if (bm[y][x] == N){
                            if ( contains(deletionArray, sumNeighbors(bm, x, y)) ){
                                bm[y][x] = 0;
                                if (!KMMrepeatFlag) KMMrepeatFlag = true;
                            }
                            else bm[y][x] = 1;
                        }
                    }
                }
            }
        }
        return bitMapToImg(bm);
    }
    //find pixel values of 2 and 3
    private static void find2and3(){
        //first iterate over all bits in bm (start from 1, end at n-1)
        for (int x = 1; x < width-1; x++){
            for ( int y = 1; y < height-1; y++){
                //if a bit is marked, check it's neighbors
                if ( bm[y][x] == 1){
                    
                    //find 3's with corner sticking out
                    if (bm[y+1][x+1] == 0 ) bm[y][x] = 3;
                    else if (bm[y+1][x-1] == 0 ) bm[y][x] = 3;
                    else if (bm[y-1][x+1] == 0) bm[y][x] = 3;
                    else if (bm[y-1][x-1] == 0) bm[y][x] = 3;
                    
                    //change it to 2 if it also has edge sticking out
                    if (bm[y+1][x] == 0) bm[y][x] = 2;
                    else if (bm[y-1][x] == 0) bm[y][x] = 2;
                    else if (bm[y][x+1] == 0) bm[y][x] = 2;
                    else if (bm[y][x-1] == 0) bm[y][x] = 2;
                }
            }
            
        }
    }
    //find pixel values of 4
    private static void find4(){
        for (int x = 0; x < width; x++){
            for( int y = 0; y < height; y++){
                if (bm[y][x] == 2){
                    if ( contains(fourArray, sumNeighbors(bm, x, y)) ){
                    bm[y][x] = 4;
                    }
                }
            }
        }
    }
    //delete pixles with value 4
    private static void delete4(){
        for (int x = 0; x < width; x++){
            for ( int y = 0; y < height; y++){
                if ( bm[y][x] == 4 ){
                    bm[y][x] = 0;
                }
            }
        }
    }

    /*********************GENERIC METHODS**************************************/
    //prepare for thinning
    protected static void prepare(BufferedImage img){
        width = img.getWidth();
        height = img.getHeight();
        bm = new int [height][width];
        //mark all black pixels 1, white pixels 0
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if (basicImageIO.isBlack(img,x,y)){
                    bm[y][x] = 1;
                }
                else{
                    bm[y][x] = 0;
                }
            }
        }
    }    
    //print state of bitmap
    private static void printBitmap(){
        //pritn all bm values
        for (int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                    System.out.print(String.valueOf(bm[y][x]));
                    //System.out.print("["+String.valueOf(x)+","+String.valueOf(y)+"]" );

            }
            System.out.print("\n");
        }
    }
    //iterrate over neighbors of x,y and calculate weight
    private static int sumNeighbors(int[][] bm, int x, int y){
        int sum = 0, tmpX = 0 , tmpY = 0;
        int [][] weight = { {128,1,2},{64,0,4},{32,16,8} };
        //iterate over neighbors of pixel x,y
        for(int i = x-1; i < x+2; i++ ){
            for(int j = y-1; j < y+2; j++){
                if ( bm[j][i] != 0 ){
                    sum += weight[tmpY][tmpX];
                }
                tmpY++;
                
            }
            tmpX++;
            tmpY=0;
        }
        return sum;
    }
    //check if int array contains a value
    private static boolean contains(int[] haystack, int needle) {
        for (int i = 0; i < haystack.length; i++){
            if (haystack[i] == needle) 
                return true;
        }
        return false;
    }
    //do some unit tests for sumNeighbors
    private static boolean doTests(){
        //unit test of sumNeighbors
        int [][] test = { {0,0,0},{0,0,0},{0,0,0} };
        if ( sumNeighbors(test, 1, 1) == 0){
            System.out.println("PASS");
        }
        else {System.out.println("FAILED"); return false;}
        
        test[1][1]= 1;
        if ( sumNeighbors(test, 1, 1) == 0){
            System.out.println("PASS");
        }
        else {System.out.println("FAILED"); return false;}
        
        test[1][2]= 1;
        if ( sumNeighbors(test, 1, 1) == 4){
            System.out.println("PASS");
        }
        else {System.out.println("FAILED"); return false;}
        
        test[1][0]= 1;
        if ( sumNeighbors(test, 1, 1) == 68){
            System.out.println("PASS");
        }
        else {System.out.println("FAILED"); return false;}

        return true;
    }
    //convert bitmap to image
    private static BufferedImage bitMapToImg(int[][] bm){
        Color myWhite = new Color(255, 255, 255);
        int white = myWhite.getRGB();
        Color myBlack = new Color(0, 0, 0);
        int black = myBlack.getRGB();
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0;x < width; x++){
            for (int y = 0; y < height; y++){
                if (bm[y][x] == 0)
                    img.setRGB(x, y, white);
                else
                    img.setRGB(x, y, black);
            }
        }
        return img;
    }
}
