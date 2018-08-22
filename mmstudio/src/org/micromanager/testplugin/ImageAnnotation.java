/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

/**
 *
 * @author BIN LI
 */

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Rectangle;
import java.util.HashMap;

import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MMException;
import org.micromanager.utils.ReportingUtils;

public class ImageAnnotation {
    private final HashMap<String, ImageInfo> lowRes_;
    private final String BASEIMAGE = "base";
    
    public ImageAnnotation() {
        lowRes_ = new HashMap<String, ImageInfo>();
    }
    
    public void showLowResImage(String file) throws MMException {
        lowRes_.clear();
        if (!file.equals("")) {
         ij.io.Opener opener = new ij.io.Opener();
         ImagePlus ip = opener.openImage(file);
         if (ip == null) {
            throw new MMException("Failed to open file: " + file);
         }
         ImageInfo bg = new ImageInfo(ip); 
         lowRes_.put(BASEIMAGE, bg);
         lowRes_.put(makeKey(1, bg.getOriginalRoi()), bg);
        }
    }
    
    private String makeKey(int binning, Rectangle roi) {
    if (binning == 1 && (roi == null || roi.width == 0)) {
         return BASEIMAGE;
    }
        String key = binning + "-" + roi.x + "-" + roi.y + "-" + roi.width + "-" 
                + roi.height;
         return key;
   }
    
}
