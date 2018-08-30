/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.util.ArrayList;
import org.micromanager.Studio;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TemplateMatching {
    private final Studio app_;
    
    public TemplateMatching(Studio studio_) {
        app_ = studio_;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public Mat readImage(String filePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat matImage = imageCodecs.imread(filePath);
        Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_BGR2GRAY);
        return matImage;
    }
    
    public Mat resizeImage(Mat image, double scale) {
        Imgproc.resize(image, image, new Size(image.cols()*scale, image.rows()*scale));
        return image;
    }
    
    public Mat downSampling(Mat matImage, int rate) {
        Mat src = matImage;
        for (int i = 0; i<rate; i++) {
            Imgproc.pyrDown(src, matImage, new Size(src.cols()/2, src.rows()/2));    
        }     
        return matImage;
    }
    
    public Mat upSampling(Mat matImage, int rate) {
        Mat src = matImage;
        for (int i = 0; i<rate; i++) {
            Imgproc.pyrUp(src, matImage, new Size(src.cols()*2, src.rows()*2));    
        }     
        return matImage;
    }
    
    public Mat loadArrayAsMat(byte[][] array_) {
        int matHeight = array_.length;
        int matWidth = array_[0].length;
        Mat matImage = new Mat(matHeight, matWidth, CvType.CV_8UC1);
        for (int i=0; i<matHeight; i++) {
            matImage.put(i, 0, array_[i]);
        }
        return matImage;
    }
    
    public void findMatch() {
        double scale = 0.01;
        Mat image = new Mat();
        Mat kernel = new Mat();
        Mat imageEdge = new Mat();
        Mat kernelEdge = new Mat();
        Mat kernelRe = new Mat();
        Mat result = new Mat();
        double r = 0;
        MinMaxLocResult mmr;
        double maxValue = 0;
        Point maxLocation;
        image = readImage("C:/Users/MuSha/Desktop/Image Data/Images/Low resolution image.tif");
        kernel = readImage("C:/Users/MuSha/Desktop/Image Data/Images/High resolution image.tif");
        
        // detect edges
        Imgproc.Canny(image, imageEdge, 50, 200);
        
        for(scale=0.02; scale<0.2;){
            Imgproc.Canny(resizeImage(kernel, scale), kernelEdge, 50, 200);
            Imgproc.matchTemplate(imageEdge, kernelEdge, result, Imgproc.TM_CCOEFF);
            mmr = Core.minMaxLoc(result);
            
            if(scale==0.01) {
                maxValue = mmr.maxVal;
                maxLocation = mmr.maxLoc;
                r = scale;
            }           
            if(mmr.maxVal>maxValue) {
                maxValue = mmr.maxVal;
                maxLocation = mmr.maxLoc;
                r = scale;
            }
            scale = scale + 0.01;
        }
        r = scale;
    }
    
}
