package com.github.raunysouza.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.raunysouza.rxjava.rest.ServiceRxJava;
import com.github.raunysouza.rxjava.rest.model.Character;
import com.github.raunysouza.rxjava.rest.model.Planet;
import com.github.raunysouza.rxjava.rest.model.Root;

import java.util.Collections;
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

    private CharacterAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView listRecyclerView = (RecyclerView) findViewById(R.id.list);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setAdapter(mAdapter = new CharacterAdapter());

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
        mAdapter.setItems(pairs);
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

    private class CharacterAdapter extends RecyclerView.Adapter<CharacterViewHolder> {

        private List<Pair<Character, Planet>> mItems = Collections.emptyList();

        @Override
        public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CharacterViewHolder(LayoutInflater.from(MainActivityRxJava.this).inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CharacterViewHolder holder, int position) {
            Pair<Character, Planet> characterPlanetPair = mItems.get(position);
            holder.mNameTextView.setText(characterPlanetPair.first.getName());
            holder.mPlanetTextView.setText(characterPlanetPair.second.getName());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItems(List<Pair<Character, Planet>> items) {
            mItems = items;
            notifyDataSetChanged();
        }
    }

    private class CharacterViewHolder extends RecyclerView.ViewHolder {

        TextView mNameTextView;
        TextView mPlanetTextView;

        public CharacterViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.name);
            mPlanetTextView = (TextView) itemView.findViewById(R.id.planet);
        }
    }
}
