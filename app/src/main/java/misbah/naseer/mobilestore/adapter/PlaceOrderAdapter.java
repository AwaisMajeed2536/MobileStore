package misbah.naseer.mobilestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.dialogs.QuantityPickerDialog;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.interfaces.QuantityPickerCallback;
import misbah.naseer.mobilestore.model.PlaceOrderModel;

/**
 * Created by Awais Majeed on 08-Mar-18.
 */

public class PlaceOrderAdapter extends BaseAdapter {

    private Context context;
    private List<PlaceOrderModel> dataList = new ArrayList<>();

    public PlaceOrderAdapter(Context context, List<PlaceOrderModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public PlaceOrderModel getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PlaceOrderModel obj = dataList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_place_order, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.item_name_tv)).setText(obj.getItemName());
        ((TextView) convertView.findViewById(R.id.quantity_tv)).setText(String.valueOf(obj.getQuantiity()));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuantityPickerDialog dialog = new QuantityPickerDialog(context, callback, position);
                dialog.show();
            }
        });
        return convertView;
    }

    QuantityPickerCallback callback = new QuantityPickerCallback() {
        @Override
        public void onQuantitySelected(int position, int quantity) {
            dataList.get(position).setQuantiity(quantity);
        }
    };
}
