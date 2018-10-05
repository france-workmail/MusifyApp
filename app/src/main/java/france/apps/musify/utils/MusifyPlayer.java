package france.apps.musify.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import france.apps.musify.utils.models.PlayableMedia;

public class MusifyPlayer {
    public static MediaPlayer player;


    private static ArrayList<PlayableMedia> musicPlaylist, shuffledPlayList;
    public static int playlistIndex =0;
    public static boolean isShuffledPlaying = false;
    public static boolean isCurrentTrackPrepared = false;
    private static  PlayableMedia currentlyPlayedMusic = null;

    private static REPEAT_TYPE repeatType = REPEAT_TYPE.NO_REPEAT;

    private static OnPlayerChangesListener listener = new OnPlayerChangesListener() {
        @Override
        public void OnCurrentTrackEnded(PlayableMedia item) {

        }

        @Override
        public void OnCurrentTrackTimeUpdated(PlayableMedia item, float currentTime, int progressPercentage) {

        }

        @Override
        public void OnPause(PlayableMedia single) {

        }

        @Override
        public void OnPlay(PlayableMedia single) {

        }

        @Override
        public void OnNewTrackOpened(PlayableMedia single) {

        }

        @Override
        public void OnNewTrackStarted(PlayableMedia single) {

        }

        @Override
        public void OnListenerAttached(PlayableMedia item) {


        }
    };



    public enum REPEAT_TYPE {
        NO_REPEAT,
        REPEAT_ALL,
        REPEAT_SINGLE,
    }




    public interface OnPlayerChangesListener{
        void OnPause(PlayableMedia item);
        void OnPlay(PlayableMedia item);
        void OnNewTrackOpened(PlayableMedia item);
        void OnNewTrackStarted(PlayableMedia item);
        void OnCurrentTrackEnded(PlayableMedia item);
        void OnCurrentTrackTimeUpdated(PlayableMedia item, float currentTime, int progressPercentage);
        void OnListenerAttached(PlayableMedia item);//handles all initialization/re-initializations
    }


    public static ArrayList<OnPlayerChangesListener> listeners = new ArrayList<>();



    private static Handler handler = new Handler();
    final static Runnable r = new Runnable() {
        @Override
        public void run() {

            PlayableMedia item = getCurrentlyPlayedMusic();
            if(item!=null&& player.isPlaying()){

//                int currentProgressPercentage = (int) (((double) PlayerUtil.player.getCurrentPosition() / PlayerUtil.player.getDuration()) * 100);

                handler.postDelayed(this,1000);
                int currentPosition = Math.round(player.getCurrentPosition()/1000) *1000;


                int progressPercentage = (int) (( ((float)player.getCurrentPosition()) /   ((float)player.getDuration())) * 100);

                for(OnPlayerChangesListener l:listeners){
                    l.OnCurrentTrackTimeUpdated(item,currentPosition, progressPercentage);
                }


            }else
                handler.removeCallbacks(this);

        }
    };


    //setters and getters

    public static void setRepeatType(REPEAT_TYPE repeat_type){
        repeatType = repeat_type;
    }
    public static REPEAT_TYPE getRepeatType(){
        return repeatType;//
    }


    public static void addListener(OnPlayerChangesListener onPlayerChangesListener){
        if(onPlayerChangesListener!=null && !listeners.contains(onPlayerChangesListener)) {
            listeners.add(onPlayerChangesListener);
            onPlayerChangesListener.OnListenerAttached(getCurrentlyPlayedMusic());
        }
    }
    public static void removeListener(OnPlayerChangesListener onPlayerChangesListener){
        if(onPlayerChangesListener!=null && listeners.contains(onPlayerChangesListener))
            listeners.remove(onPlayerChangesListener);
    }



    //support methods


    public static void playPlaylist(ArrayList<PlayableMedia> playlist){
        musicPlaylist = playlist;
        playlistIndex = 0;
        play();
    }

    private static void play(){



        if(player!=null && player.isPlaying()){
            player.stop();
            player.reset();
            player.release();
        }


        PlayableMedia s = musicPlaylist.get(playlistIndex);

        currentlyPlayedMusic = s;




        playAudio(currentlyPlayedMusic.getUrl());




        for(OnPlayerChangesListener listener: listeners){

            listener.OnNewTrackOpened(getCurrentlyPlayedMusic());

        }
    }

    public static boolean isPlaying(){
        return player!=null && player.isPlaying();
    }

    public static void shufflePlaylist(boolean shouldShuffle)
    {

        if(musicPlaylist == null)
            return ;

        if(musicPlaylist.size()>1){

            if(shouldShuffle){
                Collections.shuffle(musicPlaylist);

                playlistIndex = musicPlaylist.indexOf(currentlyPlayedMusic);
            }
            else{

                playlistIndex = musicPlaylist.indexOf(currentlyPlayedMusic);
            }
            isShuffledPlaying = shouldShuffle;

        }

    }

    public static void playOrPause(){
        if(player==null)
            return;
        if(player.isPlaying()){

            player.pause();
            for(OnPlayerChangesListener listener: listeners)
                listener.OnPause(getCurrentlyPlayedMusic());
            handler.removeCallbacks(r);
        }
        else{

            player.start();
            for(OnPlayerChangesListener listener: listeners)
                listener.OnPlay(getCurrentlyPlayedMusic());
            handler.postDelayed(r,1000);
        }
    }


    public static void playPrevious(){
        if(musicPlaylist==null)
            return;
        if(musicPlaylist.size()<=0)
            return;

        if((playlistIndex-1)>=0){

            playlistIndex--;
        }
        else{

            playlistIndex = musicPlaylist.size()-1;
        }

        play();
    }


    static boolean canPlayNextOrPrevious(){

        return musicPlaylist!=null && musicPlaylist.size()>0;
    }

    public static void playNext(){

        if(musicPlaylist==null)
            return;
        if(musicPlaylist.size()<=0)
            return;

        if((playlistIndex+1)<(musicPlaylist.size())){

            playlistIndex++;
        }
        else{

            playlistIndex = 0;
        }


        play();
    }


    public static PlayableMedia getCurrentlyPlayedMusic(){
        return currentlyPlayedMusic;

    }

    public static void setCurrentlyPlayedMusic(@Nullable PlayableMedia item) {
        currentlyPlayedMusic = item;
    }

    public static void seekToTrackPercentage(int trackDurationPercentage){


        try {
            if (player != null){
                int seekToValue = (player.getDuration() * trackDurationPercentage) / 100;
                player.seekTo(seekToValue);
            }
        }catch (IllegalStateException ex){
            //cannot seek mediaplayer
            ex.printStackTrace();
        }
    }

    private static void playAudio(String url) {

        try {

            //setup player



            if(player!=null){
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            if(!URLUtil.isValidUrl(url))
                playNext();

            player.setDataSource(url);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isCurrentTrackPrepared = true;
                    for(OnPlayerChangesListener listener: listeners)
                        listener.OnNewTrackStarted(getCurrentlyPlayedMusic());


                    handler.postDelayed(r,1000);

                    player.start();
                }
            });


            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    for(OnPlayerChangesListener listener: listeners)
                        listener.OnCurrentTrackEnded(getCurrentlyPlayedMusic());

                    handler.removeCallbacks(r);
                    playNext();
                }
            });

            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("Player", "Seeked to: "+mp.getCurrentPosition());
                    player.start();
                }
            });
            handler.removeCallbacks(r);


            //assign ui
            isCurrentTrackPrepared = false;
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }


    public static String getTimeString(long millis) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static void initializePlayerWithMusic(ArrayList<PlayableMedia> mPlaylist,int songIndex){

        if(!listeners.contains(listener))
            listeners.add(listener);

        musicPlaylist = mPlaylist;
        playlistIndex = songIndex;
        play();

    }


    public static void playTrack(PlayableMedia item) {
        if(musicPlaylist == null)
            musicPlaylist = new ArrayList<>();

        musicPlaylist.add(item);
        Log.e("dsada",listeners.toString());

        playPlaylist(musicPlaylist);
    }

    public static void destroy(){
        if(MusifyPlayer.isPlaying()){
            MusifyPlayer.player.stop();
            MusifyPlayer.player.release();
        }
    }


    public static ArrayList<PlayableMedia> getMusicPlaylist(){
        return musicPlaylist;
    }
}
