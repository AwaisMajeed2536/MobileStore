package misbah.naseer.mobilestore.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.interfaces.AddDeleteItemCallback;

/**
 * Created by Awais Majeed on 18-Mar-18.
 */

public class AddItemDialog extends Dialog implements View.OnClickListener {

    protected EditText itemNameEt;
    protected Button doneBtn;
    private AddDeleteItemCallback callback;

    public AddItemDialog(@NonNull Context context, AddDeleteItemCallback callback) {
        super(context);
        this.setContentView(R.layout.dialog_add_item);
        this.setCancelable(false);
        initView();
        this.callback = callback;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done_btn) {
            if (!TextUtils.isEmpty(itemNameEt.getText())) {
                callback.onItemAdded(itemNameEt.getText().toString());
                this.dismiss();
            } else {
                itemNameEt.setError("Enter a name");
                itemNameEt.requestFocus();
            }
        }
    }

    private void initView() {
        itemNameEt = (EditText) findViewById(R.id.item_name_et);
        doneBtn = (Button) findViewById(R.id.done_btn);
        doneBtn.setOnClickListener(AddItemDialog.this);
    }
}
