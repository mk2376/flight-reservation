package com.example.flightreservation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter implements Filterable, View.OnFocusChangeListener {      // https://stackoverflow.com/questions/23422072/searchview-in-listview-having-a-custom-adapter

    public Context context;
    public ArrayList<String> searchArrayList = new ArrayList<>();
    public ArrayList<String> orig;
    public boolean isOnFocus = false;
    public SearchView searchView;
    public SearchView searchView2;
    public String searchedText = "";

    public ListViewAdapter(Context context, ArrayList<String> orig, SearchView searchView, SearchView searchView2) {
        super();
        this.context = context;
        this.orig = orig;
        this.searchView = searchView;
        this.searchView2 = searchView2;

        searchView.setOnQueryTextFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d("me", "SearchView click "+hasFocus);
        if (hasFocus) {
            Log.d("me", "SearchView click "+searchArrayList.size());
            isOnFocus = true;
            searchView.setQuery(searchedText, false);
            getFilter().filter(searchedText);
        } else {
            searchedText = searchView.getQuery().toString();
            isOnFocus = false;
            searchArrayList.clear();
            notifyDataSetChanged();
        }
    }


    public class ViewHolder {
        TextView name;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<String> results = new ArrayList<String>();

                if (constraint != null && isOnFocus) {
                    if (orig != null && orig.size() > 0) {
                        for (final String g : orig) {
                            if (g.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                if (!g.equals(constraint.toString())) {
                                    if (!searchView2.getQuery().toString().equals(g))
                                        results.add(g);
                                }
                            }
                        }
                    }
                    oReturn.values = results;
                }
                Log.d("me", "filtering"+String.valueOf(results));
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchArrayList = (ArrayList<String>) results.values;
                if (searchArrayList == null)
                    searchArrayList = new ArrayList<>();
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_view_items, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.nameItems);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(searchArrayList.get(position));
        return convertView;
    }
}