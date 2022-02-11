package com.androidtutz.anushka.moviesapp.service;

import com.androidtutz.anushka.moviesapp.model.MovieDBResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by K. A. ANUSHKA MADUSANKA on 7/4/2018.
 */
public interface MoviesDataService {


    @GET("movie/popular")
    Observable<MovieDBResponse> getPopularMoviesWithRx(@Query("api_key") String apiKey);


}
