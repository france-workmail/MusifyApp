package france.apps.musify

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.*
import france.apps.musify.menufragments.HomeFragment
import android.util.Log
import france.apps.musify.menufragments.BrowseFragment
import france.apps.musify.utils.Constants
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.models.PlayableMedia


class MainActivity : AppCompatActivity() {

    internal var tabBottomTabs: TabLayout? = null
    internal  var ibPlayPause: ImageButton? = null
    internal var clTrackIndicator: ConstraintLayout? = null

    internal var player: MediaPlayer? = null

    internal var tvTrackInfo: TextView? = null
    internal var progressBar:ProgressBar? = null

//    internal var handler: Handler? = Handler()
//    internal var runnable:Runnable? = object:Runnable {
//        override fun run() {
//            var curTime = player?.currentPosition
//            var maxTime = player?.duration
//            var div:Float = curTime!!.toFloat() / maxTime!!.toFloat()
//            var prog:Double = div * 100.00
//
//            Log.e("Progress",""+(curTime!!) + "/"+ (maxTime!!)+"  = "+prog.toInt())
//
//            progressBar?.progress = prog.toInt()
//            handler?.postDelayed(this, 10)
//
//        }
//    }

    internal var playerListener:MusifyPlayer.OnPlayerChangesListener  = object:MusifyPlayer.OnPlayerChangesListener{
        override fun OnListenerAttached(item: PlayableMedia?) {

            var title = item?.metadata?.title
            var artist = item?.metadata?.artist
            title = title ?: item?.title //elvis expression
            artist = artist ?: "No artist"

            tvTrackInfo?.text = title.plus(" - ").plus(artist)

            ibPlayPause?.setImageResource(if(MusifyPlayer.isPlaying()) R.mipmap.ic_pause else R.mipmap.ic_play_arrow)
        }

        override fun OnPause(item: PlayableMedia?) {
            ibPlayPause?.setImageResource(R.mipmap.ic_play_arrow)
        }

        override fun OnPlay(item: PlayableMedia?) {
            ibPlayPause?.setImageResource(R.mipmap.ic_pause)
        }

        override fun OnNewTrackOpened(item: PlayableMedia?) {



            var title = item?.metadata?.title
            var artist = item?.metadata?.artist
            title = title ?: item?.title //elvis expression
            artist = artist ?: "No artist"

            tvTrackInfo?.text = title.plus(" - ").plus(artist)

            Log.e("tag","New Track opened")

        }

        override fun OnNewTrackStarted(item: PlayableMedia?) {

            ibPlayPause?.setImageResource(R.mipmap.ic_pause)
        }

        override fun OnCurrentTrackEnded(item: PlayableMedia?) {
            ibPlayPause?.setImageResource(R.mipmap.ic_play_arrow)
        }

        override fun OnCurrentTrackTimeUpdated(item: PlayableMedia?, currentTime: Float, progressPercentage:Int) {
            progressBar?.progress =progressPercentage

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabBottomTabs = findViewById(R.id.tabBottomTabs)
        var viewPager =  findViewById<ViewPager>(R.id.viewPager)

        var pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = pagerAdapter
        tabBottomTabs?.setupWithViewPager(viewPager)

        setupTabIcons()


        ibPlayPause = findViewById(R.id.ibTrackPlayPause)
        ibPlayPause?.setOnClickListener{ v->


            MusifyPlayer.playOrPause()
            (v as ImageButton).setImageResource(if (MusifyPlayer.isPlaying()) R.mipmap.ic_pause else R.mipmap.ic_play_arrow)

        }
        clTrackIndicator = findViewById(R.id.clTrackIndicator)
        clTrackIndicator?.setOnClickListener { v: View? ->
//            Toast.makeText(this,"Open Player", Toast.LENGTH_SHORT).show()

//            startActivity(Intent(this,PlayerActivity.class))
            this.startActivity(Intent(this,PlayerActivity::class.java))
        }

        tvTrackInfo = findViewById(R.id.tvTrackInfo)
        progressBar = findViewById(R.id.progressBar)






        //http://www.noiseaddicts.com/samples_1w72b820/2544.mp3     |||  https://videokeman.com/dload/0ydl/17/04/Bruno_Mars_-_That_s_What_I_Like_Lyrics_Lyric_Video.vkm
        var item = PlayableMedia(null, "https://www.sadecemp3indir.mobi/uploads/mp3/dd233c99a6a4dc221b190129ecea5465.mp3", "That's what I like")
        var item2 = PlayableMedia(null, "https://www.sadecemp3indir.mobi/uploads/mp3/a979d8f7190157d8b42c1a8f4edc4270.mp3", "Versace On the Floor")
        var item3 = PlayableMedia(null,"https://www.sadecemp3indir.mobi/uploads/mp3/c210e49b396c1c309baa9a2e4e22942d.mp3", "Love Yourself")
        var item4 = PlayableMedia(null,"https://www.sadecemp3indir.mobi/uploads/mp3/c957f382f6919a071162dbd095b9aaee.mp3", "24K Magic")



        item.setCoverImageUrl("http://www.aaminc.com/images/made/89edeb5e9990e69a/Thats_What_I_like_400_400_90_c1.jpg")
        item2.setCoverImageUrl( "http://skypip.com/wp-content/uploads/2017/04/images.jpg")
        item3.setCoverImageUrl("http://s2.glbimg.com/UNUU2P5SxHa_LP4kJcpgkr97MPI=/s.glbimg.com/jo/g1/f/original/2015/10/13/justin-bieber-purpose.jpg")
        item4.setCoverImageUrl("https://ih0.redbubble.net/image.424180221.6012/flat,550x550,075,f.u1.jpg")



        MusifyPlayer.addListener(playerListener)


        var list =  ArrayList<PlayableMedia>()
        list.add(item)
        list.add(item2)
        list.add(item3)
        list.add(item4)

        MusifyPlayer.playPlaylist(list)


    }





    internal fun setupTabIcons(){
        tabBottomTabs?.getTabAt(0)?.setIcon(R.mipmap.ic_home)
        tabBottomTabs?.getTabAt(1)?.setIcon(R.mipmap.ic_browse)
        tabBottomTabs?.getTabAt(2)?.setIcon(R.mipmap.ic_search)
        tabBottomTabs?.getTabAt(3)?.setIcon(R.mipmap.ic_radio)
        tabBottomTabs?.getTabAt(4)?.setIcon(R.mipmap.ic_library)
    }


    override  fun onDestroy() {
        super.onDestroy()
        MusifyPlayer.destroy()
    }

    internal inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            if (position == 0) {
                fragment = HomeFragment()
            } else if (position == 1) {
                fragment = BrowseFragment()
            } else if (position == 2) {
                fragment = BrowseFragment()
            }else if (position == 3) {
                fragment = BrowseFragment()
            }else if (position == 4) {
                fragment = BrowseFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 5
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Home"
                1 -> return "Browse"
                2 -> return "Search"
                3 -> return "Radio"
                4 -> return "Library"
            }

            return ""
        }

    }
}
