package france.apps.musify.utils.models;

import android.media.MediaMetadataRetriever;

import java.util.HashMap;

public class MediaMetadata {

    private String mediaUrl;
    MediaMetadataRetriever retriever;

    public MediaMetadata(String mediaUrl) {
        this.mediaUrl = mediaUrl;

        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mediaUrl, new HashMap<String, String>());
    }
    /**
     * The metadata key to retrieve the numeric string describing the
     * order of the audio data source on its original recording.
     */
    public String getCDTrackNumber(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
    }
    /**
     * The metadata key to retrieve the information about the album title
     * of the data source.
     */
    public String getAlbum(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }
    /**
     * The metadata key to retrieve the information about the artist of
     * the data source.
     */
    public String getArtist(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }
    /**
     * The metadata key to retrieve the information about the author of
     * the data source.
     */
    public String getAuthor(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
    }
    /**
     * The metadata key to retrieve the information about the composer of
     * the data source.
     */
    public String getComposer(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
    }

    /**
     * The metadata key to retrieve the date when the data source was created
     * or modified.
     */
    public String getDate(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
    }

    /**
     * The metadata key to retrieve the content type or genre of the data
     * source.
     */
    public String getGenre(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
    }

    /**
     * The metadata key to retrieve the data source title.
     */
    public String getTitle(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }
    /**
     * The metadata key to retrieve the year when the data source was created
     * or modified.
     */
    public String getYear(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
    }
    /**
     * The metadata key to retrieve the playback duration of the data source.
     */
    public String getDuration(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }
    /**
     * The metadata key to retrieve the number of tracks, such as audio, video,
     * text, in the data source, such as a mp4 or 3gpp file.
     */
    public String getNumTracks(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
    }
    /**
     * The metadata key to retrieve the information of the writer (such as
     * lyricist) of the data source.
     */
    public String getWriter(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
    }
    /**
     * The metadata key to retrieve the mime type of the data source. Some
     * example mime types include: "video/mp4", "audio/mp4", "audio/amr-wb",
     * etc.
     */
    public String getMimeType(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
    }
    /**
     * The metadata key to retrieve the information about the performers or
     * artist associated with the data source.
     */
    public String getAlbumArtist(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
    }
    /**
     * The metadata key to retrieve the numberic string that describes which
     * part of a set the audio data source comes from.
     */
    public String getDiscNumber(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
    }
    /**
     * The metadata key to retrieve the music album compilation status.
     */

    public String getCompilation(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
    }
    /**
     * If this key exists the media contains audio content.
     */
    public String getHasAudio(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
    }
    /**
     * If this key exists the media contains video content.
     */
    public String getHasVideo(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
    }
    /**
     * If the media contains video, this key retrieves its width.
     */
    public String getVideoWidth(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    }
    /**
     * If the media contains video, this key retrieves its height.
     */
    public String getVideoHeight(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
    }
    /**
     * This key retrieves the average bitrate (in bits/sec), if available.
     */
    public String getBitRate(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
    }
    /**
     * This key retrieves the location information, if available.
     * The location should be specified according to ISO-6709 standard, under
     * a mp4/3gp box "@xyz". Location with longitude of -90 degrees and latitude
     * of 180 degrees will be retrieved as "-90.0000+180.0000", for instance.
     */
    public String getLocation(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
    }


    /**
     * This key retrieves the video rotation angle in degrees, if available.
     * The video rotation angle may be 0, 90, 180, or 270 degrees.
     */

    public String getVideoRotation(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
    }

    /**
     * This key retrieves the original capture framerate, if it's
     * available. The capture framerate will be a floating point
     * number.
     */
    public String getCaptureFrameRate(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
    }
    /**
     * If this key exists the media contains still image content.
     */
    public String getHasImage(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE);
    }
    /**
     * If the media contains still images, this key retrieves the number
     * of still images.
     */
    public String getImageCount(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_COUNT);
    }

    /**
     * If the media contains still images, this key retrieves the image
     * index of the primary image.
     */
    public String getImagePrimary(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_PRIMARY);
    }

    /**
     * If the media contains still images, this key retrieves the width
     * of the primary image.
     */
    public String getImageWidth(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_WIDTH);
    }

    /**
     * If the media contains still images, this key retrieves the height
     * of the primary image.
     */
    public String getImageHeight(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT);
    }

    /**
     * If the media contains still images, this key retrieves the rotation
     * angle (in degrees clockwise) of the primary image. The image rotation
     * angle must be one of 0, 90, 180, or 270 degrees.
     */
    public String getImageRotation(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_ROTATION);
    }

    /**
     * If the media contains video and this key exists, it retrieves the
     * total number of frames in the video sequence.
     */
    public String getFrameCount(){
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
    }
}
