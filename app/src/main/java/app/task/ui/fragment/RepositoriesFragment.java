package app.task.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.task.model.RepositoryModel;
import app.task.R;
import app.task.adapter.ReposAdapter;
import app.task.global.Constant;
import app.task.network.retrofitClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RepositoriesFragment extends Fragment {

    // bind views

    @BindView(R.id.repos_recycler_view)
    RecyclerView repos_recycler_view;

    @BindView(R.id.loading_progress)
    ProgressBar loading_progress;

    @BindView(R.id.tv_msg)
    TextView tv_msg;

    // vars
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int page_index = 1;
    boolean endOfREsults = false;
    private boolean loading_flag = true;
    private ArrayList<RepositoryModel> arrayList = new ArrayList<>();
    private ReposAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repositories, container, false);
        ButterKnife.bind(this, view);
        fetchRepositories();
        return view;
    } // onCreateView

    public void fetchRepositories() {

        loading_progress.setVisibility(View.VISIBLE);

        retrofitClient.getInstance()
                .fetchRepositories(page_index + "")
                .enqueue(
                        new Callback<List<RepositoryModel>>() {
                            @Override
                            public void onResponse(Call<List<RepositoryModel>> call, Response<List<RepositoryModel>> response) {

                                try {
                                    arrayList.clear();
                                    arrayList.addAll(response.body());
                                } catch (Exception e) {
                                }

                                if (arrayList.size() == 0) {
                                    tv_msg.setText(getString(R.string.empty));
                                    tv_msg.setVisibility(View.VISIBLE);
                                    repos_recycler_view.setVisibility(View.GONE);
                                }

                                if (arrayList.isEmpty()) {
                                    page_index = page_index - 1;
                                    endOfREsults = true;
                                }
                                initializationRecyclerView();
                                setPaging();
                                loading_flag = true;
                                loading_progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<List<RepositoryModel>> call, Throwable t) {
                                tv_msg.setText(getString(R.string.Fail));
                                tv_msg.setVisibility(View.VISIBLE);
                                Log.i(Constant.TAG, "error onFailure --> " + t.getLocalizedMessage());
                                loading_progress.setVisibility(View.GONE);
                            }
                        });
    } // fetchRepositories

    private void initializationRecyclerView() {
        layoutManager = new GridLayoutManager(getContext(), 2);
        adapter = new ReposAdapter(getActivity(), arrayList);
        repos_recycler_view.setHasFixedSize(true);
        repos_recycler_view.setLayoutManager(layoutManager);
        repos_recycler_view.setAdapter(adapter);
    } // initializationRecyclerView

    private void setPaging() {
        Log.i(Constant.TAG, "setPaging called : ");

        repos_recycler_view.post(new Runnable() {
            @Override
            public void run() {
                repos_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (!endOfREsults) {
                            visibleItemCount = layoutManager.getChildCount();
                            totalItemCount = layoutManager.getItemCount();
                            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                            if (loading_flag) {
                                if ((visibleItemCount + pastVisiblesItems) + 2 >= totalItemCount) {
                                    loading_flag = false;
                                    if (!(arrayList.size() == 0)) {
                                        loading_progress.setVisibility(View.GONE);
                                    }
                                    page_index = page_index + 1;
                                    fetchMoreRepos();
                                }
                            }
                        }
                    }
                });
            }
        });

    } // set pagination

    private void fetchMoreRepos() {
        loading_progress.setVisibility(View.VISIBLE);

        retrofitClient.getInstance().fetchRepositories(
                page_index + "").enqueue(new Callback<List<RepositoryModel>>() {
            @Override
            public void onResponse(Call<List<RepositoryModel>> call, Response<List<RepositoryModel>> response) {

                loading_progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().isEmpty()) {
                        page_index = page_index - 1;
                        endOfREsults = true;
                    }
                    arrayList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    loading_flag = true;
                }
                loading_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RepositoryModel>> call, Throwable t) {
                Log.i(Constant.TAG, "error in ViewModel = " + t.getLocalizedMessage());
                loading_progress.setVisibility(View.GONE);

            }
        });
    } // fetchMoreRepos

}
