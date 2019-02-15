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
import java.security.InvalidParameterException;
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
    private static native String getGooglePlayPackage();
    private static native String getAmazonPackage();

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



//        if(isIllegallyDistributed(appContext,PACKAGE_NAME,getGooglePlayPackage(),getAmazonPackage())){
//
//            Toast.makeText(appContext, "App installation should be from official channels only", Toast.LENGTH_LONG).show();
//            new CountDownTimer(5000, 1000) {
//                public void onTick(long millisUntilFinished) {}
//                public void onFinish() {System.exit(0);}
//            }.start();
//
//            return;
//        }

        String encKey = getEncryptedKey();
        Log.e("EncryptedKey", encKey);
        Toast.makeText(appContext,encKey,Toast.LENGTH_LONG).show();
    }

    public static String getEncryptedKey(){

        byte[] keyStart = invokeNativeFunction().getBytes();
        KeyGenerator kgen = null;

        SecureRandom sr = new SecureRandom();
        try {
            kgen = KeyGenerator.getInstance("AES");
            sr.setSeed(keyStart);
            kgen.init(256, sr); // 192 and 256 bits may not be available

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        catch (InvalidParameterException invPar){
            if(kgen == null)
                return null;
            //if 256 is not supported we'll use 128
            kgen.init(128,sr);
        }



        SecretKey skey = kgen.generateKey();
        byte[] key = skey.getEncoded();

        // encrypt
        try {
            byte[] encryptedData = encrypt(key, "".getBytes());
            return new String(encryptedData);
        }catch (Exception e){e.printStackTrace();}

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
    public static boolean isIllegallyDistributed(Context context, String myPackageName, String google, String amazon)
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
