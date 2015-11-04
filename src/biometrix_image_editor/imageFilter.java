/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biometrix_image_editor;

import java.awt.image.BufferedImage;
import javafx.scene.control.Label;

/**
 *
 * @author Nibiru
 */
public abstract class imageFilter {

    private int pixel;
    //filter types
    private static final int [][]highpass = { {-1,-1,-1,},{-1,9,-1},{-1,-1,-1} };
    private static final int [][]lowpass = { {1,1,1}, {1,1,1}, {1,1,1} };
    private static final int [][]gaussian = { {1,4,1,}, {4,16,4}, {1,4,1} };    
    private static int [][]filter;
    
    protected static BufferedImage filter(BufferedImage img, int type){
        int newPixel;
        //for calculating new value of [x,y] pixel
        int sumR;
        int sumG;
        int sumB;
        //for place in filter
        int filterX;
        int filterY;
        //copy of image so we dont change pixels of picture we are working on
        BufferedImage originalCopy = basicImageIO.deepCopy(img);
        // configure multiplier for the filter we use
        double multiplier;
        switch(type){
        case 1:
                multiplier = 1; break;
        case 2:
                multiplier = 1.0/9.0; break;
        case 3:
                multiplier = 1.0/36.0; break;
        default:
                multiplier = 1; break;
        }
        System.out.println("Iterating over pixels...");
        //start iterating over pixels (we start from pixel 1,1 and end n-1,n-1)
        for (int x = 1; x < originalCopy.getWidth()-1; x++){
            for (int y = 1; y < originalCopy.getHeight()-1; y++){
                //reset place in filter
                filterX = 0;
                filterY = 0;
                //reset sum values
                sumR = 0;
                sumG = 0;
                sumB = 0;
                //iterate over neighbors of pixel x,y
                for(int i = x-1; i < x+2; i++ ){
                    for(int j = y-1; j < y+2; j++){
                        //calculate new value of pixel
                        int pixel = originalCopy.getRGB(i, j);
                        //add to total sum for new [x,y]pixel
                        sumR = sumR + rApplyFilter(type, pixel, filterX, filterY);
                        sumG = sumG + gApplyFilter(type, pixel, filterX, filterY);
                        sumB = sumB + bApplyFilter(type, pixel, filterX, filterY);
                        filterY++;
                    }
                    filterY = 0;
                    filterX++;
                }
                sumR = (int) (multiplier*sumR);
                sumG = (int) (multiplier*sumG);
                sumB = (int) (multiplier*sumB);
                
                newPixel = basicImageIO.toRGB(basicImageIO.checkBounds(sumR),basicImageIO.checkBounds(sumG),basicImageIO.checkBounds(sumB));
                img.setRGB(x, y, newPixel);
            }
        }        
        return img;
    }
    
    protected static BufferedImage Sobel(BufferedImage img){
        
        BufferedImage originalCopy = basicImageIO.deepCopy(img);
        
        //for storing neighbor pixels
        int[][]tmp = new int[3][3];
        //for storing new pixel value
        int nPixel;
        //for place in filter
        int filterX;
        int filterY;
        //for storing variables from Sobel formula
        int xR, yR, xG, yG, xB, yB, newR, newG, newB;
        for (int x = 1; x < originalCopy.getWidth()-1; x++){
            for (int y = 1; y < originalCopy.getHeight()-1; y++){
                //reset place in filter
                filterX = 0;
                filterY = 0;
                //iterate over neighbors of pixel x,y
                for(int i = x-1; i < x+2; i++ ){
                    for(int j = y-1; j < y+2; j++){
                        tmp[filterX][filterY] = originalCopy.getRGB(i, j);
                        filterY++;
                    }
                    filterX++;
                    filterY=0;
                }
                //apply Sobels forumla
                xR = ( basicImageIO.getR(tmp[2][0]) + 2 * basicImageIO.getR(tmp[2][1]) + basicImageIO.getR(tmp[2][2]) ) 
                        - ( basicImageIO.getR(tmp[0][0]) + 2 * basicImageIO.getR(tmp[0][1]) + basicImageIO.getR(tmp[0][2]) );
                
                xG = ( basicImageIO.getG(tmp[2][0]) + 2 * basicImageIO.getG(tmp[2][1]) + basicImageIO.getG(tmp[2][2]) ) 
                        - ( basicImageIO.getG(tmp[0][0]) + 2 * basicImageIO.getG(tmp[0][1]) + basicImageIO.getG(tmp[0][2]) );     
                
                xB = ( basicImageIO.getB(tmp[2][0]) + 2 * basicImageIO.getB(tmp[2][1]) + basicImageIO.getB(tmp[2][2]) ) 
                        - ( basicImageIO.getB(tmp[0][0]) + 2 * basicImageIO.getB(tmp[0][1]) + basicImageIO.getB(tmp[0][2]) );
                
                yR = ( basicImageIO.getR(tmp[0][2]) + 2 * basicImageIO.getR(tmp[1][2]) + basicImageIO.getR(tmp[2][2]) ) 
                        - ( basicImageIO.getR(tmp[0][0]) + 2 * basicImageIO.getR(tmp[1][0]) + basicImageIO.getR(tmp[2][0]) );
                
                yG = ( basicImageIO.getG(tmp[0][2]) + 2 * basicImageIO.getG(tmp[1][2]) + basicImageIO.getG(tmp[2][2]) ) 
                        - ( basicImageIO.getG(tmp[0][0]) + 2 * basicImageIO.getG(tmp[1][0]) + basicImageIO.getG(tmp[2][0]) );               
                
                yB = ( basicImageIO.getB(tmp[0][2]) + 2 * basicImageIO.getB(tmp[1][2]) + basicImageIO.getB(tmp[2][2]) ) 
                        - ( basicImageIO.getB(tmp[0][0]) + 2 * basicImageIO.getB(tmp[1][0]) + basicImageIO.getB(tmp[2][0]) );        
                
                newR = (int) Math.sqrt(Math.pow(xR, 2) + Math.pow(yR, 2) );
                newG = (int) Math.sqrt(Math.pow(xG, 2) + Math.pow(yG, 2) );
                newB = (int) Math.sqrt(Math.pow(xB, 2) + Math.pow(yB, 2) );
                
                nPixel = basicImageIO.toRGB(basicImageIO.checkBounds(newR),basicImageIO.checkBounds(newG),basicImageIO.checkBounds(newB));
                img.setRGB(x, y, nPixel);
            }
        }        
        
        return img;
    }
    
    //functions to apply filter types
    private static int rApplyFilter(int type, int pixel, int i, int j){
        // configure which filter we use
        switch(type){
        case 1:
                filter = highpass; break;
        case 2:
                filter = lowpass; break;
        case 3:
                filter = gaussian; break;
        default:
                filter = highpass; break;
        }
        //get R channel
        int R = basicImageIO.getR(pixel);
        //get product
        int productR = R * filter[i][j];
        return productR;
    }    
    private static int gApplyFilter(int type, int pixel, int i, int j){
        // configure which filter we use
        switch(type){
        case 1:
                filter = highpass; break;
        case 2:
                filter = lowpass; break;
        case 3:
                filter = gaussian; break;
        default:
                filter = highpass; break;
        }
        //get G channel
        int G = basicImageIO.getG(pixel);
        //get product
        int productG = G * filter[i][j];
        return productG;
    }
    private static int bApplyFilter(int type, int pixel, int i, int j){
        // configure which filter we use
        switch(type){
        case 1:
                filter = highpass; break;
        case 2:
                filter = lowpass; break;
        case 3:
                filter = gaussian; break;
        default:
                filter = highpass; break;
        }
        //get B channel
        int B = basicImageIO.getB(pixel);
        //get product
        int productB = B * filter[i][j];
        return productB;
    }
}
