package misbah.naseer.mobilestore.helper;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class MSTextView extends AppCompatTextView {

    public MSTextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public MSTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(context);

    }

    public MSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        this.setTypeface(UtilHelper.getFont());
    }

}