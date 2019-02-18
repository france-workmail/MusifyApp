package france.apps.musify

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatSeekBar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.models.PlayableMedia

class PlayerActivity : AppCompatActivity() {

    internal var sbMediaSeekbar: AppCompatSeekBar? = null
    internal var tvCurrentTime: TextView? = null
    internal var tvArtist: TextView? = null
    internal var tvTitle: TextView? = null
    internal var tvDuration: TextView? = null

    internal var ibPlayPause : ImageButton? = null
    internal var ibNext: ImageButton? = null
    internal var ibPrevious: ImageButton? = null
    internal var viewPager: ViewPager? = null
    internal var ibDownloaded: ImageButton? = null

    internal var ibRepeat : ImageButton? = null;

    internal var playerListener: MusifyPlayer.OnPlayerChangesListener =  object:MusifyPlayer.OnPlayerChangesListener{
        override fun OnListenerAttached(item: PlayableMedia?) {
            var title = item?.title
            var artist = item?.artist
            title = title ?: item?.title //elvis expression
            artist = artist ?: "No artist"
            tvArtist?.text = artist
            tvTitle?.text = title

            if(MusifyPlayer.isPlaying()) {
                var duration: Int? = MusifyPlayer.player?.duration
                tvDuration?.text = if (duration != null) MusifyPlayer.getTimeString(duration.toLong()) else "0:00"
            }
            else  tvDuration?.text = "0:00"


            ibPlayPause?.setImageResource(if(MusifyPlayer.isPlaying()) R.mipmap.ic_pause else R.mipmap.ic_play_arrow)
            ibDownloaded?.setImageResource(if(MusifyPlayer.getCurrentlyPlayedMusic().hasOfflineCopy)R.mipmap.ic_downloaded_arrow else R.mipmap.ic_download_arrow);
        }

        override fun OnPause(item: PlayableMedia?) {


            ibPlayPause?.setImageResource(R.mipmap.ic_play_arrow)
        }

        override fun OnPlay(item: PlayableMedia?) {


            ibPlayPause?.setImageResource(R.mipmap.ic_pause)
        }

        override fun OnNewTrackOpened(item: PlayableMedia?) {
            var title = item?.title
            var artist = item?.artist
            title = title ?: item?.title //elvis expression
            artist = artist ?: "No artist"
            tvArtist?.text = artist
            tvTitle?.text = title

            viewPager?.removeOnPageChangeListener(pagerListener)
            viewPager?.currentItem =  MusifyPlayer.playlistIndex
            viewPager?.addOnPageChangeListener(pagerListener)

            ibDownloaded?.setImageResource(if(MusifyPlayer.getCurrentlyPlayedMusic().hasOfflineCopy)R.mipmap.ic_downloaded_arrow else R.mipmap.ic_download_arrow);
        }

        override fun OnNewTrackStarted(item: PlayableMedia?) {
            var duration: Int? = MusifyPlayer.player?.duration
            tvDuration?.text = if(duration != null) MusifyPlayer.getTimeString(duration.toLong()) else "0:00"


            ibPlayPause?.setImageResource(R.mipmap.ic_pause)
        }

        override fun OnCurrentTrackEnded(item: PlayableMedia?) {

            ibPlayPause?.setImageResource(R.mipmap.ic_play_arrow)
        }

        override fun OnCurrentTrackTimeUpdated(item: PlayableMedia?, currentTime: Float, progressPercentage: Int) {

            sbMediaSeekbar?.progress = progressPercentage
            tvCurrentTime?.text = MusifyPlayer.getTimeString(currentTime.toLong())

        }
    }

    internal  var pagerListener = object:ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(p0: Int) {

        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

        }

        override fun onPageSelected(p0: Int) {
            Log.e("Viewpager", "Page: "+p0+ "   playlistIndex: "+MusifyPlayer.playlistIndex)
            if(p0 < MusifyPlayer.playlistIndex){
                MusifyPlayer.playPrevious()
            }
            else if(p0>MusifyPlayer.playlistIndex){
                MusifyPlayer.playNext()
            }

            ibDownloaded?.setImageResource(if(MusifyPlayer.getCurrentlyPlayedMusic().hasOfflineCopy)R.mipmap.ic_downloaded_arrow else R.mipmap.ic_download_arrow);
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        sbMediaSeekbar = findViewById(R.id.sbMediaSeekbar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvDuration = findViewById(R.id.tvDuration)

        tvTitle = findViewById(R.id.tvTitle)
        tvArtist = findViewById(R.id.tvArtist)

        ibPlayPause = findViewById(R.id.ibPlayPause)

        ibNext = findViewById(R.id.ibNext)
        ibPrevious = findViewById(R.id.ibPrevious)
        viewPager = findViewById(R.id.vpPlaylist)
        ibDownloaded = findViewById(R.id.ibDownloaded)
        ibRepeat = findViewById(R.id.ibRepeat)


        ibDownloaded?.setOnClickListener {

            MusifyPlayer.getCurrentlyPlayedMusic().downloadOffline(object:PlayableMedia.MediaDownloadCallbacks{
                override fun didDownload(downloaded: Boolean) {
                    viewPager?.adapter?.notifyDataSetChanged()
                    ibDownloaded?.setImageResource(if(MusifyPlayer.getCurrentlyPlayedMusic().hasOfflineCopy)R.mipmap.ic_downloaded_arrow else R.mipmap.ic_download_arrow)

                    Log.e("Download Task", "Downloaded:$downloaded")
                }
            })
        }

        ibTrackQueue?.setOnClickListener { startActivity(Intent(this,TrackQueueActivity::class.java)) }

        ibRepeat?.setOnClickListener {
            val repeatType = MusifyPlayer.getRepeatType()
            if(repeatType!=null) {
                when(repeatType){
                    MusifyPlayer.REPEAT_TYPE.NO_REPEAT ->
                        MusifyPlayer.setRepeatType(MusifyPlayer.REPEAT_TYPE.REPEAT_SINGLE)
                    MusifyPlayer.REPEAT_TYPE.REPEAT_SINGLE ->
                        MusifyPlayer.setRepeatType(MusifyPlayer.REPEAT_TYPE.REPEAT_ALL)
                    MusifyPlayer.REPEAT_TYPE.REPEAT_ALL ->
                        MusifyPlayer.setRepeatType(MusifyPlayer.REPEAT_TYPE.NO_REPEAT)
                }

                updateRepeatView()
            }
        }
        updateRepeatView()



        var ivDropPage:ImageView = findViewById(R.id.ivDropPage)
        ivDropPage.setOnClickListener { finish() }


        ibPlayPause?.setOnClickListener{ v->


            MusifyPlayer.playOrPause()
            ibPlayPause?.setImageResource(if (MusifyPlayer.isPlaying()) R.mipmap.ic_pause else R.mipmap.ic_play_arrow)

        }


        MusifyPlayer.addListener(playerListener)

//        playerListener.OnNewTrackStarted(MusifyPlayer.getCurrentlyPlayedMusic())


        sbMediaSeekbar?.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                MusifyPlayer.seekToTrackPercentage(seekBar?.progress!!)
            }
        })


        var adapter = PlaylistPagerAdapter(MusifyPlayer.getMusicPlaylist(),this)
        viewPager?.adapter = adapter
        viewPager?.currentItem = MusifyPlayer.playlistIndex




        viewPager?.addOnPageChangeListener(pagerListener)


        ibNext?.setOnClickListener {
            var index = viewPager!!.currentItem + 1
            if( index >= MusifyPlayer.getMusicPlaylist().size)
                index = 0

            viewPager?.removeOnPageChangeListener(pagerListener)
            viewPager?.setCurrentItem(index,true)
            viewPager?.addOnPageChangeListener(pagerListener)

            MusifyPlayer.playNext()
        }

        ibPrevious?.setOnClickListener {
            var index = viewPager!!.currentItem - 1
            if( index < 0)
                index = MusifyPlayer.getMusicPlaylist().size - 1

            viewPager?.removeOnPageChangeListener(pagerListener)
            viewPager?.setCurrentItem(index,true)
            viewPager?.addOnPageChangeListener(pagerListener)


            MusifyPlayer.playPrevious()
        }


    }
    fun updateRepeatView(){
        val repeatType = MusifyPlayer.getRepeatType()
        if(repeatType!=null) {
            when(repeatType){
                MusifyPlayer.REPEAT_TYPE.NO_REPEAT ->
                    ibRepeat?.setImageResource(R.mipmap.ic_repeat)
                MusifyPlayer.REPEAT_TYPE.REPEAT_SINGLE ->
                    ibRepeat?.setImageResource(R.mipmap.ic_repeat_single)
                MusifyPlayer.REPEAT_TYPE.REPEAT_ALL ->
                    ibRepeat?.setImageResource(R.mipmap.ic_repeat_all)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        MusifyPlayer.removeListener(playerListener)
    }


    internal inner class PlaylistPagerAdapter(var list:ArrayList<PlayableMedia>, var mContext: Context) : PagerAdapter() {


        internal var inflater:LayoutInflater? = null

        init {
             inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }


        override fun isViewFromObject(p0: View, p1: Any): Boolean {
            return  p0 == (p1 as View)
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var imageView = ImageView(mContext)
            imageView.layoutParams  = ViewGroup.LayoutParams(viewPager!!.width,viewPager!!.height)


            if(list[position].audio_image != null){
                Glide.with(mContext)
                        .load(list[position].audio_image)
                        .into(imageView)

            }else imageView.setImageResource(android.R.drawable.ic_menu_report_image)

            container.addView(imageView)


            return imageView
        }

         override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
