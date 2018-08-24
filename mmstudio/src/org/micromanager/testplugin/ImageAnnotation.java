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
import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MMException;
import org.micromanager.utils.ReportingUtils;

public class ImageAnnotation {
    private final Studio app_;
    private long frameLengthMs = 100;
    private File tiffFile;
    private Datastore store;
    private SortedCoordsList coords_list;
    
    public ImageAnnotation(Studio studio_) {
        app_ = studio_;
    }
    
    public void showLowResImage(final File file) throws MMException {
        final TiffParser parser = new TiffParser(app_, frameLengthMs);
        parser.loadScrubbedData(file);
        store = parser.getDatastore();
        coords_list = parser.getCoordsList();
        tiffFile = file;
        store = app_.displays().show(parser.getImage());
    }
}
