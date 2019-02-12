package com.javier.edukka.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javier.edukka.R;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.service.RestInterface;
import com.javier.edukka.service.RetrofitClient;
import com.javier.edukka.view.UpdatePointsActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder>  {
    private final ArrayList<UserModel> list;
    private final Context context;
    private final Activity activity;

    public PointsAdapter(ArrayList<UserModel> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if (list.get(i).getId() != null){
            int resourceId = context.getResources().getIdentifier(list.get(i).getImage(), "drawable", context.getPackageName());
            holder.avatar.setImageResource(resourceId);
            holder.userName.setText(list.get(i).getUsername());
            holder.name.setText(list.get(i).getName());
            holder.score.setText(list.get(i).getScore());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView userName;
        private final TextView name;
        private final TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.list_avatar);
            userName = (TextView) itemView.findViewById(R.id.list_title);
            name = (TextView) itemView.findViewById(R.id.list_desc);
            score = (TextView) itemView.findViewById(R.id.list_score);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAlertDialog(Integer.parseInt(list.get(getAdapterPosition()).getId())).show();
                }
            });
        }
    }

    public boolean checkValidation(String amount) {
        boolean result = true;

        if(amount.equals("")) {
            result = false;
        }

        return result;
    }

    public AlertDialog createAlertDialog(int id) {
        final int userId = id;
        final AlertDialog myAlertDialog;
        //final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View v = layoutInflater.inflate(R.layout.modify_points_dialog, null);

        builder.setView(v);

        final EditText amount = (EditText) v.findViewById(R.id.amount);
        Button extract = (Button) v.findViewById(R.id.extract);
        Button cancel = (Button) v.findViewById(R.id.cancel);
        Button add = (Button) v.findViewById(R.id.add);

        myAlertDialog = builder.create();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation(amount.getText().toString())) {
                    RestInterface restInterface = RetrofitClient.getInstance();
                    int points = Integer.parseInt(amount.getText().toString());
                    Call<UserModel> request = restInterface.updateUserScore(points, userId);
                    request.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            Toast.makeText(context, "Operacion realizada con exito", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {

                        }
                    });
                    myAlertDialog.dismiss();
                    activity.finish();
                    Intent i = new Intent(context, UpdatePointsActivity.class);
                    context.startActivity(i);
                } else {
                    amount.setError(context.getResources().getString(R.string.empty));
                }


            }
        });

        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation(amount.getText().toString())) {
                    RestInterface restInterface = RetrofitClient.getInstance();
                    int points = Integer.parseInt(amount.getText().toString());
                    points = -points;
                    Call<UserModel> request = restInterface.updateUserScore(points, userId);
                    request.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            Toast.makeText(context, "Operacion realizada con exito", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {

                        }
                    });
                    myAlertDialog.dismiss();
                    activity.finish();
                    Intent i = new Intent(context, UpdatePointsActivity.class);
                    context.startActivity(i);
                } else {
                    amount.setError(context.getResources().getString(R.string.empty));
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
            }
        });

        return myAlertDialog;

    }

}
