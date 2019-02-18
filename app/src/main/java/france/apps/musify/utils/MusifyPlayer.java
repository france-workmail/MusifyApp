package france.apps.musify.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.URLUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import france.apps.musify.MusifyApplication;
import france.apps.musify.utils.cache.new_cache.MediaCacheCallback;
import france.apps.musify.utils.cache.new_cache.MediaCacheWorkerTask;
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
            if(item!=null&&player.isPlaying()){

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


    /**
     * Headset/Blutooth speaker buttons
     */
    private static AudioManager mAudioManager;
    private static ComponentName mRemoteControlResponder;


    //setters and getters

    public static void setRepeatType(REPEAT_TYPE repeat_type){
        repeatType = repeat_type;
        Log.e("MusifyPlayer", "Changed repeat type");
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
        if(onPlayerChangesListener!=null)
            listeners.remove(onPlayerChangesListener);
    }



    //support methods


    public static void playPlaylist(ArrayList<PlayableMedia> playlist){
        musicPlaylist = playlist;
        playlistIndex = 0;
        play();
    }
    public static void playPlaylist(ArrayList<PlayableMedia> playlist, int index){
        musicPlaylist = playlist;
        playlistIndex = index;
        play();
    }

    private static void play(){



        if(player!=null && player.isPlaying()){
            player.stop();
            player.reset();
            player.release();
        }


        currentlyPlayedMusic = musicPlaylist.get(playlistIndex);




        playAudio(currentlyPlayedMusic.getAudio_url());




        for(OnPlayerChangesListener listener: listeners){

            listener.OnNewTrackOpened(getCurrentlyPlayedMusic());

        }
    }

    public static boolean isPlaying(){
        try {
            return player!=null && player.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public static void setCurrentlyPlayedMusic(@NonNull PlayableMedia item) {
        currentlyPlayedMusic = item;
        play();
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



        //setup headset hardware
        initMediaRemote();


            //setup player


            if (player != null) {
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            if (!URLUtil.isValidUrl(url))
                playNext();

            //TODO Add media caching.
            // Reference: https://stackoverflow.com/questions/12701249/getting-access-to-media-player-cache

            if (Constants.OFFLINE_MODE) {
//                new AudioStreamWorkerTask(MusifyApplication.getAppContext(), new OnCacheCallback() {
//                    @Override
//                    public void onSuccess(FileInputStream stream) {
//
//                        try {
//                            player.setDataSource(stream.getFD());
//                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    isCurrentTrackPrepared = true;
//                                    for (OnPlayerChangesListener listener : listeners)
//                                        listener.OnNewTrackStarted(getCurrentlyPlayedMusic());
//
//
//                                    handler.postDelayed(r, 1000);
//
//                                    player.start();
//                                }
//                            });
//
//
//                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    for (OnPlayerChangesListener listener : listeners)
//                                        listener.OnCurrentTrackEnded(getCurrentlyPlayedMusic());
//
//                                    handler.removeCallbacks(r);
//                                    playNext();
//                                }
//                            });
//
//                            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                                @Override
//                                public void onSeekComplete(MediaPlayer mp) {
//                                    Log.e("Player", "Seeked to: " + mp.getCurrentPosition());
//                                    player.start();
//                                }
//                            });
//                            stream.close();
//                            handler.removeCallbacks(r);
//
//
//                            //assign ui
//                            isCurrentTrackPrepared = false;
//                            player.prepareAsync();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (IllegalStateException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                }).execute(audio_url);

                new MediaCacheWorkerTask(MusifyApplication.getAppContext(), new MediaCacheCallback() {
                    @Override
                    public void onSnapshotFound(FileInputStream stream) {
                        streamSnapshot(stream);
//                        try {
//                            player.setDataSource(stream.getFD());
//                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    isCurrentTrackPrepared = true;
//                                    for (OnPlayerChangesListener listener : listeners)
//                                        listener.OnNewTrackStarted(getCurrentlyPlayedMusic());
//
//
//                                    handler.postDelayed(r, 1000);
//
//                                    player.start();
//                                }
//                            });
//
//
//                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    for (OnPlayerChangesListener listener : listeners)
//                                        listener.OnCurrentTrackEnded(getCurrentlyPlayedMusic());
//
//                                    handler.removeCallbacks(r);
//                                    playNext();
//                                }
//                            });
//
//                            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                                @Override
//                                public void onSeekComplete(MediaPlayer mp) {
//                                    Log.e("Player", "Seeked to: " + mp.getCurrentPosition());
//                                    player.start();
//                                }
//                            });
//                            stream.close();
//                            handler.removeCallbacks(r);
//
//
//                            //assign ui
//                            isCurrentTrackPrepared = false;
//                            player.prepareAsync();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onSnapshotMissing(String url) {
                        streamThroughNet(url);
                    }

                    @Override
                    public void onSnapshotDownloaded(boolean downloaded) {}
                }).execute(url);
            }
            //stream using internet
            else {
                streamThroughNet(url);

//                try{
//                //Without Caching Callback
//                player.setDataSource(audio_url);
//                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        isCurrentTrackPrepared = true;
//                        for (OnPlayerChangesListener listener : listeners)
//                            listener.OnNewTrackStarted(getCurrentlyPlayedMusic());
//
//
//                        handler.postDelayed(r, 1000);
//
//                        player.start();
//                    }
//                });
//
//
//                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        for (OnPlayerChangesListener listener : listeners)
//                            listener.OnCurrentTrackEnded(getCurrentlyPlayedMusic());
//
//                        handler.removeCallbacks(r);
//                        playNext();
//                    }
//                });
//
//                player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//                        Log.e("Player", "Seeked to: " + mp.getCurrentPosition());
//                        player.start();
//                    }
//                });
//                handler.removeCallbacks(r);
//
//
//                //assign ui
//                isCurrentTrackPrepared = false;
//                player.prepareAsync();
//            } catch(IOException e){
//                e.printStackTrace();
//            } catch(IllegalStateException ex){
//                ex.printStackTrace();
//            }
        }
    }

    private static void streamThroughNet(String url){
        try{
            //Without Caching Callback
            player.setDataSource(url);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isCurrentTrackPrepared = true;
                    for (OnPlayerChangesListener listener : listeners)
                        listener.OnNewTrackStarted(getCurrentlyPlayedMusic());


                    handler.postDelayed(r, 1000);

                    player.start();
                }
            });


            player.setOnCompletionListener(playerCompletionListener);

            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("Player", "Seeked to: " + mp.getCurrentPosition());
                    player.start();
                }
            });
            handler.removeCallbacks(r);


            //assign ui
            isCurrentTrackPrepared = false;
            player.prepareAsync();
        } catch(IOException e){
            e.printStackTrace();
        } catch(IllegalStateException ex){
            ex.printStackTrace();
        }
    }
    private static MediaPlayer.OnCompletionListener playerCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            for (OnPlayerChangesListener listener : listeners)
                listener.OnCurrentTrackEnded(getCurrentlyPlayedMusic());

            handler.removeCallbacks(r);


            switch (repeatType){
                case NO_REPEAT:
                    {
                        /*
                         *  If repeat option is not set, check if the current track index is
                         *  still within the playlist size so that we can move to next track.
                         *  Otherwise, the player stops.
                         */
                        if(playlistIndex < getMusicPlaylist().size()-1)
                            playNext();
                    }
                    break;
                /*
                 * Repeat the currently played track
                 */
                case REPEAT_SINGLE:
                    play();
                    break;
                /*
                 * playNext() implementations automatically goes back to first track of
                 * the playlist if player is playing the last track.
                 */
                case REPEAT_ALL:
                    playNext();

                    break;
            }


        }
    };

    private static void streamSnapshot(FileInputStream stream){
        try {
            player.setDataSource(stream.getFD());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isCurrentTrackPrepared = true;
                    for (OnPlayerChangesListener listener : listeners)
                        listener.OnNewTrackStarted(getCurrentlyPlayedMusic());


                    handler.postDelayed(r, 1000);

                    player.start();
                }
            });


            player.setOnCompletionListener(playerCompletionListener);

            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("Player", "Seeked to: " + mp.getCurrentPosition());
                    player.start();
                }
            });
            stream.close();
            handler.removeCallbacks(r);


            //assign ui
            isCurrentTrackPrepared = false;
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(IllegalStateException ex){
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
            MusifyPlayer.player.reset();
        }
    }


    public static ArrayList<PlayableMedia> getMusicPlaylist(){
        return musicPlaylist;
    }


    /**
     *
     *
     * METHODS TO MONITOR REMOTE DEVICE
     *
     */

    static MediaSessionCompat mediaSessionCompat;
    private static void initMediaRemote(){

//        if(mAudioManager==null || mRemoteControlResponder == null) {
//            mAudioManager = (AudioManager) MusifyApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
//            mRemoteControlResponder = new ComponentName(MusifyApplication.getAppContext(),
//                    HeadsetButtonReceiver.class.getName());
//
////        Log.e(TAG,"REGISTERED MEDIA REMOTE");
//            mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
////        Constants.currentPlayer = MussharePlayer.this;
//
//
//
//        }


//        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(MusifyApplication.getAppContext(),"MediaSessionTag");
//        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
//            @Override
//            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
//
//
//                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//                int keyCode = event.getKeyCode();
//
//                switch (keyCode){
////                        case KeyEvent.KEYCODE_VOLUME_UP:
//                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                        playOrPause();
//                        return true;
//                    case  KeyEvent.KEYCODE_MEDIA_NEXT:
//                        playNext();
//                        return true;
//                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                        playPrevious();
//                        return true;
//
//                }
//
//
//                return super.onMediaButtonEvent(mediaButtonEvent);
//            }
//        });
//        mediaSessionCompat.setActive(true);



        if(mediaSessionCompat==null) {
            ComponentName mediaReceiver = new ComponentName(MusifyApplication.getAppContext(), HeadsetButtonReceiver.class);
            mediaSessionCompat = new MediaSessionCompat(MusifyApplication.getAppContext(), "MediaButtons", mediaReceiver, null);
            mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
                @Override
                public boolean onMediaButtonEvent(Intent mediaButtonEvent) {

                    KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    int keyCode = event.getKeyCode();

                    if (event.getAction() == KeyEvent.ACTION_UP)
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_HEADSETHOOK:
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                playOrPause();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                playNext();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                playPrevious();
                                return true;

                        }

                    return super.onMediaButtonEvent(mediaButtonEvent);
                }


            });

            mediaSessionCompat.setActive(true);

            PlaybackStateCompat playbackStateCompat = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

                    )
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                    .build();
            mediaSessionCompat.setPlaybackState(playbackStateCompat);


        }


    }
}
