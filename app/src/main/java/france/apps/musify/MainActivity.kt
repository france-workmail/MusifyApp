package france.apps.musify

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.*
import france.apps.musify.menufragments.HomeFragment
import android.util.Log
import france.apps.musify.utils.MusifyPlayer
import france.apps.musify.utils.models.PlayableMedia


class MainActivity : AppCompatActivity() {

    internal var tabBottomTabs: TabLayout? = null
    internal  var ibPlayPause: ImageButton? = null
    internal var ibPlayerUp: ImageButton? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabBottomTabs = findViewById(R.id.tabBottomTabs)
        var viewPager =  findViewById<ViewPager>(R.id.viewPager)

        var pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = pagerAdapter
        tabBottomTabs?.setupWithViewPager(viewPager)

        setupTabIcons()



        //

        ibPlayPause = findViewById(R.id.ibTrackPlayPause)
        ibPlayPause?.setOnClickListener{ v->


            MusifyPlayer.playOrPause()
            (v as ImageButton).setImageResource(if (MusifyPlayer.isPlaying()) R.mipmap.ic_pause else R.mipmap.ic_play_arrow)

        }
        ibPlayerUp = findViewById(R.id.ibPlayerUp)
        ibPlayerUp?.setOnClickListener { v: View? ->
            Toast.makeText(this,"Open Player", Toast.LENGTH_SHORT).show()

//            startActivity(Intent(this,PlayerActivity.class))
            this.startActivity(Intent(this,PlayerActivity::class.java))
        }

        tvTrackInfo = findViewById(R.id.tvTrackInfo)
        progressBar = findViewById(R.id.progressBar)



        var item = PlayableMedia(null, "https://videokeman.com/dload/0ydl/17/04/Bruno_Mars_-_That_s_What_I_Like_Lyrics_Lyric_Video.vkm", "That's what I like")
        var item2 = PlayableMedia(null, "https://videokeman.com/dload/nm3/041411/Bruno_Mars_-_The_Lazy_Song.vkm", "The Lazy Song")
        var item3 = PlayableMedia(null,"https://videokeman.com/dload/nm5/0221/Bruno_Mars_-_When_I_Was_Your_Manx.vkm", "When I was your man")



        item.setCoverImageUrl("http://www.aaminc.com/images/made/89edeb5e9990e69a/Thats_What_I_like_400_400_90_c1.jpg")
        item2.setCoverImageUrl( "https://streamd.hitparade.ch/cdimages/bruno_mars-the_lazy_song_s.jpg")
        item3.setCoverImageUrl("https://ichef.bbci.co.uk/images/ic/512x512/p01bv8nz.jpg")


        MusifyPlayer.addListener(object : MusifyPlayer.OnPlayerChangesListener{
            override fun OnListenerAttached(item: PlayableMedia?) {

            }

            override fun OnPause(item: PlayableMedia?) {
            }

            override fun OnPlay(item: PlayableMedia?) {
            }

            override fun OnNewTrackOpened(item: PlayableMedia?) {


                var title = item?.metadata?.title
                var artist = item?.metadata?.artist
                title = title ?: "No Title" //elvis expression
                artist = artist ?: "No artist"

                tvTrackInfo?.text = title.plus(" - ").plus(artist)

                Log.e("tag","New Track opened")

            }

            override fun OnNewTrackStarted(item: PlayableMedia?) {


            }

            override fun OnCurrentTrackEnded(item: PlayableMedia?) {

            }

            override fun OnCurrentTrackTimeUpdated(item: PlayableMedia?, currentTime: Float, progressPercentage:Int) {
                progressBar?.progress =progressPercentage

            }
        } )


       var list =  ArrayList<PlayableMedia>()
        list.add(item)
        list.add(item2)
        list.add(item3)

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
                fragment = Fragment()
            } else if (position == 2) {
                fragment = Fragment()
            }else if (position == 3) {
                fragment = Fragment()
            }else if (position == 4) {
                fragment = Fragment()
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
