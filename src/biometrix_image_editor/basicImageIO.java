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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javafx.event.ActionEvent;

/**
 *
 * @author Nibiru
 */
public class basicImageIO {
    //a single file chooser to use when user loads or saves an image
    private static final FileChooser fc = new FileChooser();
    //get red channel of a pixel
    protected static int getR(int in) {
        return (int)((in << 8) >> 24) & 0xff;
    }
    //get green channel of a pixel
    protected static int getG(int in) {
        return (int)((in << 16) >> 24) & 0xff;
    }
    //get blue channel of a pixel
    protected static int getB(int in) {
        return (int)((in << 24) >> 24) & 0xff;
    }
    //smush 3 integers(0-255) into 1 pixel(integer)
    protected static int toRGB(int r,int g,int b) {
        return (int)((((r << 8)|g) << 8)|b);
    }
    //check if a channel is within bounds
    protected static int checkBounds(int channel){
        if (channel < 0) return 0;
        if (channel > 255) return 255;
        else return channel;
    }
    //display a file chooser and return the loaded image
    protected static BufferedImage load(Label status, ActionEvent event){
        BufferedImage img = null;
        fc.setTitle("Choose image to load");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpeg","*.jpg","*.png");
        fc.getExtensionFilters().add(extFilter);
        File imgf;
        System.out.println(event.getSource().toString());
        if ("Button[id=loadFingerprint, styleClass=button]'Fingerprint'".equals(event.getSource().toString()))
            //TODO: PLACE IMG IN RESOUCE AND GET HANDLE FROM THERE
             imgf = new File("S:\\Nibiru\\Documents\\NetBeansProjects\\Biometrix_Image_Editor\\src\\biometrix_image_editor\\r1.bmp");
        else if ("Button[id=loadEye, styleClass=button]'Eye'".equals(event.getSource().toString()))
            //TODO: PLACE IMG IN RESOUCE AND GET HANDLE FROM THERE
             imgf = new File("S:\\Nibiru\\Documents\\NetBeansProjects\\Biometrix_Image_Editor\\src\\biometrix_image_editor\\eye.jpg");
        else
            imgf = fc.showOpenDialog(null);
        if (imgf == null){
            status.setText("Image not loaded!");
        }
        else{
                try {
                    img = ImageIO.read(imgf);
                    status.setText("Image loaded!");
                } catch (IOException ex) {
                    status.setText("Image load error");
                }
            }
        return img;
    }
    //display a file chooser and save image
    protected static void save(BufferedImage img, Label status){
        System.out.println("Saving image...");
        fc.setTitle("Save processed image"); 
        File file = fc.showSaveDialog(null);
        if(file != null){
            try{
                ImageIO.write(img, "png", file);
                status.setText("Image saved!");
            }
            catch(IOException e){
                status.setText("Image save error!");
            }
        }
    }
    //display a file chooser and save image
    protected static void save2(BufferedImage img){
        System.out.println("Saving image...");
        File file = new File("src/output.jpg");
        if(file != null){
            try{
                ImageIO.write(img, "png", file);
            }
            catch(IOException e){
            }
        }
    }
    //method to make a copy of Buffered Image
    protected static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    //function that determies if a pixel is black
    protected static boolean isBlack(BufferedImage image,int posX,int posY){
        int color = image.getRGB(posX,posY);
        return ((color & 0x00FFFFFF) == 0);
    }
}
