package org.izv.teams.model.rest;

import com.google.gson.JsonElement;

import org.izv.teams.model.data.Equipo;

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

public interface TeamClient {
    @DELETE("equipo/{id}")
    Call<Long> delete(@Path("id") long id);

    @GET("equipo/{id}")
    Call<Equipo> get(@Path("id") long id);

    @GET("equipo")
    Call<List<Equipo>> getAll();

    @POST("equipo")
    Call<Long> post(@Body Equipo object);

    @PUT("equipo/{id}")
    Call<Boolean> put(@Path("id") long id, @Body Equipo object);

    @Multipart
    @POST("upload")
    Call<String> fileUpload(@Part MultipartBody.Part file);
}
