package com.example.smartpantrymanager.network;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("api/pantry")
    Call<List<PantryItem>> getPantry();

    @POST("api/pantry")
    Call<PantryItem> addPantryItem(@Body PantryItem item);

    @PUT("api/pantry/{id}")
    Call<PantryItem> updatePantryItem(@Path("id") int id, @Body PantryItem item);

    @DELETE("api/pantry/{id}")
    Call<Void> deletePantryItem(@Path("id") int id);

    @GET("api/recipes")
    Call<List<Recipe>> getRecipes();
}
