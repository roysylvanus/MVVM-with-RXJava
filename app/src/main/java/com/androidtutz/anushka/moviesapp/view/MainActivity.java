package com.androidtutz.anushka.moviesapp.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.androidtutz.anushka.moviesapp.R;
import com.androidtutz.anushka.moviesapp.ViewModel.MainActivityViewModel;
import com.androidtutz.anushka.moviesapp.adapter.MovieAdapter;
import com.androidtutz.anushka.moviesapp.model.Movie;
import com.androidtutz.anushka.moviesapp.model.MovieDBResponse;
import com.androidtutz.anushka.moviesapp.service.MoviesDataService;
import com.androidtutz.anushka.moviesapp.service.RetrofitInstance;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Movie> movies=new ArrayList<>();
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwipeRefreshLayout swipeContainer;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle(" TMDb Popular Movies Today");

        mainActivityViewModel=ViewModelProviders.of(this).get(MainActivityViewModel.class);
        getPopularMoviesRx();


        swipeContainer = findViewById(R.id.swipe_layout);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPopularMoviesRx();
            }
        });


    }


    public void getPopularMoviesRx() {

      mainActivityViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
          @Override
          public void onChanged(@Nullable List<Movie> moviesList) {

           movies=(ArrayList<Movie>) moviesList;
           init();


          }
      });



    }

    public void init() {


        recyclerView = findViewById(R.id.rvMovies);
        movieAdapter = new MovieAdapter(this, movies);


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mainActivityViewModel.clear();

    }
}


