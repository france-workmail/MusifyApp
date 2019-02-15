package france.apps.musify.utils.models

import com.google.firebase.database.PropertyName
import france.apps.musify.MusifyApplication
import france.apps.musify.utils.cache.new_cache.MediaCacheCallback
import france.apps.musify.utils.cache.new_cache.MediaCacheWorkerTask
import java.io.FileInputStream

class PlayableMedia(@set:PropertyName("id")
                    @get:PropertyName("id")
                    var id: String="",
                    @set:PropertyName("audio_url")
                    @get:PropertyName("audio_url")
                    var audio_url: String="",
                    @set:PropertyName("audio_image")
                    @get:PropertyName("audio_image")
                    var audio_image: String = "",
                    @set:PropertyName("title")
                    @get:PropertyName("title")
                    var title: String = "",
                    @set:PropertyName("artist")
                    @get:PropertyName("artist")
                    var artist: String = "") {

    public var metadata: MediaMetadata? = null
    public var hasOfflineCopy = false
    var isDownloading = false

    init {
        MediaCacheWorkerTask(MusifyApplication.getAppContext(), object : MediaCacheCallback {
            override fun onSnapshotFound(stream: FileInputStream) {
                hasOfflineCopy = true
            }

            override fun onSnapshotMissing(url: String) {}
            override fun onSnapshotDownloaded(downloaded: Boolean) {}
        }).execute(audio_url)

    }





//     public var audio_image:String? = null
//    fun setAudioImageUrl(url:String):PlayableMedia{
//        audio_image = url
//        return this
//    }

    fun downloadOffline(callback:MediaDownloadCallbacks){
        if(!hasOfflineCopy) {
            MediaCacheWorkerTask(MusifyApplication.getAppContext(), object : MediaCacheCallback {
                override fun onSnapshotFound(stream: FileInputStream) {}

                override fun onSnapshotMissing(url: String) {}
                override fun onSnapshotDownloaded(downloaded: Boolean) {
                    hasOfflineCopy = downloaded
                    isDownloading = false
                    callback.didDownload(downloaded)
                }
            }, true).execute(audio_url)

            isDownloading = true
        }
    }

    public interface MediaDownloadCallbacks {
        fun didDownload(downloaded:Boolean)
    }

}
