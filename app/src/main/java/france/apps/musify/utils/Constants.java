package france.apps.musify.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public  class Constants {
    public static boolean  OFFLINE_MODE =  true;

    public static DatabaseReference db(){
        return FirebaseDatabase.getInstance().getReference();
    }
}
