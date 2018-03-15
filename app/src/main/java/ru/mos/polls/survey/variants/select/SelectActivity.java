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
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.SearchViewCustomizer;

public abstract class SelectActivity extends ToolbarAbstractActivity {

    public static final String EMPTY_TEXT = "empty_text";

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.empty)
    TextView emptyTextView;
    private Adapter adapter;
    private IntentExtraProcessor intentExtraProcessor = new IntentExtraProcessor();

    private JsonObjectRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_select);
        setSupportProgressBarIndeterminateVisibility(false);
        ButterKnife.bind(this);
        intentExtraProcessor.initialize(getIntent());

        adapter = new Adapter();


        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                JSONObject jsonObject = adapter.getItem(position);
//                onLoadFromJson(jsonObject);
//                sendResult();
//            }
//        });

        String title = intentExtraProcessor.getTitle();
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        String emptyText = intentExtraProcessor.getEmptyText();
        emptyTextView.setText(TextUtils.isEmpty(emptyText) ? getString(R.string.data_not_found) : emptyText);
    }

    @OnItemClick(R.id.list)
    void onItemListClick(int position) {
        JSONObject jsonObject = adapter.getItem(position);
        onLoadFromJson(jsonObject);
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
        doRequest(s);
    }

    protected abstract void onLoadFromJson(JSONObject jsonObject);

    protected abstract void processRequestJson(String search, JSONObject requestJsonObject) throws JSONException;

    protected abstract String onGetUrl();

    protected abstract void addDataToIntent(Intent data);

    protected abstract View onNewView();

    protected abstract void onConvertView(View convertView, JSONObject jsonObject);

    private void doRequest(String s) {
        if (request != null) {
            request.cancel();
        }
        setSupportProgressBarIndeterminateVisibility(true);
        JSONObject requestJsonObject = new JSONObject();
        try {
            processRequestJson(s, requestJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = onGetUrl();
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONArray jsonArray = jsonObject.optJSONArray("objects");
                setSupportProgressBarIndeterminateVisibility(false);
                if (jsonArray.length() > 0) {
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                adapter.setData(jsonArray);
                adapter.notifyDataSetChanged();
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(this, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                setSupportProgressBarIndeterminateVisibility(false);
                Toast.makeText(SelectActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        request = new JsonObjectRequest(url, requestJsonObject, listener, errorListener);
        addRequest(request);
    }

    private void sendResult() {
        Intent data = intentExtraProcessor.generateResultIntent();
        addDataToIntent(data);
        setResult(RESULT_OK, data);
        finish();
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

    private class Adapter extends BaseAdapter implements Filterable {

        private JSONArray jsonArray = new JSONArray();

        public void setData(JSONArray data) {
            this.jsonArray = data;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public JSONObject getItem(int position) {
            return jsonArray.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            JSONObject jsonObject = getItem(position);
            long id = jsonObject.optInt("id");
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = onNewView();
            }
            JSONObject jsonObject = getItem(position);
            onConvertView(convertView, jsonObject);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

}