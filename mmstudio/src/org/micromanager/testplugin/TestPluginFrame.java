/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.micromanager.internal.utils.MMFrame;

public class TestPluginFrame extends MMFrame {
    
    private Studio studio_;
    private JTextField userText_;
    private JLabel imageInfoLabel_;
    
    public TestPluginFrame(Studio studio) {
        super("Example Plugin GUI");
        studio_ = studio;
        setLayout(new MigLayout("fill, insets 5, gap 5, flowx"));

        JLabel title = new JLabel("I'm an example plugin!");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, "span, alignx center, wrap");
        
        // Create a text field for the user to customize their alerts.
        add(new JLabel("Alert text: "));
        userText_ = new JTextField(30);
        userText_.setText("Something happened!");
        add(userText_);
        
        JButton alertButton = new JButton("Alert me!");
        // Clicking on this button will invoke the ActionListener, which in turn
        // will show a text alert to the user.
        alertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Use the contents of userText_ as the text.
                studio_.alerts().postAlert("Example Alert!",
                TestPluginFrame.class, userText_.getText());
            }
        });
        add(alertButton, "wrap");
        
        imageInfoLabel_ = new JLabel();
        add(imageInfoLabel_, "growx, split, span");
        JButton snapButton = new JButton("Snap Image");
        snapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Multiple images are returned only if there are multiple
            // cameras. We only care about the first image.
            List<Image> images = studio_.displays().getCurrentWindow().getDisplayedImages();
            Image aimage = images.get(0);
            Datastore store = studio_.displays().show(aimage);
            }
        });
        add(snapButton, "wrap");
        
        //retrieve the topmost DisplayWindow and then extract the Datastore that 
        //contains the data that the DisplayWindow presents
        
        pack();
    }

}