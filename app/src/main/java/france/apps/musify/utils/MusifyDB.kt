package france.apps.musify.utils

import android.util.Log
import com.google.firebase.database.*
import france.apps.musify.utils.models.PlayableMedia

object MusifyDB {

    private fun db(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }


    fun getNewReleases(listener: ValueEventListener) {
        db().child("songs").child("new_releases").addListenerForSingleValueEvent(listener)
        db().child("songs").child("trending").keepSynced(true)
    }
    fun getTrending(listener: ValueEventListener){
        db().child("songs").child("trending").addListenerForSingleValueEvent(listener)
        db().child("songs").child("trending").keepSynced(true)
    }
}
