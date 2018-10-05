package france.apps.musify.utils.models

import france.apps.musify.utils.models.MediaMetadata

class PlayableMedia(var id: String?, var url: String?, var title: String?) {

    public var metadata: MediaMetadata? = null

    init {

        if (url != null)
            metadata = MediaMetadata(url)

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
