package com.github.raunysouza.rxjava.rest;

import com.github.raunysouza.rxjava.rest.model.Planet;
import com.github.raunysouza.rxjava.rest.model.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author raunysouza
 */
public interface Service {

    @GET("people")
    Call<Root> getCharacters();

    @GET
    Call<Planet> getPlanet(@Url String url);
}
