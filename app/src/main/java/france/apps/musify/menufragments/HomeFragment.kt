package france.apps.musify.menufragments


import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*

import france.apps.musify.R
import france.apps.musify.databinding.FragmentHomeBinding
import france.apps.musify.utils.MusifyDB
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.models.PlayableMedia

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {


    /**
     * New releases
     */
    var newReleases: ArrayList<PlayableMedia> = ArrayList()
    internal var newReleasesAdapter: SongsAdapter? = null
    var rvNewReleases:RecyclerView? = null

    /**
     *
     * Trending
     */

    var trending: ArrayList<PlayableMedia> = ArrayList()
    internal var trendingAdapter: SongsAdapter? = null
    var rvTrending:RecyclerView? = null

    internal val listener: MusifyPlayer.OnPlayerChangesListener = object : MusifyPlayer.OnPlayerChangesListener{
        override fun OnPause(item: PlayableMedia?) {

        }

        override fun OnPlay(item: PlayableMedia?) {

            newReleasesAdapter?.notifyDataSetChanged()
            trendingAdapter?.notifyDataSetChanged()
        }

        override fun OnNewTrackOpened(item: PlayableMedia?) {
            newReleasesAdapter?.notifyDataSetChanged()
            trendingAdapter?.notifyDataSetChanged()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var binder: FragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        return binder.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         
         
         view.findViewById<ImageButton>(R.id.ibInfo).setOnClickListener {
             v ->
             Toast.makeText(activity,"Created by: France Gelasque. App Icon credits to: smalllikeart of www.flaticon.com",Toast.LENGTH_SHORT).show()
         }


         newReleasesAdapter = SongsAdapter(newReleases, activity!!)
         rvNewReleases = view.findViewById(R.id.rvNewReleases)

         var layoutManager =  LinearLayoutManager(activity)
         layoutManager.orientation = LinearLayoutManager.HORIZONTAL

         rvNewReleases?.layoutManager = layoutManager
         rvNewReleases?.adapter = newReleasesAdapter






         trendingAdapter = SongsAdapter(trending, activity!!)
         rvTrending = view.findViewById(R.id.rvTrending)

         var trendingLayoutManager =  LinearLayoutManager(activity)
         trendingLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

         rvTrending?.layoutManager = trendingLayoutManager
         rvTrending?.adapter = trendingAdapter




         MusifyDB.getNewReleases(object: ValueEventListener {
             override fun onCancelled(p0: DatabaseError) {
                 Log.e("fbError",p0.message)
             }

             override fun onDataChange(snapshot: DataSnapshot) {


                 if(snapshot.exists()){

                     newReleases.clear()
                     for(d in snapshot.children){
                         val media = d.getValue(PlayableMedia::class.java)
                         newReleases.add(media!!)
                     }

                     newReleasesAdapter!!.notifyDataSetChanged()

                 }

             }
         })

         MusifyDB.getTrending(object: ValueEventListener {
             override fun onCancelled(p0: DatabaseError) {
                 Log.e("fbError",p0.message)
             }

             override fun onDataChange(snapshot: DataSnapshot) {


                 if(snapshot.exists()){

                     trending.clear()
                     for(d in snapshot.children){
                         val media = d.getValue(PlayableMedia::class.java)
                         trending.add(media!!)
                     }

                     trendingAdapter!!.notifyDataSetChanged()

                 }

             }
         })




         MusifyPlayer.addListener(listener)

     }



    internal class SongsAdapter(private var songsList: ArrayList<PlayableMedia>, val context: Context) : RecyclerView.Adapter<SongViewHolder>() {
        override fun onCreateViewHolder(container: ViewGroup, index: Int): SongViewHolder {

            return SongViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song,container,false))
        }

        override fun getItemCount(): Int {
            return songsList.size
        }

        override fun onBindViewHolder(songViewHolder: SongViewHolder, index: Int) {
            songViewHolder.bind(songsList[index],View.OnClickListener {
                run {
                    Toast.makeText(context, "Playing from posts", Toast.LENGTH_SHORT).show()
                    MusifyPlayer.playPlaylist(songsList, index)
                    notifyDataSetChanged()
                }
            },context)
        }

    }

    internal class SongViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bind(@NonNull media: PlayableMedia, clickListener: View.OnClickListener, mContext: Context){

            if(media==null)
             return

            itemView.findViewById<TextView>(R.id.tvSongTitle).text = media.title +" - " + media.artist

            var ivSongImage: ImageView? = itemView.findViewById(R.id.ivSongImage)

            if(media.audio_image != null && ivSongImage != null){
                Glide.with(mContext)
                        .load(media.audio_image)
                        .into(ivSongImage)

            }else ivSongImage!!.setImageResource(android.R.drawable.ic_menu_report_image)


                var a = MusifyPlayer.getCurrentlyPlayedMusic()

                itemView.findViewById<ImageView>(R.id.ivPlaying).visibility =
                        if(a!=null && media.id.contentEquals(a.id)) View.VISIBLE
                        else View.GONE


            itemView.setOnClickListener(clickListener)
        }

    }


}
