/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.TaggedImage;
import org.micromanager.Studio;
import org.micromanager.api.StagePosition;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.imagedisplay.DisplayWindow;

public class SnapKernel {
    private final Studio app_;
    private Datastore store;
    private DisplayWindow display;
    private List imgList;
    private ArrayList<StagePosition> stagePosList_;
    private String label_;
    private String defaultZStage_;
    private String defaultXYStage_;
    private int gridRow_ = 0;
    private int gridCol_ = 0;
    private Hashtable<String, String> properties_;
    private String xyStage;
    private String zStage;
    private double[] stagePos = {0, 0, 0};
    
    public SnapKernel(Studio studio_){
        app_ = studio_;
    }
    
    public void snapImage() {
        store  = app_.data().createRAMDatastore();
        try {
            app_.getCMMCore().snapImage();
            TaggedImage tmp = app_.getCMMCore().getTaggedImage();
            Image image = app_.data().convertTaggedImage(tmp);
            image = image.copyAtCoords(image.getCoords().copy().channel(0).build());
            store.putImage(image);
            store = app_.displays().show(image);
        } catch (Exception ex){
            Logger.getLogger(SnapKernel.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }
    
    public double[] getStagePosition() {
        xyStage = app_.getCMMCore().getXYStageDevice();
        zStage = app_.getCMMCore().getFocusDevice();
        try {
            stagePos[0] = app_.getCMMCore().getXPosition(xyStage);
            stagePos[1] = app_.getCMMCore().getYPosition(xyStage);
            stagePos[2] = app_.getCMMCore().getPosition(zStage);
        } catch (Exception ex) {
            Logger.getLogger(SnapKernel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stagePos;
    }

}
