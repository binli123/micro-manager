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
import org.micromanager.StagePosition;
import org.micromanager.Studio;
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
    private long imHeight;
    private long imWidth;
    private double pixelSize;
    
    public SnapKernel(Studio studio_){
        app_ = studio_;
        stagePosList_ = new ArrayList<StagePosition>();
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
    
    public void generatePositions(int Size) {
        double imFieldHeight;
        double imFieldWidth;
        double[] start = {0, 0};
        double[] end = {0, 0};
        Size = 3;
        imHeight = app_.getCMMCore().getImageHeight();
        imWidth = app_.getCMMCore().getImageWidth();
        pixelSize = app_.getCMMCore().getPixelSizeUm();
        stagePos = getStagePosition();
        xyStage = app_.getCMMCore().getXYStageDevice();
        zStage = app_.getCMMCore().getFocusDevice();
        imFieldHeight = imHeight * pixelSize;
        start[0] = stagePos[0] - (imFieldHeight/2)*(Size - 1);
        start[1] = stagePos[1] - (imFieldHeight/2)*(Size - 1);
        end[0] = stagePos[0] + (imFieldHeight/2)*(Size - 1);
        end[1] = stagePos[1] + (imFieldHeight/2)*(Size - 1);
        for (int i=0; i<Size; i++) {
            for (int j=0; j<Size; j++) {
                addPosition(xyStage, 
                        start[0] + j*imFieldHeight, start[1] + i*imFieldHeight,
                        zStage, stagePos[2]);
            }
        }    
    }
    
    public void addPosition(String xyStage, double x, double y, String zStage, double z) {
        StagePosition xyPos = new StagePosition();
        xyPos.numAxes = 2;
        xyPos.stageName = xyStage;
        xyPos.x = x;
        xyPos.y = y;
        defaultXYStage_ = xyStage; 
        stagePosList_.add(xyPos);

        
      
        // create and add z position
        StagePosition zPos = new StagePosition();
        zPos.numAxes = 1;
        zPos.stageName = zStage;
        zPos.z = z;
        defaultZStage_ = zStage;
        stagePosList_.add(zPos);
    }
    
    public void goToPosition(ArrayList<StagePosition> sp_, int i) {
        StagePosition sp = sp_.get(i);
        try {
            if (sp.numAxes == 1) {
                app_.getCMMCore().setPosition(sp.stageName, sp.z);
            } else if (sp.numAxes == 2) {
                app_.getCMMCore().setXYPosition(sp.stageName, sp.x, sp.y);
            }
        } catch (Exception ex) {
            Logger.getLogger(SnapKernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void add(StagePosition sp) {
        stagePosList_.add(sp);
    }

}
