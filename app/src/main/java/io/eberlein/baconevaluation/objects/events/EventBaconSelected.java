package io.eberlein.baconevaluation.objects.events;

import io.eberlein.baconevaluation.objects.Bacon;

public class EventBaconSelected {
    private Bacon bacon;

    public EventBaconSelected(Bacon bacon){
        this.bacon = bacon;
    }

    public Bacon getBacon() {
        return bacon;
    }
}
