package org.izv.teams.model.rest.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.izv.teams.model.data.Jugador;
import org.izv.teams.model.rest.PlayerClient;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayerRepository {
    private static final String TAG = TeamRepository.class.getName();
    private PlayerClient client;
    private MutableLiveData<List<Jugador>> liveList = new MutableLiveData<>();
    private Retrofit retrofit;
    private String url = "3.83.157.88";

    public PlayerRepository() {
        retrieveApiClient(url);
        fetchAll();
    }

    public void add(Jugador object) {
        Call<Long> call = client.post(object);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
//                long resultado = response.body();
//                Log.v(TAG, response.raw().body().toString());
//                if (resultado > 0) {
//                    fetchAll();
//                }
                fetchAll();
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(TAG, t.getMessage());
            }

        });
    }

    public void delete(Jugador object) {
        Call<Long> call = client.delete(object.getId());
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
//                long resultado = response.body();
//                Log.v(TAG, response.raw().body().toString());
//                if (resultado > 0) {
//                    fetchAll();
//                }
                fetchAll();
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.v(TAG, t.getMessage());
            }
        });
    }

    public void fetchAll() {
        Call<List<Jugador>> call = client.getAll();
        call.enqueue(new Callback<List<Jugador>>() {

            @Override
            public void onResponse(Call<List<Jugador>> call, Response<List<Jugador>> response) {
                Log.v(TAG, response.raw().toString());
                liveList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Jugador>> call, Throwable t) {
                Log.v(TAG, t.getMessage());
            }
        });
    }

    public MutableLiveData<List<Jugador>> getAll() {
        return liveList;
    }

    public void retrieveApiClient(String url) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + url + "/web/equipo/public/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        client = retrofit.create(PlayerClient.class);
    }

    public void setUrl(String url) {
        retrieveApiClient(url);
    }

    public void update(Jugador object) {
        Call<Boolean> call = client.put(object.getId(), object);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                boolean resultado = response.body();
                Log.v("xyz", resultado + "resultadasdddddddddd");
                if (resultado) {
                    fetchAll();
                }
                //fetchAll();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }

    public void upload(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part request = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        Call<String> call = client.fileUpload(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.v(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.v(TAG, t.getLocalizedMessage());
            }
        });
    }
}