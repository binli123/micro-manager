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
    
    public Mat resizeImage(Mat kernel, double scale) {
        Mat tmp = kernel.clone();
        Imgproc.resize(tmp, tmp, new Size(), scale, scale, Imgproc.INTER_LINEAR);
        return tmp;
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
    
    /*public ArrayList<Double> getMatchScales() {
        return scales;
    }*/
    
    public ArrayList<Double> findMatch(Mat image_, Mat kernel_) {
        Mat image = image_.clone();
        Mat kernel = kernel_.clone();
        Mat imageEdge = image_.clone();
        maxValues.clear();
        maxValues.clear();
        matchLocations.clear();
        maxLocation = null;
        double r = 0;
        maxValue = 0;
        ArrayList<Double> scales = new ArrayList<Double>();
        MinMaxLocResult mmr = null;
        //image = readImage("C:/Users/MuSha/Desktop/Image Data/Images/Low resolution image greyscale.tif");
        //kernel = readImage("C:/Users/MuSha/Desktop/Image Data/Images/High resolution image 02 greyscale.tif");
        int res = 0;
        boolean flag = false;
        
        // detect edges
        Imgproc.Canny(image, imageEdge, 5, 10);
        
        for(scale=0.01; scale<0.1;){
            // resize + median
            Mat kernelRe = resizeImage(kernel, scale);
            Mat kernelEdge = kernel_.clone();
            Imgproc.medianBlur(kernelRe, kernelRe, 3);
            Imgproc.Canny(kernelRe, kernelEdge, 5, 10);
            
            int result_rows = imageEdge.rows() - kernelEdge.rows() + 1;
            int result_cols = imageEdge.cols() - kernelEdge.cols() + 1;
            if(result_rows<=0 || result_cols<=0) {
               break; 
            }
            Mat result = new Mat(result_rows, result_cols, CvType.CV_8U);
            
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
                res = 0;
                flag = false;
            } else {
                res++;
                if (res == 3 && flag == false) {
                    matchLocations.add(maxLocation);
                    maxValues.add(maxValue);
                    scales.add(scale-0.003);
                    flag = true;
                    res = 0;
                }
            }
            scale = scale + 0.001;
        }
        r = scale;
        return scales;
    }
    
}
