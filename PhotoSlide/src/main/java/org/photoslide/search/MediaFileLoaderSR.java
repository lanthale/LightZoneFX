/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.photoslide.search;

import org.photoslide.browserlighttable.*;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import org.photoslide.browserlighttable.MediaGridCellFactory;
import org.photoslide.browserlighttable.MediaLoadingTask;
import org.photoslide.datamodel.MediaFile;
import org.photoslide.datamodel.MediaGridCell;

/**
 *
 * @author selfemp
 */
public class MediaFileLoaderSR {

    private final MediaGridCellSearchFactory factory;

    public MediaFileLoaderSR(MediaGridCellSearchFactory factory) {
        this.factory = factory;
    }

    public MediaFileLoaderSR() {
        this.factory = null;
    }

    public Image loadImage(MediaFile fileItem) {
        Image retImage = null;
        try {
            Image iImage = new Image(fileItem.getPathStorage().toUri().toURL().toString(), 300, 300, true, false, true);
            iImage.progressProperty().addListener((ov, t, t1) -> {
                if (t1.doubleValue() == 1.0) {
                    fileItem.setLoading(false);                    
                    if (factory != null) {                        
                        MediaGridCellSR mediaCellForMediaFile = factory.getMediaCellForMediaFile(fileItem);
                        if (mediaCellForMediaFile != null) {
                            mediaCellForMediaFile.requestLayout();
                        }                        
                    }
                }
            });
            return iImage;
        } catch (MalformedURLException ex) {
            Logger.getLogger(MediaLoadingTask.class.getName()).log(Level.SEVERE, null, ex);
            return retImage;
        }
    }

    public Media loadVideo(MediaFile fileItem) {
        Media video = null;
        try {
            video = new Media(fileItem.getPathStorage().toUri().toURL().toExternalForm());
            fileItem.setLoading(false);
            fileItem.setVideoSupported(MediaFile.VideoTypes.SUPPORTED);
            fileItem.setMedia(video, MediaFile.VideoTypes.SUPPORTED);
        } catch (MediaException e) {
            if (e.getType() == MediaException.Type.MEDIA_UNSUPPORTED) {
                fileItem.setVideoSupported(MediaFile.VideoTypes.UNSUPPORTED);
                fileItem.setMedia(video, MediaFile.VideoTypes.UNSUPPORTED);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(MediaLoadingTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return video;
    }
}