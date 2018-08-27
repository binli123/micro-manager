/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;

import mmcorej.CMMCore;
import net.miginfocom.swing.MigLayout;
import org.micromanager.Studio;
import org.micromanager.api.ScriptInterface;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Coords;
import org.micromanager.display.DisplayWindow;
import mmcorej.TaggedImage;

import org.micromanager.internal.utils.MMFrame;
import org.micromanager.utils.FileDialogs;
import org.micromanager.utils.MMDialog;
import org.micromanager.utils.MMException;
import org.micromanager.utils.MMScriptException;
import org.micromanager.utils.ReportingUtils;

public class RoiMappingFrame extends MMDialog {
    
    private Studio studio_;
    private JTextField userText_;
    private JTextField coordinatesText_;
    private MMDialog mscPluginWindow;
    private final Preferences prefs_;
    
    private static final String LOWRESIMAGENAME = "Low resolution image";
    private static final String EMPTY_FILENAME_INDICATOR = "None";
    private String lowResFileName_;
    private final String[] IMAGESUFFIXES = {"tif", "tiff", "jpg", "png"};
    private static File lowResImage;
    private int[] roiCoordinates_ = {0, 0, 0, 0};
    private Rectangle getCoordinates;
    private static String ROICOORDINATES = "(0, 0) (0, 0)";
    
    public RoiMappingFrame(Studio studio) {
        super("Example Plugin GUI");
        studio_ = studio;
        setLayout(new MigLayout("fill, insets 2, gap 2, flowx"));
        prefs_ = this.getPrefsNode();
        ImageAnnotation ia = new ImageAnnotation(studio_);

        JLabel title = new JLabel("ROIs mapping");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, "span, alignx center, wrap");
        
        // Display the path to the low resolution image
        add(new JLabel("Image path: "));
        userText_ = new JTextField(30);
        userText_.setText(LOWRESIMAGENAME);
        add(userText_, "newline");
           
        // Create load button for the low resolution image
        JButton lowResButton = new JButton(" ... ");
        // Clicking on this button will invoke the ActionListener, which in turn
        // will ask the user to select a image.
        lowResButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // 
                lowResImage = FileDialogs.openFile(mscPluginWindow, 
                        "Low resolution image", 
                        new FileDialogs.FileType("MMAcq", "Low resolution image", 
                        lowResFileName_, true, IMAGESUFFIXES));
                if (lowResImage != null){
                    processLowResImage(lowResImage.getAbsolutePath());
                    userText_.setText(lowResFileName_);
                }
            }
        });
        add(lowResButton);
        

        //loadImageLabel_ = new JLabel();
        //add(loadImageLabel_);
        JButton loadImageButton = new JButton("Display Image");
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display the selected image
                 try {
                    ia.showLowResImage(lowResImage);
                } catch (MMException ex) {
                    ReportingUtils.showError(ex, "Failed to open low resolution image");
                }
            }
        });
        add(loadImageButton, "wrap");
        
        add(new JLabel("ROI Coordinates: "));
        coordinatesText_ = new JTextField(30);
        coordinatesText_.setText(ROICOORDINATES);
        add(coordinatesText_, "newline");
        
        //annoteImageLabel_ = new JLabel();
        //add(annoteImageLabel_, "growx, split, span");
        JButton annotateButton = new JButton("Annotate Image");
        annotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Set ROI
                //Record the coordinates of ROI
                ia.setROI();
                try {
                    roiCoordinates_[0] = ia.getAnnotationROI().x;
                    roiCoordinates_[1] = ia.getAnnotationROI().y;
                    roiCoordinates_[2] = ia.getAnnotationROI().x + 
                            ia.getAnnotationROI().width;
                    roiCoordinates_[3] = ia.getAnnotationROI().y + 
                            ia.getAnnotationROI().height;;
                } catch (MMScriptException ex) {
                    //ReportingUtils.showError(ex, "Failed to annotate image");
                } catch (Exception ex) {
                    Logger.getLogger(RoiMappingFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                ROICOORDINATES = String.format("(%d, %d) (%d, %d)", 
                        roiCoordinates_[0], roiCoordinates_[1], 
                        roiCoordinates_[2], roiCoordinates_[3]);
                coordinatesText_.setText(ROICOORDINATES);
            }
        });
        add(annotateButton, "wrap, span 2");
        
        
        //retrieve the topmost DisplayWindow and then extract the Datastore that 
        //contains the data that the DisplayWindow presents
        
        pack();
    }
    
    public String processLowResImage(String fileName) {
        if (EMPTY_FILENAME_INDICATOR.equals(fileName)) {
            fileName = "";
        }
        lowResFileName_ = fileName;
        prefs_.put(LOWRESIMAGENAME, lowResFileName_);
        return fileName;
    }  
 
}

