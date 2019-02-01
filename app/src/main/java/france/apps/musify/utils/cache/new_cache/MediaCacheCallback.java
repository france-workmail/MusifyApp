package france.apps.musify.utils.cache.new_cache;

import java.io.FileInputStream;

public interface MediaCacheCallback {
    void onSnapshotFound(FileInputStream inputStream);
    void onSnapshotMissing(String url);
    void onSnapshotDownloaded(boolean downloaded);
}
