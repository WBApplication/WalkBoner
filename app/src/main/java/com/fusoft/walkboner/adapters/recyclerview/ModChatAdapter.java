package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.database.GetUserDetails;
import com.fusoft.walkboner.models.Message;
import com.fusoft.walkboner.models.User;
import com.fusoft.walkboner.views.Avatar;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ModChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<Message> list;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    public String currentUserUid;

    public ModChatAdapter(Context context, ArrayList<Message> list, String currentUserUid) { // you can pass other parameters in constructor
        this.context = context;
        this.list = list;
        this.currentUserUid = currentUserUid;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, dateTV;
        Avatar senderAvatarImage;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.sender_message_text);
            dateTV = itemView.findViewById(R.id.sender_name_date_text);
            senderAvatarImage = itemView.findViewById(R.id.sender_avatar_image);
        }

        void bind(int position) {
            Message messageModel = list.get(position);
            GetUserDetails.getByUid(messageModel.getSenderUid(), new GetUserDetails.UserDetailsListener() {
                @Override
                public void OnReceived(User user) {
                    if (user.isUserBanned()) {
                        senderAvatarImage.setAvatarOwnerPrivileges(Avatar.BANNED);
                    } else if (user.isUserAdmin()) {
                        senderAvatarImage.setAvatarOwnerPrivileges(Avatar.ADMIN);
                    } else if (user.isUserModerator()) {
                        senderAvatarImage.setAvatarOwnerPrivileges(Avatar.MODERATOR);
                    } else {
                        senderAvatarImage.setAvatarOwnerPrivileges(Avatar.USER);
                    }

                    if (!user.getUserAvatar().contentEquals("default")) {
                        senderAvatarImage.setImageFromUrl(user.getUserAvatar());
                    }

                    messageTV.setText(messageModel.getMessage());

                    if (!isMessageOlderThanOneDay(messageModel.getSendedAt())) {
                        dateTV.setText(user.getUserName() + " · " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Long.parseLong(messageModel.getSendedAt())));
                    } else {
                        Date date = new Date(Long.parseLong(messageModel.getSendedAt()));
                        dateTV.setText(user.getUserName() + " · " + new SimpleDateFormat("HH:mm dd/MM").format(date));
                    }
                }

                @Override
                public void OnError(String reason) {

                }
            });
        }
    }

    private boolean isMessageOlderThanOneDay(String messageTime) {
        long oneDayDuration = 86400000;
        long messageSendedAtAfterDay = Long.parseLong(messageTime) + oneDayDuration;
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();

        if (currentTime > messageSendedAtAfterDay) {
            return true;
        }

        return false;
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, dateTV;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.sender_message_text);
            dateTV = itemView.findViewById(R.id.sender_name_date_text);
        }

        void bind(int position) {
            Message messageModel = list.get(position);
            GetUserDetails.getByUid(messageModel.getSenderUid(), new GetUserDetails.UserDetailsListener() {
                @Override
                public void OnReceived(User user) {
                    messageTV.setText(messageModel.getMessage());

                    if (!isMessageOlderThanOneDay(messageModel.getSendedAt())) {
                        dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(Long.parseLong(messageModel.getSendedAt())));
                    } else {
                        Date date = new Date(Long.parseLong(messageModel.getSendedAt()));
                        dateTV.setText(new SimpleDateFormat("HH:mm dd/MM").format(date));
                    }
                }

                @Override
                public void OnError(String reason) {

                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_in, parent, false));
        }
        return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_out, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!list.get(position).getSenderUid().contentEquals(currentUserUid)) {
            ((MessageInViewHolder) holder).bind(position);
        } else {
            ((MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!list.get(position).getSenderUid().contentEquals(currentUserUid)) {
            return 1;
        } else {
            return 2;
        }
    }
}
