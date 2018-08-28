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
import org.micromanager.Album;
import org.micromanager.StagePosition;
import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

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
    private int size_;
    
    public SnapKernel(Studio studio_){
        app_ = studio_;
        stagePosList_ = new ArrayList<StagePosition>();
    }
    
    public Album snapImages(ArrayList<StagePosition> spal_) {
        stagePosList_ = spal_;
        Album album = app_.getAlbum();
        Image image = null;
        int numOfPos = stagePosList_.size();
        for (int i = 0; i< numOfPos; i++) {
            goToPosition(stagePosList_, i);
            StagePosition sp = stagePosList_.get(i);
            if (sp.numAxes == 2){
                image = snapSingleImage();
                album.addImage(image);      
            }              
        }
        return album;
    }
    
    public Image snapSingleImage() {
        Image image = null;
        try {
            app_.getCMMCore().snapImage();
            TaggedImage tmp = app_.getCMMCore().getTaggedImage();
            image = app_.data().convertTaggedImage(tmp);
            image = image.copyAtCoords(image.getCoords().copy().channel(0).build());            
        } catch (Exception ex){
            Logger.getLogger(SnapKernel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return image;
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
    
    public ArrayList<StagePosition> generatePositions(int Size) {
        double imFieldHeight;
        double imFieldWidth;
        double[] start = {0, 0};
        double[] end = {0, 0};
        size_ = Size;
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
        return stagePosList_;
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
    
    public void getKernelImage(Album album_, int size_) {
        store = album_.getDatastore();
        byte rawdata[] = new byte[512*512]; 
        byte kerneldata[][] = new byte[512*size_][512*size_]; // [row][col]
        int numOfImg = store.getNumImages();
        for (int i = 0; i<size_; i++) {
            rawdata = getRawData(album_, i);
            byte bididata[][] = monotoBidi(rawdata, 512, 512);
            //addSubImageToRow(kerneldata,bididata);
        }
    }
        
    public byte[] getRawData(Album album_, int index_) {
        Datastore store;
        List<Image> images;
        Image image = null;
        byte rawdata[] = new byte[512*512];
        store = album_.getDatastore();
        Coords.CoordsBuilder builder = app_.data().getCoordsBuilder();
        builder = builder.index(Coords.TIME, index_);
        Coords coords = builder.build();
        images = store.getImagesMatching(coords);
        image = images.get(0);
        rawdata = (byte[]) image.getRawPixels();
        return rawdata;
    }        
   
    public byte[][] addSubImageToRow(byte[][] array_, byte[][] subArray_, 
            int xKey, int yKey) {
        int arrayRow = yKey;
        int arrayCol = xKey;
        int subArrayRow = subArray_.length;
        int subArrayCol = subArray_[0].length;
        for (int i = 0; i < arrayRow; i++) {
            int col = 0;
            for (int j = arrayCol; j < arrayCol + subArrayCol; j++) {
                array_[i][j] = subArray_[i][col];
                col++;
            }
        }
        return array_;
    }
    
    public byte[][] addSubImageToCol(byte[][] array_, byte[][] subArray_) {
        int arrayRow = array_.length;
        int arrayCol = array_[0].length;
        int subArrayRow = subArray_.length;
        int subArrayCol = subArray_[0].length;
        byte newArray[][] = new byte[arrayRow][arrayCol + subArrayCol];
        for (int i = 0; i < arrayCol; i++) {
            int row = 0;
            for (int j = arrayRow; j < arrayRow + subArrayRow; j++) {
                newArray[i][j] = subArray_[row][j];
                row++;
            }
        }
        return newArray;
    }
    
    public byte[][] monotoBidi(final byte[] array, 
            final int rows, final int cols) {
        byte[][] bidi = new byte[rows][cols];
        for ( int i = 0; i < rows; i++ )
            System.arraycopy(array, (i*cols), bidi[i], 0, cols);
        return bidi;
    } 
    
    public void HelloCV() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
    }

}
