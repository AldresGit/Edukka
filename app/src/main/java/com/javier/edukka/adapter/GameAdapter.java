package com.javier.edukka.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.service.HelperClient;
import com.javier.edukka.view.GameActivity;
import com.javier.edukka.view.GameModifyActivity;

import java.util.ArrayList;
import java.util.Locale;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> implements Filterable {
    private final ArrayList<GameModel> mArrayList;
    private ArrayList<GameModel> mFilteredList;

    public GameAdapter(ArrayList<GameModel> arrayList) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.game_name.setText(mFilteredList.get(i).getTitle());
        viewHolder.game_desc.setText(mFilteredList.get(i).getDescription());
        if (Locale.getDefault().getLanguage().equals("es")) {
            String level = HelperClient.levelTranslateEs(mFilteredList.get(i).getDifficulty());
            viewHolder.game_level.setText(level);
        } else {
            String upperString = mFilteredList.get(i).getDifficulty().substring(0,1).toUpperCase() + mFilteredList.get(i).getDifficulty().substring(1);
            viewHolder.game_level.setText(upperString);
        }
        if (Integer.parseInt(mFilteredList.get(i).getVote()) >= 0) {
            viewHolder.game_vote.setText("+"+mFilteredList.get(i).getVote());
            viewHolder.game_vote.setTextColor(0xFF009688);
        } else {
            viewHolder.game_vote.setText(mFilteredList.get(i).getVote());
            viewHolder.game_vote.setTextColor(0xFFD32F2F);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mArrayList;
                } else {
                    ArrayList<GameModel> filteredList = new ArrayList<>();
                    for (GameModel gameModel : mArrayList) {
                        if (gameModel.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(gameModel);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<GameModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView game_name;
        private final TextView game_desc;
        private final TextView game_level;
        private final TextView game_vote;

        private ViewHolder(View view) {
            super(view);
            game_name = (TextView) view.findViewById(R.id.game_name);
            game_desc = (TextView) view.findViewById(R.id.game_desc);
            game_level = (TextView) view.findViewById(R.id.game_level);
            game_vote = (TextView) view.findViewById(R.id.game_vote);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = Integer.parseInt(mFilteredList.get(getAdapterPosition()).getId());
                    String s = mFilteredList.get(getAdapterPosition()).getSubject();
                    Context context = v.getContext();

                    if(UserSingleton.getInstance().getUserModel().getRole().equals("teacher")) {
                        if(UserSingleton.getInstance().getUserModel().getClassId().equals("1")) {
                            Toast.makeText(context, R.string.edit_base_game_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(context, GameModifyActivity.class);
                            intent.putExtra(GameActivity.EXTRA_POSITION, i);
                            intent.putExtra(GameActivity.EXTRA_SUBJECT, s);
                            context.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(context, GameActivity.class);
                        intent.putExtra(GameActivity.EXTRA_POSITION, i);
                        intent.putExtra(GameActivity.EXTRA_SUBJECT, s);
                        context.startActivity(intent);
                    }


                }
            });
        }
    }

}