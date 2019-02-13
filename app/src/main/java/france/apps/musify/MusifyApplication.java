package france.apps.musify;

import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import france.apps.musify.utils.Constants;

public class MusifyApplication extends Application {

    static DiskLruCache diskLruCache;

    static Context appContext;


    static {
        System.loadLibrary("native-lib");
    }

    private static native String invokeNativeFunction();

    @Override
    public void onCreate() {
        super.onCreate();

        appContext =  getApplicationContext();
        FirebaseApp.initializeApp(appContext);

        if(Constants.OFFLINE_MODE) {
            try {
                diskLruCache = DiskLruCache.open(getCacheDir(), 1, 1, 50 * 1024 * 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        if(isHacked(appContext,PACKAGE_NAME,GOOGLE_PLAY,AMAZON_STORE)){

            Toast.makeText(appContext, "App installation should be from official channels only", Toast.LENGTH_LONG).show();
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {}
                public void onFinish() {System.exit(0);}
            }.start();

            return;
        }

        String encKey = getEncryptedKey();
        Log.e("EncryptedKey", encKey);
        Toast.makeText(appContext,encKey,Toast.LENGTH_LONG).show();
    }

    public static String getEncryptedKey(){
//        String nativeKey = invokeNativeFunction();


        try {


//            byte[] keyStart = nativeKey.getBytes();
            byte[] keyStart = invokeNativeFunction().getBytes();
            KeyGenerator kgen = null;
            kgen = KeyGenerator.getInstance("AES");

//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            SecureRandom sr = new SecureRandom();
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] key = skey.getEncoded();

            // encrypt
            byte[] encryptedData = encrypt(key,"".getBytes());

            return new String(encryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (Exception ex){ex.printStackTrace();}

        return null;
    }
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static DiskLruCache getDiskCache(Context context){
        return  diskLruCache;
    }

    public static Context getAppContext(){

        return appContext;
    }


    private static final String PACKAGE_NAME = "france.apps.musify";
    private static final String GOOGLE_PLAY = "com.android.vending";
    private static final String AMAZON_STORE = "com.android.vending";
    public static boolean isHacked(Context context, String myPackageName, String google, String amazon)
    {
        //Renamed?
        if (context.getPackageName().compareTo(myPackageName) != 0) {
            return true; // BOOM!
        }

        //Relocated?
        String installer = context.getPackageManager().getInstallerPackageName(myPackageName);

        if (installer == null){
            return true; // BOOM!
        }

        if (installer.compareTo(google) != 0 && installer.compareTo(amazon) != 0){
            return true; // BOOM!
        }
        return false;
    }

}
