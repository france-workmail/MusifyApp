package france.apps.musify;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;

import france.apps.musify.utils.Constants;

public class MusifyApplication extends Application {

    static DiskLruCache diskLruCache;

    static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext =  getApplicationContext();

        if(Constants.OFFLINE_MODE) {
            try {
                diskLruCache = DiskLruCache.open(getCacheDir(), 1, 1, 50 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static DiskLruCache getDiskCache(Context context){
        return  diskLruCache;
    }

    public static Context getAppContext(){

        return appContext;
    }

}
