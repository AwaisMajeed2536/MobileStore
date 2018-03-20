package misbah.naseer.mobilestore.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.adapter.AddRemoveItemsAdapter;
import misbah.naseer.mobilestore.adapter.PlaceOrderAdapter;
import misbah.naseer.mobilestore.dialogs.AddItemDialog;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.interfaces.AddDeleteItemCallback;
import misbah.naseer.mobilestore.interfaces.AlertDialogCallback;
import misbah.naseer.mobilestore.model.AvailableItemsModel;

public class AddRemoveItemsActivity extends AppCompatActivity implements View.OnClickListener, AddDeleteItemCallback {

    private ListView itemsLv;
    private FloatingActionButton addBtn;
    private DatabaseReference itemsRef;
    private List<AvailableItemsModel> dataList = new ArrayList<>();
    private AddRemoveItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_remove_items);
        initView();
        UtilHelper.showWaitDialog(this, "Loading View!", "please wait...");

        itemsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/itemsAvailable/");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataList.add(child.getValue(AvailableItemsModel.class));
                }
                adapter = new AddRemoveItemsAdapter(AddRemoveItemsActivity.this, AddRemoveItemsActivity.this, dataList);
                itemsLv.setAdapter(adapter);
                UtilHelper.dismissWaitDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {
        itemsLv = (ListView) findViewById(R.id.items_lv);
        addBtn = (FloatingActionButton) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_btn) {
            new AddItemDialog(this, this).show();
        }
    }

    @Override
    public void onItemDeleteClicked(final String itemId) {
        new AlertDialog.Builder(this).setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        itemsRef.child(itemId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                List<AvailableItemsModel> newDatalist = new ArrayList<>();
                                for (AvailableItemsModel obj : dataList) {
                                    if (obj.getItemId().equals(itemId))
                                        continue;
                                    newDatalist.add(obj);
                                }
                                adapter.onDatasetChanged(newDatalist);
                                Toast.makeText(AddRemoveItemsActivity.this, "Item Deleted", Toast.LENGTH_SHORT).

                                        show();
                            }
                        });
                    }
                }).

                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).

                create().

                show();

    }

    @Override
    public void onItemAdded(String itemName) {
        final AvailableItemsModel model = new AvailableItemsModel(itemName, UtilHelper.getRandomId());
        itemsRef.child(UtilHelper.getRandomId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                List<AvailableItemsModel> newDatalist = new ArrayList<>();
                newDatalist.addAll(dataList);
                newDatalist.add(model);
                adapter.onDatasetChanged(newDatalist);
                Toast.makeText(AddRemoveItemsActivity.this, "Item Added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
