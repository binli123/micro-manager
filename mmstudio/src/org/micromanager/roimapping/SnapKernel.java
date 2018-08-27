/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.TaggedImage;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.imagedisplay.DisplayWindow;

public class SnapKernel {
    private final Studio app_;
    private Datastore store;
    private DisplayWindow display;
    private List imgList;
    
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

}
