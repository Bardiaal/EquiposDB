package org.izv.teams.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.izv.teams.model.rest.repository.PlayerRepository;
import org.izv.teams.model.data.Jugador;

import java.io.File;
import java.util.List;

public class PlayerViewModel extends AndroidViewModel {
    private PlayerRepository repository;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayerRepository();
    }



    public void add(Jugador object) {
        repository.add(object);
    }


    public void delete(Jugador object) {
        repository.delete(object);
    }


    public void update(Jugador object) {
        repository.update(object);
    }


    public void upload(File file) {
        repository.upload(file);
    }

    public LiveData<List<Jugador>> getAll() {
        return repository.getAll();
    }
}
