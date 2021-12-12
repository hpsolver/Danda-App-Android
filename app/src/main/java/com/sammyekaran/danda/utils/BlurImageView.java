package com.sammyekaran.danda.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.sammyekaran.danda.R;

import java.util.HashMap;

public class BlurImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static HashMap<String, DimenPair> mDimenCache = new HashMap<String, DimenPair>();
    private float defaultBitmapScale = 0.1f;
    private static final int   MAX_RADIUS   = 25;
    private static final int   MIN_RADIUS   = 1;
    Drawable imageOnView;
    boolean mAdjustViewBounds;
    private String groupId = null;
    private boolean adjustWidth = false;

    int cachedWidth = 0;
    int cachedHeight = 0;



    public BlurImageView(Context context) {
        super(context);
        init(null);
    }

    public BlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (imageOnView == null)
            imageOnView = drawable;
    }

    private void init(AttributeSet attrs) {

        if (attrs != null && getDrawable() != null) {

            imageOnView = getDrawable();

            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BlurImageView, 0, 0);

            Integer radius = typedArray.getInteger(R.styleable.BlurImageView_radius, 0);

            setBlur(radius);

            typedArray.recycle();
        }
    }


    public void setBitmapScale(float bitmapScale) {
        this.defaultBitmapScale = bitmapScale;
    }

    public void setBlur(int radius) {

        if (imageOnView == null)
            imageOnView = getDrawable();

        // max radius = 25
        if (radius > MIN_RADIUS && radius <= MAX_RADIUS) {

            Bitmap blurred = blurRenderScript(((BitmapDrawable) imageOnView).getBitmap(), radius);
            setImageBitmap(blurred);
            invalidate();

        } else if (radius == 0) {
            setImageDrawable(imageOnView);
            invalidate();

        } else
            Log.e("BLUR", "actualRadius invalid: " + radius);
    }

    public Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        int width  = Math.round(smallBitmap.getWidth() * defaultBitmapScale);
        int height = Math.round(smallBitmap.getHeight() * defaultBitmapScale);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(smallBitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(getContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        mAdjustViewBounds = adjustViewBounds;
        super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*Drawable mDrawable = getDrawable();
        if (mDrawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (mAdjustViewBounds) {
            int mDrawableWidth = mDrawable.getIntrinsicWidth();
            int mDrawableHeight = mDrawable.getIntrinsicHeight();
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
                // Fixed Height & Adjustable Width
                int height = heightSize;
                int width = height * mDrawableWidth / mDrawableHeight;
                if (isInScrollingContainer())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                // Fixed Width & Adjustable Height
                int width = widthSize;
                int height = width * mDrawableHeight / mDrawableWidth;
                if (isInScrollingContainer())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (cachedWidth == 0 && cachedHeight == 0 && groupId != null) {
            if(mDimenCache.containsKey(groupId)) {
                Log.d("RatioImageView", "Using Group Cache. Group Id="+groupId);
                DimenPair pair = mDimenCache.get(groupId);
                cachedWidth = pair.width;
                cachedHeight = pair.height;
                setMeasuredDimension(cachedWidth, cachedHeight);
                return;
            }
        }

        if (cachedWidth > 0 && cachedHeight > 0) {
            Log.d("RatioImageView", "Using Cache. Group Id="+groupId);
            setMeasuredDimension(cachedWidth, cachedHeight);
            return;
        }

        Drawable d = getDrawable();
        if (d != null) {
            Log.d("RatioImageView", "Setting size. Group Id="+groupId);
            float ratio = (float) getMeasuredWidth() / (float) d.getIntrinsicWidth();
            int imgHeight = (int) (d.getIntrinsicHeight() * ratio);
            int imgWidth = (int) (d.getIntrinsicWidth() * ratio);
            if(adjustWidth) {
                cachedWidth = imgWidth;
                cachedHeight = getMeasuredHeight();
            } else {
                cachedWidth = getMeasuredWidth();
                cachedHeight = imgHeight;
            }
            setMeasuredDimension(cachedWidth, cachedHeight);

            if(groupId != null) {
                addToCache();
            }
        } else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }

    private boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    private void addToCache() {
        mDimenCache.put(groupId, new DimenPair(cachedWidth, cachedHeight));
    }

    /***
     * Change the groupId for this image view. This will trigger recalculation of dimensions.
     *
     * @param groupId String any string or null.
     */
    public void setGroupId(String groupId) {
        if(groupId != null) {
            mDimenCache.remove(groupId);
        }
        this.groupId = groupId;
        cachedWidth = 0;
        cachedHeight = 0;
    }

    /***
     * Default: false. If set to true, we will adjust the width to maintain aspect ration instead of the height.
     *
     * @param adjustWidth
     */
    public void setAdjustWidth(boolean adjustWidth) {
        this.adjustWidth = adjustWidth;
        cachedWidth = 0;
        cachedHeight = 0;
        if(groupId != null) {
            mDimenCache.remove(groupId);
        }
    }

    private class DimenPair {
        public DimenPair(int w, int h) {
            this.width = w;
            this.height = h;
        }
        int width;
        int height;
    }

}
