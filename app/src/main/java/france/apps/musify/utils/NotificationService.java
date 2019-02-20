package france.apps.musify.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import france.apps.musify.MainActivity;
import france.apps.musify.PlayerActivity;
import france.apps.musify.R;
import france.apps.musify.utils.models.PlayableMedia;

public class NotificationService extends Service {

    @Override
    public void onDestroy() {
//        MusifyPlayer.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
           Log.i(LOG_TAG, "Service Started");
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
            MusifyPlayer.playPrevious();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
            MusifyPlayer.playOrPause();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
            MusifyPlayer.playNext();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
//        return START_STICKY;
         return super.onStartCommand(intent, flags, startId);
    }


    Notification status;
    private final String LOG_TAG = "NotificationService";
    RemoteViews views = null;
    RemoteViews bigViews = null;

    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);
// showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));
        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);
        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);
        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);
        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);


        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setImageViewResource(R.id.status_bar_play,
                R.mipmap.ic_play_arrow);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.mipmap.ic_play_arrow);
        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");
        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");
        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_headphones;
        status.contentIntent = pendingIntent;



        if(notificationTarget==null)
            notificationTarget = new NotificationTarget(
                    this,
                    R.id.status_bar_album_art,
                    views,
                    status,
                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

        if(listener!=null)
            listener.OnNewTrackOpened(MusifyPlayer.getCurrentlyPlayedMusic());
        MusifyPlayer.addListener(listener);


        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);



    }

    private NotificationTarget notificationTarget;
    private MusifyPlayer.OnPlayerChangesListener listener = new MusifyPlayer.OnPlayerChangesListener() {
        @Override
        public void OnPause(PlayableMedia item) {




            views.setImageViewResource(R.id.status_bar_play,
                    R.mipmap.ic_play_arrow);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.mipmap.ic_play_arrow);
        }

        @Override
        public void OnPlay(PlayableMedia item) {


            views.setImageViewResource(R.id.status_bar_play,
                    R.mipmap.ic_pause);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.mipmap.ic_pause);


        }

        @Override
        public void OnNewTrackOpened(PlayableMedia item) {

            if(item==null)return;

            String title = item.getTitle();
            String artist = item.getArtist();


            views.setTextViewText(R.id.status_bar_track_name, title);
            bigViews.setTextViewText(R.id.status_bar_track_name, title);
            views.setTextViewText(R.id.status_bar_artist_name, artist);
            bigViews.setTextViewText(R.id.status_bar_artist_name, artist);






            Glide
                .with(NotificationService.this)
                .asBitmap()
                .load(item.getAudio_image())
                .into(notificationTarget);
        }

        @Override
        public void OnNewTrackStarted(PlayableMedia item) {
            OnPlay(item);
        }

        @Override
        public void OnCurrentTrackEnded(PlayableMedia item) {

        }

        @Override
        public void OnCurrentTrackTimeUpdated(PlayableMedia item, float currentTime, int progressPercentage) {
        }

        @Override
        public void OnListenerAttached(PlayableMedia item) {

        }
    };

}
