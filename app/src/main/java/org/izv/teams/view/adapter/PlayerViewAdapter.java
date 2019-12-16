package org.izv.teams.view.adapter;

import android.annotation.SuppressLint;
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
import org.izv.teams.model.data.Jugador;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PlayerViewAdapter extends RecyclerView.Adapter<PlayerViewAdapter.PlayerViewHolder> {
    private LayoutInflater li;
    private List<Jugador> listaJugadores;
    private PlayerViewAdapter.OnClickListener onClickListener;
    private Context context;
    private final String PATH;
    private String url = "3.83.157.88";

    private class GetBitmap extends AsyncTask<Jugador, Void, Bitmap> {
        private ImageView ivPlayer;


        public GetBitmap(PlayerViewAdapter.PlayerViewHolder viewHolder) {
            ivPlayer = viewHolder.ivPlayer;
        }

        protected Bitmap doInBackground(Jugador... jugadores) {
            return getBitmapFromURL(PATH + jugadores[0].getNombre() + ".png");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ivPlayer.setImageBitmap(bitmap);
            } else {
                ivPlayer.setImageDrawable(context.getDrawable(R.drawable.player));
            }
        }
    }

    public PlayerViewAdapter(Context context) {
        PATH = "http://" + url + "/web/equipo/public/upload/";
        this.context = context;
        li = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayerViewHolder(li.inflate(R.layout.item_player, parent, false));
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public interface OnClickListener {
        void onItemClick(Jugador jugador, View view);
    }

    public void setOnClickListener(PlayerViewAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, final int position) {
        if (listaJugadores != null) {
            final Jugador jugador = listaJugadores.get(position);
            holder.tvPlayerName.setText(context.getString(R.string.etPlayerName) + ": " + jugador.getNombre());
            holder.tvLastName.setText(context.getString(R.string.etPlayerLastName) + ": " +jugador.getApellidos());

            new PlayerViewAdapter.GetBitmap(holder).execute(jugador);
            holder.ivPlayerUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(jugador, v);
                }
            });
            holder.ivPlayerDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(jugador, v);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        int elementos = 0;
        if (listaJugadores != null) {
            elementos = listaJugadores.size();
        }
        return elementos;
    }

    public void setPlayers(List<Jugador> jugadores) {
        listaJugadores = jugadores;
        notifyDataSetChanged();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlayerName, tvLastName;
        private ImageView ivPlayer, ivPlayerUpdate, ivPlayerDelete;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            ivPlayer = itemView.findViewById(R.id.ivPlayer);
            ivPlayerUpdate = itemView.findViewById(R.id.ivPlayerUpdate);
            ivPlayerDelete = itemView.findViewById(R.id.ivPlayerDelete);
        }
    }
}
