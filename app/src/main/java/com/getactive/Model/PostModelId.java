package com.getactive.Model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostModelId {

    @Exclude
    public String PostModelId;

    public <T extends PostModelId> T withId(@NonNull final String id) {
        this.PostModelId = id;
        return (T) this;
    }

}
