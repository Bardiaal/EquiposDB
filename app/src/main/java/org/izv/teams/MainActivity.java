package org.izv.teams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.izv.teams.model.data.Equipo;
import org.izv.teams.view.adapter.MainViewAdapter;
import org.izv.teams.view.model.MainViewModel;
import org.izv.teams.view.operations.BeforeCrud;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_TEAM = "team";
    private RecyclerView rvMain;
    private FloatingActionButton fabMain;
    private EditText etTeamDialogName, etTeamDialogCity, etTeamDialogStadium, etTeamDialogPeople;
    private ImageView ivTeamDialog;
    private Button btTeamDialogCancel, btTeamDialogAccept;
    private AlertDialog alertDialog;
    private MainViewAdapter adapter;
    private MainViewModel viewModel;
    private Uri uri;
    private static final int PHOTO_SELECTED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents().assignEvents();
    }

    private MainActivity initComponents() {
        rvMain = findViewById(R.id.rvMain);
        fabMain = findViewById(R.id.fabMain);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        adapter = new MainViewAdapter(this);
        rvMain.setAdapter(adapter);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        return this;
    }

    private MainActivity createInsertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.team_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        return this;
    }

    private MainActivity initDialogComponents() {
        etTeamDialogName = alertDialog.findViewById(R.id.etDialogTeamName);
        etTeamDialogCity = alertDialog.findViewById(R.id.etTeamDialogCity);
        etTeamDialogStadium = alertDialog.findViewById(R.id.etTeamDialogStadium);
        etTeamDialogPeople = alertDialog.findViewById(R.id.etTeamDialogPeople);
        ivTeamDialog = alertDialog.findViewById(R.id.ivTeamDialog);
        btTeamDialogCancel = alertDialog.findViewById(R.id.btTeamDialogCancel);
        btTeamDialogAccept = alertDialog.findViewById(R.id.btTeamDialogAccept);
        return this;
    }

    private MainActivity assignDialogEvents(final BeforeCrud<Equipo> beforeCrud) {
        btTeamDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Equipo equipo = new Equipo();
                String name = etTeamDialogName.getText().toString();
                String city = etTeamDialogCity.getText().toString();
                String stadium = etTeamDialogStadium.getText().toString();
                String people = etTeamDialogPeople.getText().toString();
                if (!name.isEmpty() && !city.isEmpty() && !stadium.isEmpty() && !people.isEmpty()) {
                    equipo.setNombre(name);
                    equipo.setCiudad(city);
                    equipo.setEstadio(stadium);
                    long p = 0;
                    try {
                        p = Long.parseLong(etTeamDialogPeople.getText().toString());
                    } catch (Exception ignored) {
                    }
                    equipo.setAforo(p);
                    if (uri != null) {
                        saveSelectedImageInFile(uri, name + ".png");
                        uri = null;
                    }
                    beforeCrud.doIt(equipo);
                    alertDialog.cancel();
                }
            }
        });
        btTeamDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        ivTeamDialog.setOnClickListener(new View.OnClickListener() {
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

    private MainActivity createDialog(final Equipo equipo, String message, String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                viewModel.delete(equipo);
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

    private MainActivity setInsertDialogValues(Equipo equipo) {
        etTeamDialogName.setText(equipo.getNombre());
        etTeamDialogCity.setText(equipo.getCiudad());
        etTeamDialogStadium.setText(equipo.getEstadio());
        etTeamDialogPeople.setText(String.valueOf(equipo.getAforo()));
        ivTeamDialog = alertDialog.findViewById(R.id.ivTeamDialog);
        return this;
    }


    private MainActivity assignEvents() {
        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createInsertDialog()
                        .initDialogComponents()
                        .assignDialogEvents(new BeforeCrud<Equipo>() {
                            @Override
                            public void doIt(Equipo equipo) {
                                viewModel.add(equipo);
                            }
                        });
            }
        });
        viewModel.getAll().observe(this, new Observer<List<Equipo>>() {
            @Override
            public void onChanged(List<Equipo> equipos) {
                adapter.setTeams(equipos);
            }
        });
        adapter.setOnClickListener(new MainViewAdapter.OnClickListener() {
            @Override
            public void onItemClick(final Equipo equipo, View view) {
                switch (view.getId()) {
                    case R.id.ivTeamDelete:
                        createDialog(equipo, getString(R.string.deleteTeam), getString(R.string.titleDelteTeam));
                        break;
                    case R.id.ivTeamUpdate:
                        createInsertDialog()
                                .initDialogComponents()
                                .setInsertDialogValues(equipo)
                                .assignDialogEvents(new BeforeCrud<Equipo>() {
                                    @Override
                                    public void doIt(Equipo team) {
                                        team.setId(equipo.getId());
                                        Log.v("xyz", team.toString());
                                        viewModel.update(team);
                                    }
                                });
                        break;
                }
            }
        });

        adapter.setOnLongClickListener(new MainViewAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClick(Equipo equipo) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class)
                        .putExtra(KEY_TEAM, equipo.getId());
                startActivity(intent);
            }
        });
        return this;
    }
}
