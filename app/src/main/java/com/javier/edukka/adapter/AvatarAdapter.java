package com.javier.edukka.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.javier.edukka.R;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
    private final List<Drawable> data;
    private int selectedPos = 0;

    public AvatarAdapter(List<Drawable> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_row, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.avatar.setImageDrawable(data.get(position));
        holder.itemView.setBackgroundColor(selectedPos == position ? 0xFF3F51B5 : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int pos) {
        notifyItemChanged(selectedPos);
        selectedPos = pos;
        notifyItemChanged(selectedPos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;

        private ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                }
            });
        }
    }

}