package misbah.naseer.mobilestore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import misbah.naseer.mobilestore.R;

/**
 * Created by Awais Majeed on 05-Feb-18.
 */

public class OrderTimesAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> dataList = new ArrayList<>();

    public OrderTimesAdapter(Context context, ArrayList<String> dataList) {
        super(context, -1, dataList);
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_order_times, parent, false);
        }
        try {
            TextView orderTv = (TextView) convertView.findViewById(R.id.order_tv);
            TextView timeTv = (TextView) convertView.findViewById(R.id.time_tv);
            String[] data = dataList.get(position).split("\\+");
            orderTv.setText(data[0]);
            timeTv.setText(data[1].replace(",",":"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
