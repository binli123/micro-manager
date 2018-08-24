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
import java.io.File;
import java.util.HashMap;
import mmcorej.CMMCore;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MMException;
import org.micromanager.utils.ReportingUtils;
import org.micromanager.MMStudio;
import org.micromanager.utils.MMScriptException;

public class ImageAnnotation {
    private final Studio app_;
    private long frameLengthMs = 100;
    private File tiffFile;
    private Datastore store;
    private SortedCoordsList coordsList;
    private Rectangle roi;
    
    public ImageAnnotation(Studio studio_) {
        app_ = studio_;
    }
    
    public void showLowResImage(final File file) throws MMException {
        final TiffParser parser = new TiffParser(app_, frameLengthMs);
        parser.loadScrubbedData(file);
        store = parser.getDatastore();
        coordsList = parser.getCoordsList();
        tiffFile = file;
        store = app_.displays().show(parser.getImage());
    }
    
    public Rectangle getAnnotationROI() throws MMScriptException {
        roi = getROI();
        return roi;
    }

    public Rectangle getROI() throws MMScriptException {
        // ROI values are given as x,y,w,h in individual one-member arrays (pointers in C++):
        int[][] a = new int[4][1];
        try {
        app_.getCMMCore().getROI(a[0], a[1], a[2], a[3]);
        } catch (Exception e) {
            throw new MMScriptException(e.getMessage());
        }
        // Return as a single array with x,y,w,h:
         return new Rectangle(a[0][0], a[1][0], a[2][0], a[3][0]);
    }
    
    
}
