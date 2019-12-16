package org.izv.teams.view.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.izv.teams.R;
import org.izv.teams.model.data.Equipo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainViewAdapter extends RecyclerView.Adapter<MainViewAdapter.MainViewHolder> {
    private LayoutInflater li;
    private List<Equipo> listaEquipos;
    private Context context;
    private OnLongClickListener onLongClickListener;
    private String url = "3.83.157.88";
    private OnClickListener onClickListener;
    private final String PATH;

    private class GetBitmap extends AsyncTask<Equipo, Void, Bitmap> {
        private ImageView ivTeam;


        public GetBitmap(MainViewAdapter.MainViewHolder viewHolder) {
            ivTeam = viewHolder.ivTeam;
        }

        protected Bitmap doInBackground(Equipo... equipos) {
            return getBitmapFromURL(PATH + equipos[0].getNombre() + ".png");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ivTeam.setImageBitmap(bitmap);
            } else {
                ivTeam.setImageDrawable(context.getDrawable(R.drawable.shield));
            }
        }
    }

    public interface OnClickListener {
        void onItemClick(Equipo equipo, View view);
    }

    public interface OnLongClickListener {
        void onItemLongClick(Equipo equipo);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public MainViewAdapter(Context context) {
        PATH = "http://" + url + "/web/equipo/public/upload/";
        this.context = context;
        li = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(li.inflate(R.layout.item_team, parent, false));
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, final int position) {
        if (listaEquipos != null) {
            final Equipo equipo = listaEquipos.get(position);
            Log.v("xyz", equipo.getNombre() + "asdsad");
            holder.tvTeamName.setText(equipo.getNombre());
            holder.tvCity.setText(equipo.getCiudad());
            holder.tvStadium.setText(equipo.getEstadio());
            holder.tvPeople.setText(String.valueOf(equipo.getAforo()));
            new GetBitmap(holder).execute(equipo);
            holder.ivTeamUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(equipo, v);
                }
            });
            holder.ivTeamDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(equipo, v);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.onItemLongClick(equipo);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int elementos = 0;
        if (listaEquipos != null) {
            elementos = listaEquipos.size();
        }
        return elementos;
    }

    public void setTeams(List<Equipo> equipos) {
        listaEquipos = equipos;
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTeamName, tvCity, tvStadium, tvPeople;
        private ImageView ivTeam, ivTeamUpdate, ivTeamDelete;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvStadium = itemView.findViewById(R.id.tvStadium);
            tvPeople = itemView.findViewById(R.id.tvPeople);
            ivTeam = itemView.findViewById(R.id.ivTeam);
            ivTeamUpdate = itemView.findViewById(R.id.ivTeamUpdate);
            ivTeamDelete = itemView.findViewById(R.id.ivTeamDelete);
        }
    }
}