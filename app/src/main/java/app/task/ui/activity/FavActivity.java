package app.task.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.task.R;
import app.task.adapter.FavAdapter;
import app.task.adapter.ReposAdapter;
import app.task.global.Constant;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavActivity extends AppCompatActivity {


    // Bind Views
    @BindView(R.id.fav_recycler_view)
    RecyclerView fav_recycler_view;

    @BindView(R.id.loading_progress)
    ProgressBar loading_progress;

    @BindView(R.id.tv_msg)
    TextView tv_msg;


    // vars
    private FavAdapter adapter;
    private LinearLayoutManager layoutManager;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        ButterKnife.bind(this);
        getSavedList();
        initializationRecyclerView();
    } // onCreate

//============================================================================================

    private void initializationRecyclerView() {
        layoutManager = new GridLayoutManager(this, 2);
        adapter = new FavAdapter(this, list);
        fav_recycler_view.setHasFixedSize(true);
        fav_recycler_view.setLayoutManager(layoutManager);
        fav_recycler_view.setAdapter(adapter);
    } // initializationRecyclerView

//============================================================================================

    private void getSavedList() {
        SharedPreferences sharedPreferences =
                Objects.requireNonNull(getSharedPreferences(Constant.MY_PREFS_NAME
                        , Context.MODE_PRIVATE));
         list =
                new Gson().fromJson(sharedPreferences.
                                getString("SAVED_ARRAY", null),
                        new TypeToken<List<String>>() {
                        }.getType());

        Log.i(Constant.TAG, "getSAVED_ARRAY -->  " + list);

    } // getSavedList

//============================================================================================

}
