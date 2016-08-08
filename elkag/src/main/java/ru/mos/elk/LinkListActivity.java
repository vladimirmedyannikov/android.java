package ru.mos.elk;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LinkListActivity extends BaseActivity {
			
    public static final String NAMES = "ru.mos.profile.NAMES";
    public static final String INTENTS = "ru.mos.profile.INTENTS";
    public static final String ICONS = "ru.mos.profile.ICONS";
    public static final String TITLE = "ru.mos.profile.TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        final Bundle args = getIntent().getExtras();
        setTitle(args.getInt(TITLE));
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(getAdapter(args));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent(args, position);

                if(intent!=null)
                    startActivity(intent);
            }
        });
    }

    public Intent getIntent(Bundle args, int position) {
        SparseArray<Intent> intents = args.getSparseParcelableArray(INTENTS);
        return intents.get(position);
    }

    public int getContentView() {
        return R.layout.activity_dynamic;
    }

    public ListAdapter getAdapter(Bundle extras) {
        final int[] icons = extras.getIntArray(ICONS);
        return new ArrayAdapter<String>(this, R.layout.elk_item_profile_link, getNames(extras)){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if(icons!=null && position<icons.length)
                    ((TextView)v.findViewById(R.id.tvTitle)).setCompoundDrawablesWithIntrinsicBounds(icons[position],0,0,0);
                return v;
            }
        };

    }

    public String[] getNames(Bundle extras){
        return extras.getStringArray(NAMES);
    }

}
