package com.fusoft.walkboner.database.funcions;

import com.fusoft.walkboner.models.Comment;

import java.util.List;

public interface PostCommentsListener {
    void OnCommentsReceived(List<Comment> comments);

    void OnError(String reason);
}
