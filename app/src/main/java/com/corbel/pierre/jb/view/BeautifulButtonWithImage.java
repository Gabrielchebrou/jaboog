package com.corbel.pierre.jb.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AutoResizeTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeautifulButtonWithImage extends RelativeLayout {

    View rootView;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.text_view)
    AutoResizeTextView textView;

    public BeautifulButtonWithImage(Context context) {
        this(context, null);
    }

    public BeautifulButtonWithImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautifulButtonWithImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        rootView = inflate(context, R.layout.beautiful_button_with_image, this);
        ButterKnife.bind(this);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BeautifulButtonWithImage, defStyleAttr, 0);

        String str = attr.getString(R.styleable.BeautifulButtonWithImage_pco_label);
        int id = attr.getResourceId(R.styleable.BeautifulButtonWithImage_pco_src, -1);
        setText(str);
        setDrawable(id);
        attr.recycle();
    }

    public void setText(String newText) {
        textView.setText(newText);
    }

    public void setDrawable(int newDrawableId) {
        imageView.setImageResource(newDrawableId);
    }

    public void setWikiUnderline() {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.material_color_blue_900));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (hasOnClickListeners()) {
                callOnClick();
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
