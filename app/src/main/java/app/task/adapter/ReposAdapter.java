package app.task.adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.task.model.RepositoryModel;
import app.task.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.MyViewHolder> {

    // vars

    private ArrayList<RepositoryModel> list;
    private Context context;

    public ReposAdapter(Context context, ArrayList<RepositoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.tv_name.setText(list.get(position).getName());
        holder.tv_lang.setText(list.get(position).getLanguage());
        holder.tv_watchers.setText(list.get(position).getWatchers() +
                " " + context.getString(R.string.Views
        ));

        holder.tv_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getUrl() != null)
                openWebPage(list.get(position).getUrl());
            }
        });

    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            context.startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context,
                    "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_name)
        TextView tv_name;

        @BindView(R.id.tv_lang)
        TextView tv_lang;

        @BindView(R.id.tv_watchers)
        TextView tv_watchers;

        @BindView(R.id.tv_link)
        TextView tv_link;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
