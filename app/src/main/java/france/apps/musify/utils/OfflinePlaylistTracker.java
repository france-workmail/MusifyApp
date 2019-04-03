package france.apps.musify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import france.apps.musify.MusifyApplication;
import france.apps.musify.utils.cache.new_cache.MediaCacheWorkerTask;
import france.apps.musify.utils.models.PlayableMedia;

public class OfflinePlaylistTracker {

    private static String preferencesKey="thIsIsPr3fer3nc3sKey4Offl!neTr@cks";


    public static void saveOfflineTrack(PlayableMedia media){

        ArrayList<PlayableMedia> offlineMedias = getAllOfflineTracks();
        boolean alreadyHasCopy = false;

        for (PlayableMedia pm :
                offlineMedias) {
            if(pm.getId().equalsIgnoreCase(media.getId())){
                alreadyHasCopy = true;
                break;
            }
        }

        if(!alreadyHasCopy) {

            Context mContext = MusifyApplication.getAppContext();

            SharedPreferences.Editor editor = mContext.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE).edit();


            String mediaJson = new GsonBuilder().disableHtmlEscaping().create().toJson(media);

            Log.e("Save Offline", mediaJson);
            //key would be a combination of media id and its audio url

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            String key = currentDateandTime.concat(media.getId().concat(media.getAudio_url()));

            editor.putString(key, mediaJson);
            editor.apply();
        }




    }
    /** Generates an array list of @PlayableMedia object based from sharedpreferences list of media file representations **/
    public static ArrayList<PlayableMedia> getAllOfflineTracks(){

        ArrayList<PlayableMedia> response = new ArrayList<>();

        Context mContext = MusifyApplication.getAppContext();

        SharedPreferences prefs = mContext.getSharedPreferences(preferencesKey,Context.MODE_PRIVATE);


        Map<String,?> keys = prefs.getAll();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        for(Map.Entry<String,?> entry: keys.entrySet()){
            PlayableMedia media = gson.fromJson(entry.getValue().toString(),PlayableMedia.class);
            response.add(media);
        }

        return response;
    }

    /** Removes the disklrucache copy of the actual media file as well as its representation on shared preferences **/
    public static boolean removeFromOffline(PlayableMedia media){
        try {
            Context mContext = MusifyApplication.getAppContext();

            //Remove from disk
            MusifyApplication.getDiskCache(mContext).
                    remove(MediaCacheWorkerTask.getHashKeyForString(media.getAudio_url()));


            //remove from shared preferences

            SharedPreferences prefs = mContext.getSharedPreferences(preferencesKey,Context.MODE_PRIVATE);
            Map<String,?> keys = prefs.getAll();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            for(Map.Entry<String,?> entry: keys.entrySet()){
                PlayableMedia m = gson.fromJson(entry.getValue().toString(),PlayableMedia.class);

                if(m.getId().equalsIgnoreCase(media.getId()) && m.getAudio_url().equalsIgnoreCase(media.getAudio_url())){


                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove(entry.getKey());
                    editor.apply();
                    break;
                }

            }





            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  false;
    }

}
