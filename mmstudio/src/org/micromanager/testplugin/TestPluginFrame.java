/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
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
import org.micromanager.utils.ReportingUtils;

public class TestPluginFrame extends MMDialog {
    
    private Studio studio_;
    private JTextField userText_;
    private JLabel imageInfoLabel_;
    private MMDialog mscPluginWindow;
    private final Preferences prefs_;
    
    private static final String LOWRESIMAGENAME = "Low resolution image";
    private static final String EMPTY_FILENAME_INDICATOR = "None";
    private String lowResFileName_;
    private final String[] IMAGESUFFIXES = {"tif", "tiff", "jpg", "png"};
    private static File lowResImage;
    
    public TestPluginFrame(Studio studio) {
        super("Example Plugin GUI");
        studio_ = studio;
        setLayout(new MigLayout("fill, insets 2, gap 2, flowx"));
        prefs_ = this.getPrefsNode();

        JLabel title = new JLabel("ROIs mapping");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, "span, alignx center, wrap");
        
        // Display the path to the low resolution image
        add(new JLabel("Low resolution image: "));
        userText_ = new JTextField(30);
        userText_.setText(LOWRESIMAGENAME);
        add(userText_);
        
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
        add(lowResButton, "wrap");
        
        imageInfoLabel_ = new JLabel();
        add(imageInfoLabel_, "growx, split, span");
        JButton annotateButton = new JButton("Snap Image");
        annotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Display the selected image
                ImageAnnotation ia = new ImageAnnotation();
                 try {
                    ia.showLowResImage(lowResFileName_);
                } catch (MMException ex) {
                    ReportingUtils.showError(ex, "Failed to open low resolution image");
                }
                 
                
                List<Image> images = studio_.displays().getCurrentWindow().getDisplayedImages();
                Image aimage = images.get(0);
                Datastore store = studio_.displays().show(aimage);  
            }
        });
        add(annotateButton, "wrap");
        
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

