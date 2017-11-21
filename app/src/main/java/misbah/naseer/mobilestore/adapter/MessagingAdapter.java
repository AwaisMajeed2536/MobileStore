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
import misbah.naseer.mobilestore.interfaces.ClickListenCallback;

/**
 * Created by Devprovider on 26/07/2017.
 */

public class MessagingAdapter extends BaseAdapter {
    private Context context;
    private List<HashMap<String, String>> dataList = new ArrayList<>();
    private ClickListenCallback clickListenCallback;

    public MessagingAdapter(Context context, List<HashMap<String, String>> dataList, ClickListenCallback clickListenCallback) {
        this.context = context;
        this.dataList = dataList;
        this.clickListenCallback = clickListenCallback;
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
        HashMap<String, String> obj = dataList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.messaging_single_item, parent, false);
        }
        if (obj != null && obj.size() == 4) {
            ((TextView) convertView.findViewById(R.id.sender_id_tv)).setText(obj.get(Constants.MESSAGE_FROM));
            ((TextView) convertView.findViewById(R.id.message_tv)).setText(obj.get(Constants.MESSAGE_BODY));
            ((ImageView) convertView.findViewById(R.id.forward_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListenCallback.clicked(position);
                }
            });
        }
        return convertView;
    }
}
