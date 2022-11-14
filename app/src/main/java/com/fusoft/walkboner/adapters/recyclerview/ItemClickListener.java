/*
 * Copyright (c) 2022 - WalkBoner.
 * ItemClickListener.java
 */

package com.fusoft.walkboner.adapters.recyclerview;

import com.fusoft.walkboner.models.Influencer;
import com.fusoft.walkboner.models.album.Album;

public interface ItemClickListener {
    default void onItemClick(Object object, int position) {}

    default void onItemClick(Album album, int position) {}

    default void onItemClick(Influencer influencer, int position) {}
}
