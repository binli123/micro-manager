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
import ij.WindowManager;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import javax.swing.JOptionPane;
import mmcorej.CMMCore;
import org.json.JSONException;
import org.json.JSONObject;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import org.micromanager.utils.ImageUtils;
import org.micromanager.utils.MMException;
import org.micromanager.utils.ReportingUtils;
import org.micromanager.MMStudio;
import org.micromanager.imagedisplay.VirtualAcquisitionDisplay;
import org.micromanager.utils.MDUtils;
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
    
    public void setROI(){
        ImagePlus curImage = WindowManager.getCurrentImage();
      if (curImage == null) {
         return;
      }

      Roi roi = curImage.getRoi();
      
      try {
         if (roi == null) {
            // if there is no ROI, create one
            Rectangle r = curImage.getProcessor().getRoi();
            int iWidth = r.width;
            int iHeight = r.height;
            int iXROI = r.x;
            int iYROI = r.y;
            if (roi == null) {
               iWidth /= 2;
               iHeight /= 2;
               iXROI += iWidth / 2;
               iYROI += iHeight / 2;
            }

            curImage.setRoi(iXROI, iYROI, iWidth, iHeight);
            roi = curImage.getRoi();
         }

         Rectangle r = roi.getBounds();

         // If the image has ROI info attached to it, correct for the offsets.
         // Otherwise, assume the image was taken with the current camera ROI
         // (which is a horrendously buggy way to do things, but that was the
         // old behavior and I'm leaving it in case there are cases where it is
         // necessary).

         SetROI(r);

      } catch (MMScriptException e) {
      }
    }
 
    public void SetROI(Rectangle r) throws MMScriptException {
    try {
        app_.getCMMCore().setROI(r.x, r.y, r.width, r.height);
    } catch (Exception e) {
      }
   }
    
}
