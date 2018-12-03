package france.apps.musify.utils.models

class PlayableMedia(var id: String="", var audio_url: String="", var title: String = "", var artist: String = "") {

    public var metadata: MediaMetadata? = null

    init {

//        if (audio_url != null && !Constants.OFFLINE_MODE) {
//            try {
//                metadata = MediaMetadata(audio_url)
//            }catch (e: RuntimeException){
//                e.printStackTrace()
//            }
//        }

    }



     public var audio_image:String? = null
    fun setAudioImageUrl(url:String):PlayableMedia{
        audio_image = url
        return this
    }

}
