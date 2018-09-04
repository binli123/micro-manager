/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.micromanager.Studio;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class KernelCorrelation {
    private final Studio app_;
    
    public KernelCorrelation(Studio studio_) {
        app_ = studio_;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
    
    public BufferedImage matToBufferedImage(Mat bgr) {
        int width = bgr.width();
        int height = bgr.height();
        BufferedImage image;
        WritableRaster raster;
        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        raster = image.getRaster();

        byte[] px = new byte[1];

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                bgr.get(y,x,px);
                raster.setSample(x, y, 0, px[0]);
            }            
        }
        return image;
    }
    
    public MatOfKeyPoint getKeyPoints(Mat mat) {
        FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
        MatOfKeyPoint mkp = new MatOfKeyPoint();
        fd.detect(mat, mkp);
        return mkp;
    }
    
    public Mat getFeatures(Mat mat) {
        Mat desc = new Mat();
        MatOfKeyPoint mkp = getKeyPoints(mat);
        DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.ORB);
        de.compute(mat, mkp, desc);
        return desc;        
    }
    
    public Mat readImage(String filePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat matImage = imageCodecs.imread(filePath);
        Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_BGR2GRAY);
        return matImage;
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
    
    public Mat resizeImage(Mat src, Mat dst, double scale) {
        Imgproc.resize(src, dst, new Size(src.cols()*scale, src.rows()*scale));
        return dst;
    }
    
    public Mat cropImageFromKernel(Mat image, Mat kernel, double scale, Point position) {
        Size sz = kernel.size();
        Mat imCrop = new Mat();
        int imageWidth = (int) (sz.width * scale);
        int imageHeight = (int) (sz.height * scale);
        int startX = (int) (position.x - 50);
        int startY = (int) (position.y - 50);
        int width = (int) (50 + imageWidth);
        int height = (int) (50 + imageHeight);
        Rect rectCrop = new Rect(startX, startY, width, height);
        imCrop = image.submat(rectCrop);
        return imCrop;
    }
    
    public LinkedList<DMatch> findMatch(double scale, Mat kernel_, Mat image_) {
        Mat image = new Mat();
        Mat kernel = new Mat();
        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        image = readImage("C:/Users/MuSha/Desktop/Image Data/Images/Low resolution image greyscale crop.tif");
        kernel = readImage("C:/Users/MuSha/Desktop/Image Data/Images/High resolution image 01 greyscale.tif");
        // resampling
        // image = downSampling(image, 0);
        // kernel = upSampling(kernel, 0);
        
        Mat kernelRe = new Mat();
        kernelRe = resizeImage(kernel,kernelRe, scale);
        Imgproc.medianBlur(kernelRe, kernelRe, 3);
        
        // contrast
        //Imgproc.equalizeHist(image, image);
        //Imgproc.equalizeHist(kernel, kernel);

        // morphology
        Mat morpElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, 
                 new Size(3, 3));
        Imgproc.dilate(image, image, morpElement);
        Imgproc.erode(image, image, morpElement);
        
        // filtering
        Imgproc.medianBlur(image, image, 5);
        
        // Mat destination = new Mat(image.rows(),image.cols(),image.type());
        // Imgproc.GaussianBlur(image, destination, new Size(0,0), 10);
        // Core.addWeighted(image, 1.5, destination, -0.5, 0, destination);
        // image = destination;
        
        // detect edges in kernel
        // Imgproc.Canny(image, image, 50, 200);
        // Imgproc.Canny(kernel, kernel, 50, 200);
        
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat desc1 = new Mat();
        Mat desc2 = new Mat();
        keypoints1 = getKeyPoints(image);
        keypoints2 = getKeyPoints(kernelRe);
        desc1 = getFeatures(image);
        desc2 = getFeatures(kernelRe); 
        matcher.knnMatch(desc1, desc2, matches, 2);
        
        
        for (Iterator<MatOfDMatch> iterator = matches.iterator(); iterator.hasNext();) {
        MatOfDMatch matOfDMatch = (MatOfDMatch) iterator.next();
            if (matOfDMatch.toArray()[0].distance / matOfDMatch.toArray()[1].distance < 0.8) {
                good_matches.add(matOfDMatch.toArray()[0]);
            }               
        }
        
        List<Point> pts1 = new ArrayList<Point>();
        List<Point> pts2 = new ArrayList<Point>();
        for(int i = 0; i<good_matches.size(); i++){
            pts1.add(keypoints1.toList().get(good_matches.get(i).queryIdx).pt);
            pts2.add(keypoints2.toList().get(good_matches.get(i).trainIdx).pt);     
        }
        
        Mat outputMask = new Mat();
        MatOfPoint2f pts1Mat = new MatOfPoint2f();
        pts1Mat.fromList(pts1);
        MatOfPoint2f pts2Mat = new MatOfPoint2f();
        pts2Mat.fromList(pts2);
        
        Mat Homog = Calib3d.findHomography(pts1Mat, pts2Mat, Calib3d.RANSAC, 15, outputMask, 2000, 0.995);
        
        LinkedList<DMatch> better_matches = new LinkedList<DMatch>();
        for (int i = 0; i < good_matches.size(); i++) {
            if (outputMask.get(i, 0)[0] != 0.0) {
                better_matches.add(good_matches.get(i));
            }
        }
        
        Mat outputImg = new Mat();
        MatOfDMatch better_matches_mat = new MatOfDMatch();
        better_matches_mat.fromList(better_matches);
        Features2d.drawMatches(image, keypoints1, kernelRe, keypoints2, better_matches_mat, outputImg);        
        Imgcodecs.imwrite("C:/Users/MuSha/Desktop/Image Data/Images/result1.tif", outputImg);        
        return good_matches;
    }   
}
