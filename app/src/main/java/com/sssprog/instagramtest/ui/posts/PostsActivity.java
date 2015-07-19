package com.sssprog.instagramtest.ui.posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.database.Post;
import com.sssprog.instagramtest.api.services.PostServiceImpl;
import com.sssprog.instagramtest.mvp.PresenterClass;
import com.sssprog.instagramtest.ui.BaseMvpActivity;
import com.sssprog.instagramtest.ui.post.PostActivity;
import com.sssprog.instagramtest.ui.search.SearchActivity;
import com.sssprog.instagramtest.utils.Prefs;
import com.sssprog.instagramtest.utils.ViewStateSwitcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

@PresenterClass(PostsPresenter.class)
public class PostsActivity extends BaseMvpActivity<PostsPresenter> {

    private static final int REQUEST_CHANGE_NAME = 0;

    @InjectView(R.id.listView)
    ListView listView;

    private ViewStateSwitcher stateSwitcher;
    private PostsAdapter adapter;
    private View footerProgressBar;
    private boolean allLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        updateUserNameView();
        initListView();
        initStateSwitcher();
        loadItems(true);
    }

    private void initStateSwitcher() {
        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, listView);
        ViewStateSwitcher.addTextState(stateSwitcher, ViewStateSwitcher.STATE_EMPTY, R.string.no_items);
    }

    private void initListView() {
        View footer = getLayoutInflater().inflate(R.layout.loading_footer, null);
        footerProgressBar = footer.findViewById(R.id.progressBar);
        listView.addFooterView(footer, null, false);
        adapter = new PostsAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Post item = (Post) listView.getItemAtPosition(position);
            startActivity(PostActivity.createIntent(PostsActivity.this, item.getId()));
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem >= totalItemCount && !allLoaded) {
                    loadItems(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadItems(true);
                return true;
            case R.id.action_change_user:
                startActivityForResult(new Intent(this, SearchActivity.class), REQUEST_CHANGE_NAME);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadItems(boolean fromStart) {
        if (fromStart) {
            stateSwitcher.switchToLoading(false);
        }
        footerProgressBar.setVisibility(View.VISIBLE);
        getPresenter().loadItems(fromStart);
    }

    void onItemsLoaded(List<Post> items, boolean firstPage, boolean allLoaded) {
        this.allLoaded = allLoaded;
        if (firstPage) {
            adapter.clear();
        }
        if (allLoaded) {
            footerProgressBar.setVisibility(View.GONE);
        }
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        if (firstPage) {
            listView.setSelection(0);
        }
        if (adapter.isEmpty()) {
            stateSwitcher.switchToEmpty(true);
        } else {
            stateSwitcher.switchToMain(true);
        }
    }

    private void updateUserNameView() {
        setTitle(Prefs.getUserName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHANGE_NAME) {
            updateUserNameView();
            Config.appComponent().postService().clearCache();
            loadItems(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class PostsAdapter extends ArrayAdapter<Post> {

        public PostsAdapter(Context context) {
            super(context, 0, new ArrayList<Post>());
            setNotifyOnChange(false);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Post item = getItem(position);
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(item.getDescription());
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.with(parent.getContext()).load(item.getLowResolutionImage()).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.text)
        TextView text;
        @InjectView(R.id.progressBar)
        View progressBar;

        public ViewHolder(final View root) {
            ButterKnife.inject(this, root);
        }
    }
}
