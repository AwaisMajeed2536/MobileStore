package misbah.naseer.mobilestore.helper;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Asad-Pc on 11/2/2015.
 * Code edited By Atif Rehman on 07/01/2016
 */
public class MSEditText extends AppCompatEditText {

    public MSEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        //this.setTextSize(context.getAllResources().getDimension(R.dimen.activity_main_roundbutton_textsize));

    }

    public MSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
       // this.setTextSize(context.getAllResources().getDimension(R.dimen.activity_main_roundbutton_textsize));
        ViewGroup.LayoutParams params = this.getLayoutParams();

    }

    private void init(Context context) {
        this.setTypeface(UtilHelper.getFont());
        //this.setTextSize(context.getAllResources().getDimension(R.dimen.activity_main_roundbutton_textsize));

    }

    public MSEditText(Context context) {
        super(context);
        init(context);
       // this.setTextSize(context.getAllResources().getDimension(R.dimen.activity_main_roundbutton_textsize));

    }
}
