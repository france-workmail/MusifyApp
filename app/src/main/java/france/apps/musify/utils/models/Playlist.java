package france.apps.musify.utils.models;

import java.util.ArrayList;

public class Playlist {

    private String id;
    private String playlistName;
    private String playListAuthorId;
    private String playlistAuthorName;
    private ArrayList<PlayableMedia> playlistTracks;

    public Playlist(String id, String playlistName, String playListAuthorId, String playlistAuthorName, ArrayList<PlayableMedia> playlistTracks) {
        this.id = id;
        this.playlistName = playlistName;
        this.playListAuthorId = playListAuthorId;
        this.playlistAuthorName = playlistAuthorName;
        this.playlistTracks = playlistTracks;
    }

    public ArrayList<PlayableMedia> getPlaylistTracks() {
        return playlistTracks;
    }

    public void setPlaylistTracks(ArrayList<PlayableMedia> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }
    public void addTrack(PlayableMedia toAddMedia){
        this.playlistTracks.add(toAddMedia);
    }
}
