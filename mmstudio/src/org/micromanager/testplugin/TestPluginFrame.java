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
import java.util.prefs.Preferences;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import org.micromanager.MMStudio;
import org.micromanager.Studio;
import org.micromanager.utils.FileDialogs;
import org.micromanager.utils.MMDialog;
import org.micromanager.utils.*;

public class TestPluginFrame extends MMDialog {
    
    private Studio studio_;
    private JTextField firstUserText_;
    private JTextField secondUserText_;
    private JLabel imageInfoLabel_;
    private MMDialog mscPluginWindow;
    private final Preferences prefs_;
    



    private final String[] IMAGESUFFIXES = {"tif", "tiff", "jpg", "png"};
    //private static File lowResImage;
    private File firstConfig;
    private File secondConfig;

    public TestPluginFrame(Studio studio) {
        super("Example Plugin GUI");
        studio_ = studio;
        setLayout(new MigLayout("fill, insets 2, gap 2, flowx"));
        prefs_ = this.getPrefsNode();

        JLabel title = new JLabel("Config select");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, "span, alignx center, wrap");
        
        // Display the path to the low resolution image
        add(new JLabel("First config file: "));
        
        firstUserText_ = new JTextField(30);
        firstUserText_.setText("First Config File");
        add(firstUserText_);
        
        // Create load button for the low resolution image
        JButton firstButton = new JButton(" ... ");
        // Clicking on this button will invoke the ActionListener, which in turn
        // will ask the user to select a image.
        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // 
                firstConfig = loadConfiguration(firstUserText_);
                //firstUserText_.setText("First file");
                /*
                firstConfig = FileDialogs.openFile(mscPluginWindow, 
                        "Config file", 
                        new FileDialogs.FileType("MMAcq", ".cfg", 
                                firstConfigFileName_, true, IMAGESUFFIXES));
                if (firstConfig != null){
                    processLowResImage(firstConfig.getAbsolutePath());
                    firstUserText_.setText(firstConfigFileName_);
                }
                */
            }
        });
        add(firstButton, "wrap");
        
        // Add second config file 
                // Display the path to the low resolution image
        add(new JLabel("Second config file: "));
        
        secondUserText_ = new JTextField(30);
        secondUserText_.setText("Second Config File");
        add(secondUserText_);
        
        // Create load button for the low resolution image
        JButton secondButton = new JButton(" ... ");
        // Clicking on this button will invoke the ActionListener, which in turn
        // will ask the user to select a image.
        secondButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondConfig = loadConfiguration(secondUserText_);
            }
        });
        add(secondButton, "wrap");
        
        imageInfoLabel_ = new JLabel();
        add(imageInfoLabel_, "growx, split, span");
        
        JButton config1Button = new JButton("Config 1");
        config1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseConfig(firstConfig);
            }
        });
        add(config1Button);
        
        JButton config2Button = new JButton("Config 2");
        config2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseConfig(secondConfig);
            }
        });
        add(config2Button);
        
        /*
        JButton annotateButton = new JButton("Snap Image");
        annotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondUserText_.setText("test");
            }
        });
        add(annotateButton, "wrap");
        */
        
        //retrieve the topmost DisplayWindow and then extract the Datastore that 
        //contains the data that the DisplayWindow presents
        
        pack();
    }
    
    /*
    public String processLowResImage(String fileName) {
        if (EMPTY_FILENAME_INDICATOR.equals(fileName)) {
            fileName = "";
        }
        lowResFileName_ = fileName;
        prefs_.put(LOWRESIMAGENAME, lowResFileName_);
        return fileName;
    }  
    */
    
    private File loadConfiguration(JTextField userText_) {
        File configFile = FileDialogs.openFile(MMStudio.getFrame(), 
            "Load a config file", MMStudio.MM_CONFIG_FILE);
        userText_.setText(configFile.getName());
        
        ReportingUtils.logMessage("Read in " + configFile.getName() + " as config file");
        return configFile;
      /*
      if (configFile != null) {
         try{
            studio_.getCMMCore().loadSystemConfiguration(configFile.getAbsolutePath());
            userText_.setText(configFile.getName());
         }
         catch(Exception e){
             System.out.println(e.getStackTrace());
         }
      }
*/
   }
    
    private void chooseConfig(File configFile){
         if (configFile != null) {
         try{
            studio_.getCMMCore().loadSystemConfiguration(configFile.getAbsolutePath());
            ReportingUtils.logMessage("Choose " + configFile.getName() + " as config file");
         }
         catch(Exception e){
             System.out.println(e.getStackTrace());
         }
      }
    }
}

