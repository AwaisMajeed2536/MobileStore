package misbah.naseer.mobilestore.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.interfaces.QuantityPickerCallback;

/**
 * Created by Awais Majeed on 12-Mar-18.
 */

public class QuantityPickerDialog extends Dialog implements View.OnClickListener {

    protected EditText quantityEt;
    protected Button doneBtn;
    private QuantityPickerCallback callback;
    private Context context;
    private int position;

    public QuantityPickerDialog(@NonNull Context context, QuantityPickerCallback callback, int position) {
        super(context);
        this.setContentView(R.layout.dialog_quantity_input);
        this.setCancelable(false);
        initView();
        this.context = context;
        this.callback = callback;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done_btn) {
            if (!TextUtils.isEmpty(quantityEt.getText())) {
                callback.onQuantitySelected(position, Integer.parseInt(quantityEt.getText().toString()));
                this.dismiss();
            } else {
                quantityEt.setError("Enter a value");
                quantityEt.requestFocus();
            }
        }
    }

    private void initView() {
        quantityEt = (EditText) findViewById(R.id.quantity_et);
        doneBtn = (Button) findViewById(R.id.done_btn);
        doneBtn.setOnClickListener(QuantityPickerDialog.this);
    }
}
