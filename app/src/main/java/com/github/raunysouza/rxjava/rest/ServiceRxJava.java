package com.github.raunysouza.rxjava.rest;

import com.github.raunysouza.rxjava.rest.model.Planet;
import com.github.raunysouza.rxjava.rest.model.Root;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author raunysouza
 */
public interface ServiceRxJava {

    @GET("people")
    Observable<Root> getCharacters();

    @GET
    Observable<Planet> getPlanet(@Url String url);
}
