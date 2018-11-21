package france.apps.musify.utils.cache.old;

import java.io.FileInputStream;

public interface OnCacheCallback {
    void onSuccess(FileInputStream stream);
    void onError();
}
