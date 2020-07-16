package com.artsncode.anytaghelper;

import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

/**
 * Forked by pedromarthon on 25/07/2019
 * Created by danylo.volokh on 12/22/2015.
 *
 * Forked to allow multiple tag types in the same text view and to add newer logic
 * This class is a combination of {@link android.text.style.ForegroundColorSpan}
 * and {@link ClickableSpan}.
 *
 * You can set a color of this span plus set a click listener
 */
public class ClickableForegroundColorSpan extends ClickableSpan {

    private OnTagClickListener onTagClickListener;

    public interface OnTagClickListener {
        void onHashTagClicked(String hashTag);
        void onAtTagClicked(String atTag);
        void onLinkTagClicked(String linkTag);
    }

    private final int mColor;

    public ClickableForegroundColorSpan(@ColorInt int color, OnTagClickListener listener) {
        mColor = color;
        onTagClickListener = listener;

        if (onTagClickListener == null) {
            throw new RuntimeException("constructor, click listener not specified. Are you sure you need to use this class?");
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mColor);
    }

    @Override
    public void onClick(View widget) {
        CharSequence text = ((TextView) widget).getText();

        Spanned s = (Spanned) text;
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);

        if(text.subSequence(start, end).toString().startsWith("#")){
            onTagClickListener.onHashTagClicked(text.subSequence(start + 1/*skip "#" sign*/, end).toString());
        }else if(text.subSequence(start, end).toString().startsWith("@")){
            onTagClickListener.onAtTagClicked(text.subSequence(start + 1/*skip "@" sign*/, end).toString());
        } else if(text.subSequence(start, end).toString().matches("(?i)((http|ftp|https):\\/\\/)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?")) {
            onTagClickListener.onLinkTagClicked(text.subSequence(start, end).toString());
        }
    }
}
