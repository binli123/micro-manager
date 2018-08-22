/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

public class ImageInfo extends ImagePlus {
    private final int binning_;
    private final Rectangle roi_;
    
   
    public ImageInfo(ImagePlus ip, int binning, Rectangle roi) {
        super(ip.getTitle(), ip.getProcessor());
        binning_ = binning;
         roi_ = roi;
    }
   
    public ImageInfo(ImagePlus ip) {
        this(ip, 1, new Rectangle(0, 0, ip.getWidth(), ip.getHeight()));
    }
   
    public ImageInfo(ImageProcessor ip) {
        super("", ip);
        binning_ = 1;
        roi_ = new Rectangle(0, 0, ip.getWidth(), ip.getHeight());
    }
   
    public int getBinning() {
        return binning_;
    }
   
     public Rectangle getOriginalRoi() {
        return roi_;
    }
}
