/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.micromanager.StagePosition;
import org.micromanager.Studio;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;


public class CoordinatesTransformMatrix {
    private final Studio app_;
    private ArrayList<StagePosition> stagePosList_;
    
    public CoordinatesTransformMatrix(Studio studio) {
        app_ = studio;
    }
    
    public int[][] getKernelStartEnd(ArrayList<StagePosition> sp_, int Size_, 
            double[] stagePos_) {
        double oneStart=0;
        double oneEnd=0;
        double oneKernel=0;
        double kernelRealSize=0;
        int kernelRealPosition[][] = {{0, 0, 0}, {0, 0, 0}};
        int flag = 1;
        for (int i=0; i<sp_.size(); i++){
            StagePosition sp = sp_.get(i);
            if (sp.numAxes==2 && flag==1) {
                oneStart = (double) sp.x;
                flag = 2;
            } 
            if (sp.numAxes==2 && flag==2) {
                oneEnd = (double) sp.x;
                flag = 3;
            }
            if (flag==3) {
                break;
            }
        }
        oneKernel = oneEnd - oneStart;
        kernelRealSize = oneKernel * Size_;
        kernelRealPosition[0][0] = (int) (stagePos_[0] - kernelRealSize/2);
        kernelRealPosition[0][1] = (int) (stagePos_[1] - kernelRealSize/2);
        kernelRealPosition[1][0] = (int) (stagePos_[0] + kernelRealSize/2);
        kernelRealPosition[1][1] = (int) (stagePos_[1] + kernelRealSize/2);
        
        return kernelRealPosition;
    }
    
    public Mat getTransformMatrix(int[] roiCoordinates_, int[][] kernelReal_) {
        Mat afMat = new Mat();
        Point[] srcTri = new Point[3];
        srcTri[0] = new Point(roiCoordinates_[0], roiCoordinates_[1]);
        srcTri[1] = new Point(roiCoordinates_[1], roiCoordinates_[2]);
        srcTri[2] = new Point(roiCoordinates_[2], roiCoordinates_[3]);
        
        Point[] dstTri = new Point[3];
        dstTri[0] = new Point(kernelReal_[0][0], kernelReal_[0][1]);
        dstTri[1] = new Point(kernelReal_[0][1], kernelReal_[1][0]);
        dstTri[2] = new Point(kernelReal_[1][0], kernelReal_[1][1]);

        afMat = Imgproc.getAffineTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(srcTri));
        return afMat;
    }
}
