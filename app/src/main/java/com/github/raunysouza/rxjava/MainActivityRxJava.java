package com.github.raunysouza.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.github.raunysouza.rxjava.rest.ServiceRxJava;
import com.github.raunysouza.rxjava.rest.model.Character;
import com.github.raunysouza.rxjava.rest.model.Planet;
import com.github.raunysouza.rxjava.rest.model.Root;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author raunysouza
 */
public class MainActivityRxJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load layout and setup any view

        loadCharacters();
    }

    public void loadCharacters() {
        Retrofit retrofit = getRetrofitConfig();
        ServiceRxJava service = retrofit.create(ServiceRxJava.class);

        service.getCharacters()
                .map(Root::getResults)
                .flatMap(Observable::fromIterable)
                .flatMap(character -> service.getPlanet(character.getHomeworld()), Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(this::updateList, this::showError);

    }

    private void updateList(List<Pair<Character, Planet>> pairs) {
        // Update list and refresh ui
        Log.i("MainActivity", "Everything is loaded");
    }

    private void showError(Throwable t) {
        Log.e("MainActivity", t.getMessage(), t);
        Toast.makeText(MainActivityRxJava.this, "An error occurred while getting information", Toast.LENGTH_SHORT).show();
    }

    private Retrofit getRetrofitConfig() {
        return new Retrofit.Builder()
                .baseUrl("http://swapi.co/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
