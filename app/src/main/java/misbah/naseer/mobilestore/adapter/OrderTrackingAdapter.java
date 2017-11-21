package misbah.naseer.mobilestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;

/**
 * Created by Devprovider on 31/07/2017.
 */

public class OrderTrackingAdapter extends BaseAdapter {
    private Context context;
    private List<HashMap<String, String>> dataList = new ArrayList<>();

    public OrderTrackingAdapter(Context context, List<HashMap<String, String>> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HashMap<String, String> model = dataList.get(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.order_tracking_list_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.order_tv)).setText(model.get(Constants.ORDER_TEXT_KEY));
        ImageView receivedIcon = (ImageView) convertView.findViewById(R.id.received_icon);
        if(model.get(Constants.ORDER_STATUS_KEY).equalsIgnoreCase("pending")){
            receivedIcon.setVisibility(View.GONE);
        }else{
            receivedIcon.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
