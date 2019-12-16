package org.izv.teams.model.rest;

import org.izv.teams.model.data.Equipo;
import org.izv.teams.model.data.Jugador;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PlayerClient {
    @DELETE("jugador/{id}")
    Call<Long> delete(@Path("id") long id);

    @GET("jugador/{id}")
    Call<Jugador> get(@Path("id") long id);

    @GET("jugador")
    Call<List<Jugador>> getAll();

    @POST("jugador")
    Call<Long> post(@Body Jugador object);

    @PUT("jugador/{id}")
    Call<Boolean> put(@Path("id") long id, @Body Jugador object);

    @Multipart
    @POST("upload")
    Call<String> fileUpload(@Part MultipartBody.Part file);
}


