package com.fusoft.walkboner.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fusoft.walkboner.R;
import com.fusoft.walkboner.models.Message;

import java.text.DateFormat;
import java.util.ArrayList;

import de.dlyt.yanndroid.oneui.view.RecyclerView;

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

        TextView messageTV,dateTV;
        MessageInViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.sender_message_text);
            dateTV = itemView.findViewById(R.id.sender_name_date_text);
        }
        void bind(int position) {
            Message messageModel = list.get(position);
            messageTV.setText(messageModel.getMessage());
            dateTV.setText("Mamiko" + "·" + DateFormat.getTimeInstance(DateFormat.SHORT).format(Long.parseLong(messageModel.getSendedAt())));
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV,dateTV;
        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.sender_message_text);
            dateTV = itemView.findViewById(R.id.sender_name_date_text);
        }
        void bind(int position) {
            Message messageModel = list.get(position);
            messageTV.setText(messageModel.getMessage());
            dateTV.setText("Mamiko" + " · " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Long.parseLong(messageModel.getSendedAt())));
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
