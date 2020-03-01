package app.task.network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import app.task.model.RepositoryModel;
import app.task.global.Constant;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class retrofitClient {

    private ApiRequest apiRequest;
    private static retrofitClient instance;
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

//============================================================================================

    public retrofitClient() {

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.Base_Url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        apiRequest = retrofit.create(ApiRequest.class);

    } // Retrofit Client Method

//============================================================================================

    public static retrofitClient getInstance() {
        if (instance == null) {
            instance = new retrofitClient();
        }
        return instance;
    } // get Instance

//============================================================================================

    public Call<List<RepositoryModel>> fetchRepositories(String page_index) {
        return apiRequest.fetchRepositories(
                Constant.Limit,
                page_index
        );
    } // fetchSliders

//============================================================================================

}
