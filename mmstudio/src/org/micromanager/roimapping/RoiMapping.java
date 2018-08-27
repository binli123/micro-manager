/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.roimapping;

import org.micromanager.MMStudio;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = MenuPlugin.class)
public class RoiMapping implements SciJavaPlugin, MenuPlugin {
   private Studio studio_;
   private RoiMappingFrame frame_;

   /**
    * This method receives the Studio object, which is the gateway to the
    * Micro-Manager API. You should retain a reference to this object for the
    * lifetime of your plugin. This method should not do anything except for
    * store that reference, as Micro-Manager is still busy starting up at the
    * time that this is called.
    */
   @Override
   public void setContext(Studio studio) {
      studio_ = studio;
   }

   /**
    * This method is called when your plugin is selected from the Plugins menu.
    * Typically at this time you should show a GUI (graphical user interface)
    * for your plugin.
    */
   @Override
   public void onPluginSelected() {
      if (frame_ == null) {
         // We have never before shown our GUI, so now we need to create it.
         frame_ = new RoiMappingFrame(studio_);
      }
      frame_.setVisible(true);
   }

   /**
    * This string is the sub-menu that the plugin will be displayed in, in the
    * Plugins menu.
    */
   @Override
   public String getSubMenu() {
      return "Developer Tools";
   }

   /**
    * The name of the plugin in the Plugins menu.
    */
   @Override
   public String getName() {
      return "ROI Mapping";
   }

   @Override
   public String getHelpText() {
      return "Help text that is displayed in certain contexts to tell the user what the plugin does.";
   }

   @Override
   public String getVersion() {
      return "2.0";
   }

   @Override
   public String getCopyright() {
      return "University of California, 2012-2015";
   }
}
