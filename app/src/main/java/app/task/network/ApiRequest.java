package app.task.network;

import java.util.List;

import app.task.model.RepositoryModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRequest {

//============================================================================================

    @GET("JeffreyWay/repos")
    Call<List<RepositoryModel>> fetchRepositories(
            @Query("per_page") String per_page,
            @Query("page") String page);     // fetchRepositories

//============================================================================================

}
