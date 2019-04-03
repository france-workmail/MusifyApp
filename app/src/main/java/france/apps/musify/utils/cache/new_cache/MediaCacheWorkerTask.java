package france.apps.musify.utils.cache.new_cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import france.apps.musify.MusifyApplication;

public class MediaCacheWorkerTask extends AsyncTask<String, Void, FileInputStream> {

    private MediaCacheCallback callback = null;
    private Context context = null;


    private DiskLruCache.Editor currentEditor = null;

    private String urlToDownload = null;
    private boolean downloadWhenNotInCache = false;

    public MediaCacheWorkerTask(MediaCacheCallback callback) {
        this.context = MusifyApplication.getAppContext();
        this.callback = callback;
    }
    public MediaCacheWorkerTask(MediaCacheCallback callback, boolean downloadWhenNotInCache) {
        this(callback);
        this.downloadWhenNotInCache = downloadWhenNotInCache;
    }

    private String fileKey = null;
    @Override
    protected FileInputStream doInBackground(String... params) {
        urlToDownload = params[0];
        // Application class where i did open DiskLruCache
        DiskLruCache cache = MusifyApplication.getDiskCache(context);
        if (cache == null)
            return null;
        String key = hashKeyForDisk(urlToDownload);
        final int DISK_CACHE_INDEX = 0;
        long currentMaxSize = cache.getMaxSize();
        float percentageSize = Math.round((cache.size() * 100.0f) / currentMaxSize);
        if (percentageSize >= 90) // cache size reaches 90%
            cache.setMaxSize(currentMaxSize + (15 * 1024 * 1024)); // increase size to 15MB
        try {
            DiskLruCache.Snapshot snapshot = cache.get(key);
            if (snapshot == null) {
                Log.e(getTag(), "Snapshot is not available");
//                DiskLruCache.Editor editor = cache.edit(key);
//                if (editor != null) {
//                    if (downloadUrlToStream(data, editor.newOutputStream(DISK_CACHE_INDEX)))
//                        editor.commit();
//                    else
//                        editor.abort();
//                }
//                snapshot = cache.get(key);

                fileKey = key;
//                currentEditor = cache.edit(key);

//                urlToDownload = data;
//                snapshot.getInputStream(DISK_CACHE_INDEX);


                return null;
            } else
                Log.e(getTag(), "Snapshot found sending");
            if (snapshot != null)
                return (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(getTag(), "File stream is null");
        return null;
    }


    @Override
    protected void onPostExecute(FileInputStream fileInputStream) {
        super.onPostExecute(fileInputStream);
        if (callback != null) {
            if (fileInputStream != null)
                callback.onSnapshotFound(fileInputStream);
            else {
                callback.onSnapshotMissing(urlToDownload);

//                if(currentEditor != null && urlToDownload!=null && downloadWhenNotInCache){
//                    new DownloadUrlToStreamTask(callback).execute();
//                }
                if( urlToDownload!=null && downloadWhenNotInCache){

                    try {


                        currentEditor = null;
                        currentEditor = MusifyApplication.getDiskCache(context).edit(fileKey);
                        if(currentEditor == null){
                            callback.onSnapshotDownloaded(false);
                            return;
                        }

                        new DownloadUrlToStreamTask(callback).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
//                else{
//                    try {
//                        if (currentEditor != null) {
//                            currentEditor.abort();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }catch (IllegalStateException ex){
//                        ex.printStackTrace();
//                    }
//                }
            }
        }
        callback = null;
        context = null;
    }

    class DownloadUrlToStreamTask extends AsyncTask<Void,Void,Boolean>{


        MediaCacheCallback downloadCallback;

        public DownloadUrlToStreamTask(MediaCacheCallback downloadCallback) {
            this.downloadCallback = downloadCallback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            final int DISK_CACHE_INDEX = 0;
            try {

                boolean downloaded = downloadUrlToStream(urlToDownload,currentEditor.newOutputStream(DISK_CACHE_INDEX));

                if(downloaded) {
                    Log.e("Cache", "Cached media file: "+ urlToDownload);
                    currentEditor.commit();
                }
                else
                    currentEditor.abortUnlessCommitted();

                return downloaded;

            } catch (IOException e) {
                e.printStackTrace();
//                downloadCallback.onSnapshotDownloaded(false);
                currentEditor.abortUnlessCommitted();
                return  false;
            }


        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            downloadCallback.onSnapshotDownloaded(s);
        }

        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
            HttpURLConnection urlConnection = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = urlConnection.getInputStream();
                // you can use BufferedInputStream and BufferOuInputStream
                IOUtils.copy(stream, outputStream);
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(stream);
                Log.i(getClass().getSimpleName(), "Stream closed all done");
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    IOUtils.close(urlConnection);
            }
            return false;
        }
    }



    private String getTag() {
        return getClass().getSimpleName();
    }

    private static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1)
                sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }


    public static String getHashKeyForString(String str){
        return hashKeyForDisk(str);
    }
}
