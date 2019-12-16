package org.izv.teams.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.izv.teams.model.rest.repository.TeamRepository;
import org.izv.teams.model.data.Equipo;

import java.io.File;
import java.util.List;

public class MainViewModel extends AndroidViewModel{
    private TeamRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new TeamRepository();
    }


    public void add(Equipo object) {
        repository.add(object);
    }


    public void delete(Equipo object) {
        repository.delete(object);
    }


    public void update(Equipo object) {
        repository.update(object);
    }


    public void upload(File file) {
        repository.upload(file);
    }

    public LiveData<List<Equipo>> getAll() {
        return repository.getAll();
    }
}
