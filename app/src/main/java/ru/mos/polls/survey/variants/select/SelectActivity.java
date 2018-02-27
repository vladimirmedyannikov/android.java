package ru.mos.polls.survey.variants.select;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.SearchViewCustomizer;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.survey.variants.select.model.Objects;
import ru.mos.polls.survey.variants.select.service.SelectService;

public abstract class SelectActivity<T extends Objects> extends ToolbarAbstractActivity {

    public static final String EMPTY_TEXT = "empty_text";
    T model;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.empty)
    TextView emptyTextView;
    protected Adapter adapter;
    private IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();

    SelectService.Request request;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_select);
        setSupportProgressBarIndeterminateVisibility(false);
        ButterKnife.bind(this);
        intentExtraProcessor.initialize(getIntent());
        request = new SelectService.Request();
        adapter = new Adapter();


        listView.setAdapter(adapter);
        String title = intentExtraProcessor.getTitle();
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        String emptyText = intentExtraProcessor.getEmptyText();
        emptyTextView.setText(TextUtils.isEmpty(emptyText) ? getString(R.string.data_not_found) : emptyText);
    }

    @OnItemClick(R.id.list)
    void onItemListClick(int position) {
        T jsonObject = adapter.getItem(position);
        getDataFromObject(jsonObject);
        sendResult();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(Menu.NONE, 4, 0, "Title");
        item.setIcon(android.R.drawable.ic_menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setActionView(item, getSearchView());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String s;
        if (searchView == null) {
            s = "";
        } else {
            s = searchView.getQuery().toString();
        }
        processRequest(s);
        doRequest(s);
    }

    protected abstract void getDataFromObject(T object);

    protected abstract void processRequest(String search);

    protected abstract void addDataToIntent(Intent data);

    protected abstract View onNewView();

    protected abstract void onConvertView(View convertView, T object);

    protected abstract void doRequest(String s);

    protected void setEmptyTextVisibility(int size) {
        emptyTextView.setVisibility(size > 0 ? View.GONE : View.VISIBLE);
    }

    protected void addItemList(List<T> list) {
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private void sendResult() {
        Intent data = intentExtraProcessor.generateResultIntent();
        addDataToIntent(data);
        setResult(RESULT_OK, data);
        finish();
    }

    protected HandlerApiResponseSubscriber<SelectService.Result<T>> getHandler() {
        HandlerApiResponseSubscriber<SelectService.Result<T>> handler = new HandlerApiResponseSubscriber<SelectService.Result<T>>(this, null) {
            @Override
            protected void onResult(SelectService.Result<T> result) {
                setEmptyTextVisibility(result.getObjects().size());
                addItemList(result.getObjects());
            }
        };
        return handler;
    }

    private SearchView searchView;

    private SearchView getSearchView() {
        searchView = new SearchView(this);
        searchView.setVisibility(View.VISIBLE);
        customizeSearchView(searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processRequest(newText);
                doRequest(newText);
                return false;
            }
        });
        return searchView;
    }

    private void customizeSearchView(View searchView) {
        SearchViewCustomizer customizer = new SearchViewCustomizer(searchView);
        customizer.setCloseIcon(R.drawable.x)
                .setFinderIcon(R.drawable.lupa)
                .setColorTextSearch(android.R.color.white)
                .applay();

    }

    public class Adapter extends BaseAdapter implements Filterable {

        private List<T> list = new ArrayList<>();

        public void setList(List<T> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public T getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            T object = getItem(position);
            long id = object.getId();
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = onNewView();
            }
            T item = getItem(position);
            onConvertView(convertView, item);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }
}