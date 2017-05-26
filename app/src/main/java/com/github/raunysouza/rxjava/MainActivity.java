package com.github.raunysouza.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.github.raunysouza.rxjava.rest.Service;
import com.github.raunysouza.rxjava.rest.model.Character;
import com.github.raunysouza.rxjava.rest.model.Planet;
import com.github.raunysouza.rxjava.rest.model.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author raunysouza
 */
public class MainActivity extends AppCompatActivity {

    private List<Pair<Character, Planet>> pairs = Collections.synchronizedList(new ArrayList<Pair<Character, Planet>>());

    Service service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load layout and setup any view

        loadCharacters();
    }

    public void loadCharacters() {
        Retrofit retrofit = getRetrofitConfig();
        service = retrofit.create(Service.class);

        service.getCharacters()
                .enqueue(new Callback<Root>() {
                    @Override
                    public void onResponse(@Nullable Call<Root> call, @Nullable Response<Root> response) {
                        if (response != null && response.body() != null) {
                            List<Character> characters = response.body().getResults();
                            if (characters != null && !characters.isEmpty()) {
                                for (Character character : characters) {
                                    loadPlanet(character);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<Root> call, @Nullable Throwable t) {
                        showError(t);
                    }
                });

    }

    private void loadPlanet(final Character character) {
        service.getPlanet(character.getHomeworld())
                .enqueue(new Callback<Planet>() {
                    @Override
                    public void onResponse(@Nullable Call<Planet> call, @Nullable Response<Planet> response) {
                        if (response != null && response.body() != null) {
                            pairs.add(new Pair<>(character, response.body()));
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<Planet> call, @Nullable Throwable t) {
                        showError(t);
                    }
                });
    }

    private void showError(Throwable t) {
        Log.e("MainActivity", t.getMessage(), t);
        Toast.makeText(MainActivity.this, "An error occurred while getting information", Toast.LENGTH_SHORT).show();
    }

    private Retrofit getRetrofitConfig() {
        return new Retrofit.Builder()
                .baseUrl("http://swapi.co/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
