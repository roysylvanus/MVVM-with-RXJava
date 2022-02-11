package com.androidtutz.anushka.moviesapp.model;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import com.androidtutz.anushka.moviesapp.R;
import com.androidtutz.anushka.moviesapp.service.MoviesDataService;
import com.androidtutz.anushka.moviesapp.service.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {

    private Application application;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    private MutableLiveData<List<Movie>> moviesLiveData=new MutableLiveData<>();
    private ArrayList<Movie> movies;
    private Observable<MovieDBResponse> movieDBResponseObservable;

    public MovieRepository(Application application) {
        this.application = application;


    }

    public MutableLiveData<List<Movie>> getMoviesLiveData() {

        movies = new ArrayList<>();
        MoviesDataService getMoviesDataService = RetrofitInstance.getService();
        movieDBResponseObservable = getMoviesDataService.getPopularMoviesWithRx(application.getApplicationContext().getString(R.string.api_key));

        compositeDisposable.add(movieDBResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<MovieDBResponse, Observable<Movie>>() {
                    @Override
                    public Observable<Movie> apply(MovieDBResponse movieDBResponse) throws Exception {
                        return Observable.fromArray(movieDBResponse.getMovies().toArray(new Movie[0]));
                    }
                })
                .filter(new Predicate<Movie>() {
                    @Override
                    public boolean test(Movie movie) throws Exception {
                        return movie.getVoteAverage()>7.0;
                    }
                })
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        movies.add(movie);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        moviesLiveData.postValue(movies);
                    }
                }));
        return moviesLiveData;
    }

    public void clear(){
        compositeDisposable.clear();
    }
}
