/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import java.awt.Font;
import java.text.NumberFormat;
import java.util.prefs.Preferences;
import javax.swing.*;

import mmcorej.CMMCore;
import net.miginfocom.swing.MigLayout;
import org.micromanager.Studio;
import org.micromanager.api.ScriptInterface;
import org.micromanager.internal.utils.MMFrame;

public class TestPluginFrame extends MMFrame {
    
    private Studio studio_;
    private JTextField userText_;
    
    public TestPluginFrame(Studio studio) {
        super("Example Plugin GUI");
        studio_ = studio;
        setLayout(new MigLayout("fill, insets 2, gap 2, flowx"));

        JLabel title = new JLabel("I'm an example plugin!");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        add(title, "span, alignx center, wrap");
        
        // Create a text field for the user to customize their alerts.
        add(new JLabel("Alert text: "));
        userText_ = new JTextField(30);
        userText_.setText("Something happened!");
        add(userText_);
        
        pack();
    }

}