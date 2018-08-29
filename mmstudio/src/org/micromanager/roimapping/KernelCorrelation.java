/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import org.micromanager.Studio;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


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
}
