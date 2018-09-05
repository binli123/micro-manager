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


public class CoordinatesTransformMatrix {
    private final Studio app_;
    private ArrayList<StagePosition> stagePosList_;
    
    public CoordinatesTransformMatrix(Studio studio) {
        app_ = studio;
    }
    
    public double[][] getKernelStartEnd(ArrayList<StagePosition> sp_, int Size_, 
            double[] stagePos_) {
        double oneStart=0;
        double oneEnd=0;
        double oneKernel=0;
        double kernelRealSize=0;
        double kernelRealPosition[][] = {{0, 0, 0}, {0, 0, 0}};
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
        kernelRealPosition[0][0] = stagePos_[0] - kernelRealSize/2;
        kernelRealPosition[0][1] = stagePos_[1] - kernelRealSize/2;
        kernelRealPosition[1][0] = stagePos_[0] + kernelRealSize/2;
        kernelRealPosition[1][1] = stagePos_[1] + kernelRealSize/2;
        
        return kernelRealPosition;
    }
    
    public void getTransformMatrix(double[] roiCo_, double[] kernelReal_) {
        
    }
}
