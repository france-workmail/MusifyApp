package france.apps.musify

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import france.apps.musify.menufragments.HomeFragment.SongsAdapter
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.OfflinePlaylistTracker
import france.apps.musify.utils.models.PlayableMedia

class SettingsActivity : AppCompatActivity() {

    var rvOfflineTracks: RecyclerView? = null
    internal var offlineTracksAdapter: SongsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        var arrOfflineTracks : ArrayList<PlayableMedia> = OfflinePlaylistTracker.getAllOfflineTracks()


        rvOfflineTracks = findViewById(R.id.rvOfflineTracks)



        var layoutManager =  LinearLayoutManager(this@SettingsActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rvOfflineTracks?.layoutManager = layoutManager


        offlineTracksAdapter = SongsAdapter(arrOfflineTracks,this@SettingsActivity)
        rvOfflineTracks?.adapter = offlineTracksAdapter

    }




}
