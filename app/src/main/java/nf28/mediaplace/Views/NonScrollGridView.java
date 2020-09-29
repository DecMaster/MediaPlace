package nf28.mediaplace.Views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

public class NonScrollGridView extends GridView {

    // PROPRIETES
    private static final String TAG = "AutoGridView";
    private int numColumnsID;
    private int previousFirstVisible;
    private int numColumns = 3;

    // CONSTRUCTEURS
    public NonScrollGridView(Context context) {
        super(context);
    }
    public NonScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    public NonScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    // METHODES
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }

    private void init(AttributeSet attrs) {
        // Read numColumns out of the AttributeSet
        int count = attrs.getAttributeCount();
        if(count > 0) {
            for(int i = 0; i < count; i++) {
                String name = attrs.getAttributeName(i);
                if(name != null && name.equals("numColumns")) {
                    // Update columns
                    this.numColumnsID = attrs.getAttributeResourceValue(i, 1);
                    updateColumns();
                    break;
                }
            }
        }
    }

    private void updateColumns() {
        this.numColumns = 3;
    }

    @Override
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        super.setNumColumns(numColumns);

        Log.d(TAG, "setSelection --> " + previousFirstVisible);
        setSelection(previousFirstVisible);
    }

    @Override
    protected void onLayout(boolean changed, int leftPos, int topPos, int rightPos, int bottomPos) {
        super.onLayout(changed, leftPos, topPos, rightPos, bottomPos);
        setHeights();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        updateColumns();
        setNumColumns(this.numColumns);
    }

    @Override
    protected void onScrollChanged(int newHorizontal, int newVertical, int oldHorizontal, int oldVertical) {
        // Check if the first visible position has changed due to this scroll
        int firstVisible = getFirstVisiblePosition();
        if(previousFirstVisible != firstVisible) {
            // Update position, and update heights
            previousFirstVisible = firstVisible;
            setHeights();
        }

        super.onScrollChanged(newHorizontal, newVertical, oldHorizontal, oldVertical);
    }

    public void setHeights() {
        ListAdapter adapter = getAdapter();

        if(adapter != null) {
            for(int i = 0; i < getChildCount(); i+=numColumns) {
                // Determine the maximum height for this row
                int maxHeight = 0;
                for(int j = i; j < i+numColumns; j++) {
                    View view = getChildAt(j);
                    if(view != null && view.getHeight() > maxHeight) {
                        maxHeight = view.getHeight();
                    }
                }

                // Set max height for each element in this row
                if(maxHeight > 0) {
                    for(int j = i; j < i+numColumns; j++) {
                        View view = getChildAt(j);
                        if(view != null && view.getHeight() != maxHeight) {
                            view.setMinimumHeight(maxHeight);
                        }
                    }
                }
            }
        }
    }


}