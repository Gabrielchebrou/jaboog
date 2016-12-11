package com.corbel.pierre.jb.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AutoResizeTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeautifulButton extends RelativeLayout {

    View rootView;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.text_view)
    AutoResizeTextView textView;

    public BeautifulButton(Context context) {
        this(context, null);
    }

    public BeautifulButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautifulButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        rootView = inflate(context, R.layout.beautiful_button, this);
        ButterKnife.bind(this);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BeautifulButtonWithImage, defStyleAttr, 0);

        String str = attr.getString(R.styleable.BeautifulButtonWithImage_pco_label);
        setText(str);
        attr.recycle();
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String newText) {
        textView.setText(newText);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (hasOnClickListeners() && isClickable()) {
                callOnClick();
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
