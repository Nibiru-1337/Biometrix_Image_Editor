/*
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
 *
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 *
 */
package biometrix_image_editor;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Nibiru
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label status;
    @FXML
    private ImageView imgView;
    @FXML
    private Slider sThreshold;
    @FXML
    private Button bThreshold;
    @FXML
    private Button bBrightness;
    @FXML
    private Slider sBrightness;

    
    private BufferedImage img;
    private BufferedImage originalImg;

    private boolean isThresholdOn = false;
    private boolean isBrightnessOn = false;
    
    @FXML
    private void convertGreyScale(ActionEvent event){
        System.out.println("Itreating over pixels...");
        for (int x = 0; x < img.getWidth(); x++){
            for (int y = 0; y < img.getHeight(); y++){
            int pixel = img.getRGB(x, y);
            int R = basicImageIO.getR(pixel);
            int G = basicImageIO.getG(pixel);
            int B = basicImageIO.getB(pixel);
            int mean = ( R + G + B )/ 3;
            pixel = basicImageIO.toRGB(mean,mean,mean);
            img.setRGB(x, y, pixel);
            }
        }
        setImgView();
        status.setText("Converted to greyscale!");
        //update originalImage after operation
        originalImg = basicImageIO.deepCopy(img);
    }
    @FXML
    private void convertColorInverse(ActionEvent event){
        System.out.println("Itreating over pixels...");
        for (int x = 0; x < img.getWidth(); x++){
            for (int y = 0; y < img.getHeight(); y++){
                int pixel = img.getRGB(x, y);
                int R = basicImageIO.getR(pixel);
                int G = basicImageIO.getG(pixel);
                int B = basicImageIO.getB(pixel);
                int newR = 255 - R;
                int newG = 255 - G;
                int newB = 255 - B;
                pixel = basicImageIO.toRGB(newR,newG,newB);
                img.setRGB(x, y, pixel);
            }
        }
        setImgView();
        status.setText("Completed color inversion!");
        //update originalImage after operation
        originalImg = basicImageIO.deepCopy(img);
    }
    @FXML
    private void activatedThreshold(ActionEvent event){
        isThresholdOn = !isThresholdOn;
        if (isThresholdOn){
            bThreshold.setText("Threshold:On");
            //sThreshold.setDisable(false);
            status.setText("Thresholding started!");

        }
        else{
            bThreshold.setText("Threshold:Off");
            //sThreshold.setDisable(true);
            status.setText("Thresholding stoped!");
            //update originalImage after operation
            //originalImg = basicImageIO.deepCopy(img);
        }
    }
    @FXML
    private void preformNormalization(ActionEvent event){
        // for nomralizing we need to determine minPix and maxPix
        int minRVal = 255;
        int maxRVal = 0;
        int minGVal = 255;
        int maxGVal = 0;
        int minBVal = 255;
        int maxBVal = 0;
        System.out.println("Looking for minPix and maxPix...");
        //first we find maximum and minumum values
        for (int x = 0; x < img.getWidth(); x++){
            for (int y = 0; y < img.getHeight(); y++){
                int pixel = img.getRGB(x, y);
                int R = basicImageIO.getR(pixel);
                int G = basicImageIO.getG(pixel);
                int B = basicImageIO.getB(pixel);
                if (R < minRVal) minRVal = R;
                if (R > maxRVal) maxRVal = R;
                if (G < minGVal) minGVal = G;
                if (G > maxGVal) maxGVal = G;
                if (B < minBVal) minBVal = B;
                if (B > maxBVal) maxBVal = B;
            }
        }
        //now we set the normalized values
        for (int x = 0; x < img.getWidth(); x++){
            for (int y = 0; y < img.getHeight(); y++){
                int pixel = img.getRGB(x, y);
                int R = basicImageIO.getR(pixel);
                int G = basicImageIO.getG(pixel);
                int B = basicImageIO.getB(pixel);
                R =  (int)((R - minRVal) * ((double)255/(maxRVal-minRVal)));
                G =  (int)((G - minGVal) * ((double)255/(maxGVal-minGVal)));
                B =  (int)((B - minBVal) * ((double)255/(maxBVal-minBVal)));
                if (R < 0) R = 0; if (R > 255) R = 255;
                if (G < 0) G = 0; if (G > 255) G = 255;
                if (B < 0) B = 0; if (B > 255) B = 255;
                pixel = basicImageIO.toRGB(R,G,B);
                img.setRGB(x, y, pixel);
            }
        }
        setImgView();
        //update originalImage after operation
        originalImg = basicImageIO.deepCopy(img);
        status.setText("Normalized!");
    }
    @FXML
    private void activatedBrightness(ActionEvent event){
        isBrightnessOn = !isBrightnessOn;
        if (isBrightnessOn){
            bBrightness.setText("Brightness:On");
            status.setText("Changing brightness!");
        }
        else{
            bBrightness.setText("Brightness:Off");
            status.setText("Stoped changing brightness!");
            //update originalImage after operation
            originalImg = basicImageIO.deepCopy(img);
        }
    }
    @FXML
    private void lowPassFilter(ActionEvent event){
        img = imageFilter.filter(originalImg, 2);
        status.setText("Low Pass applied!");
        setImgView();
        originalImg = basicImageIO.deepCopy(img);
    }
    @FXML
    private void highPassFilter(ActionEvent event){
        img = imageFilter.filter(originalImg, 1);
        setImgView();
        status.setText("High Pass applied!");
        originalImg = basicImageIO.deepCopy(img);
        
    }
    @FXML
    private void gaussFilter(ActionEvent event){
        img = imageFilter.filter(originalImg, 3);
        status.setText("Gauss applied!");
        setImgView();
        originalImg = basicImageIO.deepCopy(img);
    }
    @FXML
    private void sobelFilter(ActionEvent event){
        img = imageFilter.Sobel(originalImg);
        setImgView();
        originalImg = basicImageIO.deepCopy(img);
        status.setText("Sobel applied!");       
    }
    @FXML
    private void findPupil(ActionEvent event){
        img = pupilFinder.findPupil(img, originalImg);
        setImgView();
        img = originalImg;
    }
    @FXML
    private void doKMM(ActionEvent event){
        img = thinner.thinImgKMM(img);
        setImgView();
        basicImageIO.save2(img);
    }
    @FXML
    private void doK3M(ActionEvent event){
        img = thinner.thinImgK3M(img);
        setImgView();
        basicImageIO.save2(img);
    }
    @FXML
    private void loadImage(ActionEvent event) {
        //pass event because its used to determine if it should load default
        img = basicImageIO.load(status, event);
        setImgView();
        originalImg = basicImageIO.deepCopy(img);
    }
    @FXML
    private void saveImg(ActionEvent event){
        basicImageIO.save(img, status);
         
    }
    //sets ImageView to the current BufferedImage img
    private void setImgView(){
        Image image = SwingFXUtils.toFXImage(img, null);
        imgView.setImage(image);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //add listener to monitor Threshold slider change
        sThreshold.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if (isThresholdOn){
                //iterate over pixels
                for (int x = 0; x < originalImg.getWidth(); x++){
                    for (int y = 0; y < originalImg.getHeight(); y++){
                        int pixel = originalImg.getRGB(x, y);
                        int R = basicImageIO.getR(pixel);
                        int G = basicImageIO.getG(pixel);
                        int B = basicImageIO.getB(pixel);
                        int mean = ( R + G + B )/ 3;
                        if (mean < sThreshold.getValue())
                            pixel = basicImageIO.toRGB(0,0,0);
                        else
                            pixel = basicImageIO.toRGB(255,255,255);
                        img.setRGB(x,y,pixel);
                    }
                }
                //set ImgView so user sees changes as slider moves
                setImgView();
            }
        });

        //add listener to monitor Threshold slider change
        sBrightness.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if (isBrightnessOn){
                //iterate over pixels
                double b = sBrightness.getValue();
                for (int x = 0; x < originalImg.getWidth(); x++){
                    for (int y = 0; y < originalImg.getHeight(); y++){
                        int pixel = originalImg.getRGB(x, y);
                        int R = basicImageIO.getR(pixel);
                        int G = basicImageIO.getG(pixel);
                        int B = basicImageIO.getB(pixel);
                        R += b;
                        R = basicImageIO.checkBounds(R);
                        G += b;
                        G = basicImageIO.checkBounds(G);
                        B += b;
                        B = basicImageIO.checkBounds(B);
                        pixel = basicImageIO.toRGB(R,G,B);
                        img.setRGB(x, y, pixel);
                    }
                }
                //set ImgView so user sees changes as slider moves
                setImgView();
            }
        });
    }
        
}
    
