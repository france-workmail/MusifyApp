package france.apps.musify.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import france.apps.musify.R;

public  class Constants {
    public static boolean  OFFLINE_MODE =  true;

    public static DatabaseReference db(){
        return FirebaseDatabase.getInstance().getReference();
    }



    public interface ACTION {
        public static String MAIN_ACTION = "france.apps.musify.action.main";
        public static String INIT_ACTION = "france.apps.musify.action.init";
        public static String PREV_ACTION = "france.apps.musify.action.prev";
        public static String PLAY_ACTION = "france.apps.musify.action.play";
        public static String NEXT_ACTION = "france.apps.musify.action.next";
        public static String STARTFOREGROUND_ACTION = "france.apps.musify.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "france.apps.musify.action.stopforeground";
    }
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_headphones, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}
