package com.sssprog.instagramtest.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.models.SearchItem;
import com.sssprog.instagramtest.mvp.PresenterClass;
import com.sssprog.instagramtest.ui.BaseMvpActivity;
import com.sssprog.instagramtest.utils.CircleTransform;
import com.sssprog.instagramtest.utils.Prefs;
import com.sssprog.instagramtest.utils.ViewStateSwitcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

@PresenterClass(SearchPresenter.class)
public class SearchActivity extends BaseMvpActivity<SearchPresenter> {

    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.userName)
    EditText userName;

    private ViewStateSwitcher stateSwitcher;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        adapter = new SearchAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchItem item = (SearchItem) listView.getItemAtPosition(position);
                Prefs.putString(R.string.pref_current_user_name, item.getUserName());
                Prefs.putString(R.string.pref_current_user_id, item.getId());
                setResult(RESULT_OK);
                finish();
            }
        });

        initStateSwitcher();
        updateState();
        initBehaviour();
    }

    private void initStateSwitcher() {
        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, listView);
        ViewStateSwitcher.addTextState(stateSwitcher, ViewStateSwitcher.STATE_EMPTY, R.string.no_items);
    }

    private void initBehaviour() {
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadItems();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadItems() {
        stateSwitcher.switchToLoading(true);
        getPresenter().search(userName.getText().toString());
    }

    void onItemsLoaded(List<SearchItem> items) {
        adapter.setItems(items);
        updateState();
    }

    private void updateState() {
        if (adapter.isEmpty()) {
            stateSwitcher.switchToEmpty(true);
        } else {
            stateSwitcher.switchToMain(true);
        }
    }

    private static class SearchAdapter extends ArrayAdapter<SearchItem> {

        public SearchAdapter(Context context) {
            super(context, 0, new ArrayList<SearchItem>());
            setNotifyOnChange(false);
        }

        public void setItems(List<SearchItem> items) {
            clear();
            addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final SearchItem item = getItem(position);
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(item.getUserName());
            Picasso.with(parent.getContext()).load(item.getImage())
                    .resizeDimen(R.dimen.list_avatar_size, R.dimen.list_avatar_size)
                    .transform(new CircleTransform()).into(holder.image);
            return convertView;
        }
    }

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.text)
        TextView text;

        public ViewHolder(final View root) {
            ButterKnife.inject(this, root);
        }
    }
}
