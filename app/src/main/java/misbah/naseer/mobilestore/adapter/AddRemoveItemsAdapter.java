package misbah.naseer.mobilestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.interfaces.AddDeleteItemCallback;
import misbah.naseer.mobilestore.model.AvailableItemsModel;

/**
 * Created by Awais Majeed on 18-Mar-18.
 */

public class AddRemoveItemsAdapter extends BaseAdapter{

    private Context context;
    private List<AvailableItemsModel> dataList = new ArrayList<>();
    private AddDeleteItemCallback callback;

    public AddRemoveItemsAdapter(Context context, AddDeleteItemCallback callback, List<AvailableItemsModel> dataList) {
        this.callback = callback;
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public AvailableItemsModel getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void onDatasetChanged(List<AvailableItemsModel> newDatalist){
        dataList.clear();
        dataList.addAll(newDatalist);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.li_add_remove_items, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.item_name_tv)).setText(dataList.get(i).getItemName());
        view.findViewById(R.id.delete_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemDeleteClicked(dataList.get(i).getItemId());
            }
        });
        return view;
    }
}
