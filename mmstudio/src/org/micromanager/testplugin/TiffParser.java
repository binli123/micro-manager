/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.micromanager.testplugin;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.DatastoreFrozenException;
import org.micromanager.data.DatastoreRewriteException;
import org.micromanager.data.Image;
import org.micromanager.data.Metadata;

public class TiffParser {
    private final Studio app_;
    private File tiffFile_;
    private Datastore store;
    private SortedCoordsList coords_list;
    private Coords.CoordsBuilder c_builder;
    private Metadata.MetadataBuilder m_builder;
private long frameLengthMs;
    
    public TiffParser(Studio studio_, long frameLengthMs) {
        app_ = studio_;
        this.frameLengthMs = frameLengthMs;
    }
    
    public final void loadGeneralTiff(File file) throws FileNotFoundException, 
            IOException {
        // Open the tiff via ImageJ
        app_.logs().logMessage("Trying to open general tiff.");
        InputStream input_stream = new FileInputStream(file);
        ImagePlus win;
        try {
            Opener o = new Opener ();
            win = o.openTiff(input_stream, "tiffParser");
        } finally {
            input_stream.close();
        }
        
        ImageStack stack = win.getImageStack();
        // Build up metadata from scratch
        m_builder = app_.data().getMetadataBuilder();
        m_builder.imageNumber(0l).camera("tiffParser").exposureMs(1.0d)
                .xPositionUm(0.0).yPositionUm(0.0).zPositionUm(0.0)
                .positionName("tiffParser");
        int width = stack.getWidth();
        int height = stack.getHeight();
        int bytesPerPixel;
        if(stack.getPixels(win.getSlice()) instanceof short[]) {
            bytesPerPixel = 1;
        } else {
            throw new ArrayStoreException("Wrong image bit depth.");
        }
        
        Image ram_image;
        Coords coords; 
        Metadata metadata;
        for (int i=1; i<=stack.getSize(); i++) {
            // get immutable types from builders
            coords = c_builder.build();
            metadata = m_builder.build();
            
            ram_image = app_.data().createImage(stack.getPixels(i), width, height, bytesPerPixel, 1, coords, metadata);
            // put the image into new datastore
            try {
                store.putImage(ram_image);
            } catch (DatastoreFrozenException ex) {
            } catch (DatastoreRewriteException ex) {
            } catch (IllegalArgumentException ex) {
            }
            // put the relevant coords into an ordered array
            coords_list.add(coords);
            
            // Prepare builders for next image by incrementing values
            c_builder.offset("time", (int) frameLengthMs);
            m_builder.imageNumber((long) i).elapsedTimeMs((double) i*frameLengthMs);
            // Log the process
            if (i%100==0) {
                app_.logs().logDebugMessage(String.format("Loaded %d images from general tiff stack.",i));
            }
        }
    }
}

/*
private final Studio app;
Studio studio
app = studio;
File file
InputStream input_stream = new FileInputStream(file)
ImagePlus win
Opener o = new Opener();
win = o.openTiff(input_stream,"InjectorStack")
ImageStack stack = win.getImageStack();
m_builder = app.data().getMetadataBuilder();
Image ram_image;
ram_image = app.data().createImage(stack.getPixels(i), width, height, bytesPerPixel, 1, coords, metadata);
store.putImage(ram_image);
*/