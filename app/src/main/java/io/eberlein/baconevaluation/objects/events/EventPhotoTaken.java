package io.eberlein.baconevaluation.objects.events;

import android.net.Uri;

public class EventPhotoTaken {
    private Uri uri;

    public EventPhotoTaken(Uri uri){
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
