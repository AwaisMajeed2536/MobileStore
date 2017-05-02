package misbah.naseer.mobilestore.helper;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by Asad-Pc on 11/2/2015.
 */
public class MSButton extends AppCompatButton {
    public MSButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(UtilHelper.getFont());
    }
}
