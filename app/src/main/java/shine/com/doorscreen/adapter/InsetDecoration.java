package shine.com.doorscreen.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;


/**
 * 设置item的边距为8dp
 */
public class InsetDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "InsetDecoration";
    private int mInsets;

    public InsetDecoration(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mInsets = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f, displayMetrics);
    }
    public InsetDecoration(Context context, int margin) {
        mInsets = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //We can supply forced insets for each item view here in the Rect
        outRect.set(mInsets, mInsets, mInsets, mInsets);
    }
}
