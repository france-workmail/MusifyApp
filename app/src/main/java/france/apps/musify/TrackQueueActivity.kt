package france.apps.musify

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.models.MediaMetadata
import france.apps.musify.utils.models.PlayableMedia
import java.lang.ref.WeakReference

class TrackQueueActivity : AppCompatActivity() {

    internal var rvTracklist: RecyclerView? = null
    internal var tracklistAdapter:TrackListAdapter? = null
    private var playerListener: MusifyPlayer.OnPlayerChangesListener = object:MusifyPlayer.OnPlayerChangesListener{
        override fun OnPause(item: PlayableMedia?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun OnPlay(item: PlayableMedia?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun OnNewTrackOpened(item: PlayableMedia?) {
            tracklistAdapter?.notifyDataSetChanged()
        }

        override fun OnNewTrackStarted(item: PlayableMedia?) {
        }

        override fun OnCurrentTrackEnded(item: PlayableMedia?) {
        }

        override fun OnCurrentTrackTimeUpdated(item: PlayableMedia?, currentTime: Float, progressPercentage: Int) {
        }

        override fun OnListenerAttached(item: PlayableMedia?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_queue)


        rvTracklist = findViewById(R.id.rvTrackList)



        var trendingLayoutManager =  LinearLayoutManager(this)
        trendingLayoutManager.orientation = LinearLayoutManager.VERTICAL

        rvTracklist?.layoutManager = trendingLayoutManager
        tracklistAdapter = TrackListAdapter(applicationContext)
        rvTracklist?.adapter = tracklistAdapter
        MusifyPlayer.addListener(playerListener)


    }

    override fun onDestroy() {
        MusifyPlayer.removeListener(playerListener)
        super.onDestroy()
    }

    internal class TrackListAdapter(@NonNull var mContext: Context): RecyclerView.Adapter<TrackListViewHolder>() {


        override fun onCreateViewHolder(container: ViewGroup, p1: Int): TrackListViewHolder {
            return TrackListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_track,container,false))
        }

        override fun getItemCount(): Int {
            Log.e("Playlist Size: ", ""+MusifyPlayer.getMusicPlaylist().size )
            return MusifyPlayer.getMusicPlaylist().size
        }

        override fun onBindViewHolder(viewHolder: TrackListViewHolder, index: Int) {
            viewHolder.bind(MusifyPlayer.getMusicPlaylist()[index], View.OnClickListener {
                MusifyPlayer.playPlaylist(MusifyPlayer.getMusicPlaylist(),index)
                notifyDataSetChanged()
            },mContext)
        }


    }

    internal class TrackListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(@NonNull media: PlayableMedia, clickListener: View.OnClickListener, mContext: Context){
            itemView.setOnClickListener(clickListener)

            var tvTitle =  itemView.findViewById<TextView>(R.id.tvSongTitle)

            tvTitle.text = media.title
            itemView.findViewById<TextView>(R.id.tvArtist).text = media.artist

            val ivSongImage: ImageView? = itemView.findViewById(R.id.ivSongImage)

            if(ivSongImage != null){
                Glide.with(mContext)
                        .load(media.audio_image)
                        .into(ivSongImage)

            }else ivSongImage!!.setImageResource(android.R.drawable.ic_menu_report_image)


            val a = MusifyPlayer.getCurrentlyPlayedMusic()

            tvTitle.setTextColor(
                    if(a!=null && media.id.contentEquals(a.id)) Color.GREEN
                    else Color.WHITE)


            //TODO Replace this with a model specific duration
            // this consumes too much processing.

            var tvDuration: TextView? = itemView.findViewById<TextView>(R.id.tvSongDuration)
//            tvDuration.text = MusifyPlayer.getTimeString(MediaMetadata(media.audio_url).duration.toLong())



//            doAsync{
//              tvDuration?.post {
//                  tvDuration?.text = MusifyPlayer.getTimeString(MediaMetadata(media.audio_url).duration.toLong())
//              }
//            }
            if(tvDuration!=null ){
                //do this to minimize the metadata fetching to only a single time
                if(media.metadata!=null)
                    tvDuration.text = MusifyPlayer.getTimeString(media.metadata!!.duration.toLong())
                else
                    DurationAsycTask(WeakReference(tvDuration), media).execute()

            }

        }

    }

    /**
     * WeakReference to avoid context leak
     */
    internal class DurationAsycTask(var view: WeakReference<TextView>, var media: PlayableMedia ): AsyncTask<Void,Void,String>(){
        override fun doInBackground(vararg params: Void?): String {
            val metadata = MediaMetadata(media.audio_url)
            media.metadata = metadata
            return MusifyPlayer.getTimeString(metadata.duration.toLong())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            view.get()?.text = result
        }
    }

    internal class doAsync(val handler: ()-> Unit):AsyncTask<Void,Void,Void>(){
        init {

            execute()
        }
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}
