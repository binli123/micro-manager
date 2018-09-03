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
    double scale = 0.01;
    private ArrayList<Double> scales = new ArrayList<Double>();
    private double maxValue = 0;
    private Point maxLocation = null;
    private ArrayList<Double> maxValues = new ArrayList<Double>();
    private ArrayList<Point> matchLocations = new ArrayList();
    
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
    
    public Mat resizeImage(Mat scr, Mat dst, double scale) {
        Imgproc.resize(scr, dst, new Size(scr.cols()*scale, scr.rows()*scale));
        return dst;
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
    
    public ArrayList<Point> getMatchPositions() {
        return matchLocations;
    }
    
    public ArrayList<Double> getMatchScales() {
        return scales;
    }
    
    public void findMatch(Mat image_, Mat kernel_) {
        Mat image = new Mat();
        Mat kernel = new Mat();
        Mat imageEdge = new Mat();
        Mat kernelEdge = new Mat();;
        Mat result = new Mat();
        double r = 0;
        MinMaxLocResult mmr;
        // image = readImage("C:/Users/MuSha/Desktop/Image Data/Images/Low resolution image.tif");
        // kernel = readImage("C:/Users/MuSha/Desktop/Image Data/Images/High resolution image 01.tif");
        image = image_;
        kernel = kernel_;
        int res = 0;
        
        // detect edges
        Imgproc.Canny(image, imageEdge, 5, 10);
        
        for(scale=0.01; scale<0.1;){
            Mat kernelRe = new Mat();
            kernelRe = resizeImage(kernel,kernelRe, scale);
            Imgproc.Canny(kernelRe, kernelEdge, 5, 10);
            Imgproc.matchTemplate(imageEdge, kernelEdge, result, Imgproc.TM_CCOEFF);
            mmr = Core.minMaxLoc(result);
            
            if(scale==0.1) {
                maxValue = mmr.maxVal;
                maxLocation = mmr.maxLoc;
                r = scale;
            }           
            if(mmr.maxVal>maxValue) {
                maxValue = mmr.maxVal;
                maxLocation = mmr.maxLoc;
                r = scale;
                res = 0;
            } else {
                res++;
                if (res == 5) {
                    matchLocations.add(maxLocation);
                    maxValues.add(maxValue);
                    scales.add(scale-0.005);
                }
            }
            scale = scale + 0.001;
        }
        r = scale;
    }
    
}
