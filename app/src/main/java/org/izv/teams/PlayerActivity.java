package org.izv.teams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.izv.teams.model.data.Jugador;
import org.izv.teams.view.adapter.PlayerViewAdapter;
import org.izv.teams.view.model.PlayerViewModel;
import org.izv.teams.view.operations.BeforeCrud;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private static final String KEY_TEAM = "team";
    private RecyclerView rvPlayer;
    private FloatingActionButton fabPlayer;
    private EditText etPlayerDialogName, etPlayerDialogLastName;
    private ImageView ivPlayerDialog;
    private Button btPlayerDialogCancel, btPlayerDialogAccept;
    private AlertDialog alertDialog;
    private PlayerViewAdapter adapter;
    private PlayerViewModel viewModel;
    private Uri uri;
    private static final int PHOTO_SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initComponents().assignEvents();
    }

    private PlayerActivity initComponents() {
        rvPlayer = findViewById(R.id.rvPlayer);
        fabPlayer = findViewById(R.id.fabPlayer);
        viewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
        adapter = new PlayerViewAdapter(this);
        rvPlayer.setAdapter(adapter);
        rvPlayer.setLayoutManager(new LinearLayoutManager(this));
        return this;
    }

    private PlayerActivity createInsertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.player_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        return this;
    }

    private PlayerActivity initDialogComponents() {
        etPlayerDialogName = alertDialog.findViewById(R.id.etDialogPlayerName);
        etPlayerDialogLastName = alertDialog.findViewById(R.id.etDialogPlayerLastName);
        ivPlayerDialog = alertDialog.findViewById(R.id.ivPlayerDialog);
        btPlayerDialogCancel = alertDialog.findViewById(R.id.btPlayerDialogCancel);
        btPlayerDialogAccept = alertDialog.findViewById(R.id.btPlayerDialogAccept);
        return this;
    }

    private PlayerActivity assignDialogEvents(final BeforeCrud<Jugador> beforeCrud) {
        btPlayerDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jugador jugador = new Jugador();
                String name = etPlayerDialogName.getText().toString();
                String lastName = etPlayerDialogLastName.getText().toString();

                if (!name.isEmpty() && !lastName.isEmpty()) {
                    jugador.setNombre(name);
                    jugador.setApellidos(lastName);
                    if (uri != null) {
                        saveSelectedImageInFile(uri, name + ".png");
                        uri = null;
                    }
                    beforeCrud.doIt(jugador);
                    alertDialog.cancel();
                }
            }
        });
        btPlayerDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        ivPlayerDialog.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
                intent.setType("*/*");
                String[] types = {"image/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
                startActivityForResult(intent, PHOTO_SELECTED);
            }
        });
        return this;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SELECTED && resultCode == Activity.RESULT_OK && null != data) {
            uri = data.getData();
            try {
                InputStream in = this.getContentResolver().openInputStream(uri);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                ivPlayerDialog.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSelectedImageInFile(Uri uri, String name) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT < 28) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                bitmap = null;
            }
        } else {
            try {
                final InputStream in = this.getContentResolver().openInputStream(uri);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                //ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                //bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                bitmap = null;
            }
        }
        if (bitmap != null) {
            File file = saveBitmapInFile(bitmap, name);
            if (file != null) {
                viewModel.upload(file);
            }
        }
    }

    private File saveBitmapInFile(Bitmap bitmap, String name) {
        File file = new File(getFilesDir(), name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            file = null;
        }
        return file;
    }

    private PlayerActivity createDialog(final Jugador jugador, String message, String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                viewModel.delete(jugador);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
        return this;
    }

    private PlayerActivity setInsertDialogValues(Jugador jugador) {
        etPlayerDialogName.setText(jugador.getNombre());
        etPlayerDialogLastName.setText(jugador.getApellidos());
        return this;
    }


    private PlayerActivity assignEvents() {
        fabPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createInsertDialog()
                        .initDialogComponents()
                        .assignDialogEvents(new BeforeCrud<Jugador>() {
                            @Override
                            public void doIt(Jugador jugador) {
                                long teamId = getIntent().getLongExtra(KEY_TEAM, 0);
                                jugador.setIdequipo(teamId);
                                viewModel.add(jugador);
                            }
                        });
            }
        });
        viewModel.getAll().observe(this, new Observer<List<Jugador>>() {
            @Override
            public void onChanged(List<Jugador> jugadores) {
                List<Jugador> filtered = new ArrayList<>();
                long teamId = getIntent().getLongExtra(KEY_TEAM, 0);
                for (Jugador jug : jugadores) {
                    if (jug.getIdequipo() == teamId) {
                        filtered.add(jug);
                    }
                }
                adapter.setPlayers(filtered);
            }
        });
        adapter.setOnClickListener(new PlayerViewAdapter.OnClickListener() {
            @Override
            public void onItemClick(final Jugador jugador, View view) {
                switch (view.getId()) {
                    case R.id.ivPlayerDelete:
                        createDialog(jugador, getString(R.string.deleteTeam), getString(R.string.titleDelteTeam));
                        break;
                    case R.id.ivPlayerUpdate:
                        createInsertDialog()
                                .initDialogComponents()
                                .setInsertDialogValues(jugador)
                                .assignDialogEvents(new BeforeCrud<Jugador>() {
                                    @Override
                                    public void doIt(Jugador player) {
                                        player.setId(jugador.getId());
                                        player.setIdequipo(jugador.getIdequipo());
                                        Log.v("xyz", player.getNombre() + "nombre" + player.getApellidos() + "apellidos" + player.getIdequipo() + "idquipo");
                                        viewModel.update(player);
                                    }
                                });
                        break;
                }
            }
        });
        return this;
    }
}
