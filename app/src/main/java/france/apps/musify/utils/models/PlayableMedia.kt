package france.apps.musify.utils.models

import france.apps.musify.utils.Constants
import france.apps.musify.utils.models.MediaMetadata

class PlayableMedia(var id: String?, var url: String?, var title: String?) {

    public var metadata: MediaMetadata? = null

    init {

//        if (url != null && !Constants.OFFLINE_MODE) {
//            try {
//                metadata = MediaMetadata(url)
//            }catch (e: RuntimeException){
//                e.printStackTrace()
//            }
//        }

    }

     public var coverImageUrl:String? = null


    fun setCoverImageUrl( url:String):PlayableMedia{
        coverImageUrl = url
        return this
    }
//    fun getCoverImageUrl():String{
//        return coverImageUrl
//    }

}
