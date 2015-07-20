package com.sssprog.instagramtest.ui.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.database.Comment;
import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.mvp.PresenterFactory;
import com.sssprog.instagramtest.ui.BaseMvpActivity;
import com.sssprog.instagramtest.utils.ViewStateSwitcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostActivity extends BaseMvpActivity<PostPresenter> {

    private static final String PARAM_POST_ID = "PARAM_POST_ID";

    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.progressBar)
    View progressBar;
    @InjectView(R.id.listView)
    ListView listView;

    private ViewStateSwitcher stateSwitcher;
    private CommentsAdapter adapter;

    public static Intent createIntent(Context context, long postId) {
        return new Intent(context, PostActivity.class)
                .putExtra(PARAM_POST_ID, postId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        adapter = new CommentsAdapter(this);
        listView.setAdapter(adapter);

        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, R.id.contentContainer);
        ViewStateSwitcher.addTextState(stateSwitcher, ViewStateSwitcher.STATE_EMPTY, R.string.general_error_message);
        stateSwitcher.switchToLoading(false);
        getPresenter().loadData(getIntent().getExtras().getLong(PARAM_POST_ID));
    }

    @Override
    protected PresenterFactory<PostPresenter> getPresenterFactory() {
        return DaggerPostActivityComponent.builder()
                .appComponent(Config.appComponent())
                .build();
    }

    void onDataLoaded(PostWithComments data) {
        stateSwitcher.switchToMain(true);
        Picasso.with(this).load(data.post.getImage()).into(image, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });
        adapter.setItems(data.comments);
    }

    void onError() {
        stateSwitcher.switchToEmpty(true);
    }

    private static class CommentsAdapter extends ArrayAdapter<Comment> {

        public CommentsAdapter(Context context) {
            super(context, 0, new ArrayList<Comment>());
            setNotifyOnChange(false);
        }

        public void setItems(List<Comment> items) {
            clear();
            addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Comment item = getItem(position);
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(item.getText());
            holder.userName.setText(item.getUserName());
            return convertView;
        }
    }

    static class ViewHolder {
        @InjectView(R.id.text)
        TextView text;
        @InjectView(R.id.userName)
        TextView userName;

        public ViewHolder(final View root) {
            ButterKnife.inject(this, root);
        }
    }

}
