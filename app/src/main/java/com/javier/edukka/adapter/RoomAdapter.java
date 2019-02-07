package com.javier.edukka.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.app.Config;
import com.javier.edukka.controller.UserSingleton;
import com.javier.edukka.model.MultiplayerGameModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.view.CreateRoomActivity;
import com.javier.edukka.view.SearchRoomActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final ArrayList<MultiplayerGameModel> list;
    Context context;

    public RoomAdapter(ArrayList<MultiplayerGameModel> roomList, Context context) {
        list = roomList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_room, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if (list.get(i).getData() != null) {
            int resourceId = context.getResources().getIdentifier(list.get(i).getData().split(";")[1], "drawable", context.getPackageName());
            holder.userImage.setImageResource(resourceId);
            String text = context.getResources().getString(R.string.game_room) + " " +  list.get(i).getData().split(";")[0];
            holder.userName.setText(text);
            if(list.get(i).getExtra().equals("0")) {
                holder.extraPoints.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView userName;
        private final ImageView userImage;
        private final ImageView extraPoints;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userImage = (ImageView) itemView.findViewById(R.id.user_image);
            extraPoints = (ImageView) itemView.findViewById(R.id.extra_points);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int roomId = Integer.parseInt(list.get(getAdapterPosition()).getId());
                    int user2 = Integer.parseInt(UserSingleton.getInstance().getUserModel().getId());

                    SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
                    String myregId = pref.getString("regId", null);
                    String regId = list.get(getAdapterPosition()).getIdUser1();

                    RestInterface restInterface = RetrofitClient.getInstance();

                    //----------------Enviar mensaje----------------------

                    String message = "hello" + ";" + UserSingleton.getInstance().getUserModel().getName() +
                            ";" + UserSingleton.getInstance().getUserModel().getImage() + ";" + myregId;

                    Call<Void> requestMessage = restInterface.sendMessage(regId, message);
                    requestMessage.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(context, R.string.signup_success, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                    //---------------Enviar mensaje-----------------------

                    /*
                    Call<UserModel> request = restInterface.joinRoom(user2, myregId, "connected", roomId);

                    request.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            UserModel model = response.body();
                            if (model.getId()==null) {
                                Toast.makeText(context, R.string.time_limit_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(context, CreateRoomActivity.class);
                                i.putExtra(SearchRoomActivity.ID_ROOM, String.valueOf(roomId));
                                i.putExtra(SearchRoomActivity.NAME_PLAYER1, model.getName());
                                i.putExtra(SearchRoomActivity.IMAGE_PLAYER1, model.getImage());
                                i.putExtra(SearchRoomActivity.ROLE, "guest");
                                context.startActivity(i);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            Log.d("Error",t.getMessage());
                        }
                    });

                    */


                }
            });
        }
    }
}
