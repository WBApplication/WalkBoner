/*
 * Copyright (c) 2022 - WalkBoner.
 * ItemLongClickListener.java
 */

package com.fusoft.walkboner.adapters.recyclerview;

import com.fusoft.walkboner.models.album.Album;

public interface ItemLongClickListener {
    default void onAlbumLongClicked(Album album) {}
}
