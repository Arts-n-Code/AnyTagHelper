package com.artsncode.anytaghelper;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a helper class that should be used with {@link android.widget.EditText} or {@link TextView}
 * In order to have hash-tagged words highlighted. It also provides a click listeners for every hashtag
 * <p>
 * Example :
 * #ThisIsHashTagWord
 * #ThisIsFirst#ThisIsSecondHashTag
 * #hashtagendsifitfindsnotletterornotdigitsignlike_thisIsNotHighlithedArea
 */
public final class AnyTagHelper implements ClickableForegroundColorSpan.OnTagClickListener {

    /**
     * If this is not null then  all of the symbols in the List will be considered as valid symbols of hashtag
     * For example :
     * mAdditionalHashTagChars = {'$','_','-'}
     * it means that hashtag: "#this_is_hashtag-with$dollar-sign" will be highlighted.
     * <p>
     * Note: if mAdditionalHashTagChars would be "null" only "#this" would be highlighted
     */
    private final List<Character> mAdditionalTagChars;
    private TextView mTextView;
    private int mHashTagWordColor;
    private int mAtTagWordColor;
    private int mLinkTagWordColor;

    private enum Tags {
        HASH,
        AT,
        LINK
    }

    private OnTagClickListener mOnTagClickListener;

    public static final class Creator {

        private Creator() {
        }

        public static AnyTagHelper create(int hashTagColor, int atTagColor, int linkTagColor) {
            return new AnyTagHelper(hashTagColor, atTagColor, linkTagColor, null);
        }

        public static AnyTagHelper create(int hashTagColor, int atTagColor, int linkTagColor, char... additionalTagChars) {
            return new AnyTagHelper(hashTagColor, atTagColor, linkTagColor, additionalTagChars);
        }

        public static AnyTagHelper create(AnyTagHelperProperties anyTagHelperProperties) {
            return new AnyTagHelper(
                    anyTagHelperProperties.getHashTagColor(),
                    anyTagHelperProperties.getAtTagColor(),
                    anyTagHelperProperties.getLinkTagColor(),
                    anyTagHelperProperties.getAdditionalTagChars()
            );
        }

    }

    public interface OnTagClickListener {
        void onHashTagClicked(String hashTag);

        void onAtTagClicked(String atTag);

        void onLinkTagClicked(String linkTag);
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() > 0) {
                eraseAndColorizeAllText(text);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private AnyTagHelper(int hashTagColor, int atTagColor, int linkTagColor, char... additionalHashTagCharacters) {
        mHashTagWordColor = hashTagColor;
        mAtTagWordColor = atTagColor;
        mAdditionalTagChars = new ArrayList<>();
        mLinkTagWordColor = linkTagColor;

        if (additionalHashTagCharacters != null) {
            for (char additionalChar : additionalHashTagCharacters) {
                mAdditionalTagChars.add(additionalChar);
            }
        }
    }

    public void handle(TextView textView) {
        if (mTextView == null) {
            mTextView = textView;
            mTextView.addTextChangedListener(mTextWatcher);

            // in order to use spannable we have to set buffer type
            mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);

            if (mOnTagClickListener != null) {
                // we need to set this in order to get onPlaceClicked event
                mTextView.setMovementMethod(LinkMovementMethod.getInstance());

                // after onPlaceClicked clicked text become highlighted
                mTextView.setHighlightColor(Color.TRANSPARENT);
            } else {
                // hash tags are not clickable, no need to change these parameters
            }

            setColorsToAllTags(mTextView.getText());
        } else {
            throw new RuntimeException("TextView is not null. You need to create a unique HashTagHelper for every TextView");
        }

    }

    private void eraseAndColorizeAllText(CharSequence text) {

        Spannable spannable = ((Spannable) mTextView.getText());

        CharacterStyle[] spans = spannable.getSpans(0, text.length(), CharacterStyle.class);
        for (CharacterStyle span : spans) {
            spannable.removeSpan(span);
        }

        setColorsToAllTags(text);
    }

    private void setColorsToAllTags(CharSequence text) {

        int startIndexOfNextSign;

        int index = 0;

        Pattern pattern = Pattern.compile("(?i)((?:(?:http|ftp|https):\\/\\/)?(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            char precedingCharacter = matcher.start() != 0 ? text.charAt(matcher.start() - 1) : text.charAt(matcher.start());
            if(precedingCharacter != '@' && precedingCharacter != '#')
                setColorForTagToTheEnd(matcher.start(), matcher.end(), Tags.LINK);
        }
        while (index < text.length() - 1) {
            char sign = text.charAt(index);
            int endIndexOfTag = index + 1;
            Tags currentTag = null;

            if (sign == '#') {
                currentTag = Tags.HASH;
            } else if (sign == '@') {
                currentTag = Tags.AT;
            }

            if(currentTag != null) {
                startIndexOfNextSign = index;
                endIndexOfTag = findNextValidTagChar(text, startIndexOfNextSign);
                setColorForTagToTheEnd(startIndexOfNextSign, endIndexOfTag, currentTag);
            }
            index = endIndexOfTag;
        }
    }

    private int findNextValidTagChar(CharSequence text, int start) {

        int nonLetterDigitCharIndex = -1; // skip first sign '#"
        for (int index = start + 1; index < text.length(); index++) {

            char sign = text.charAt(index);

            boolean isValidSign = Character.isLetterOrDigit(sign) || mAdditionalTagChars.contains(sign);
            if (!isValidSign) {
                nonLetterDigitCharIndex = index;
                break;
            }
        }
        if (nonLetterDigitCharIndex == -1) {
            // we didn't find non-letter. We are at the end of text
            nonLetterDigitCharIndex = text.length();
        }

        return nonLetterDigitCharIndex;
    }

    private void setColorForTagToTheEnd(int startIndex, int endIndex, Tags tag) {
        if (mOnTagClickListener != null) {
            Spannable s = (Spannable) mTextView.getText();
            CharacterStyle span;
            int color = 0;
            if(tag.equals(Tags.HASH)) {
                color = mHashTagWordColor;
            } else if(tag.equals(Tags.AT)) {
                color = mAtTagWordColor;
            } else if(tag.equals(Tags.LINK)) {
                color = mLinkTagWordColor;
            }
            if(color != 0) {
                span = new ClickableForegroundColorSpan(color, this);
                s.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

    }

    public List<String> getAllHashTags(boolean withHashes) {

        String text = mTextView.getText().toString();
        Spannable spannable = (Spannable) mTextView.getText();

        // use set to exclude duplicates
        Set<String> hashTags = new LinkedHashSet<>();

        for (CharacterStyle span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            if (text.substring(spannable.getSpanStart(span), spannable.getSpanEnd(span)).startsWith("#")) {
                hashTags.add(
                        text.substring(!withHashes ? spannable.getSpanStart(span) + 1/*skip "#" sign*/
                                        : spannable.getSpanStart(span),
                                spannable.getSpanEnd(span)));
            }
        }

        return new ArrayList<>(hashTags);
    }

    public List<String> getAllHashTags() {
        return getAllHashTags(false);
    }

    public List<String> getAllAtTags(boolean withAts) {

        String text = mTextView.getText().toString();
        Spannable spannable = (Spannable) mTextView.getText();

        // use set to exclude duplicates
        Set<String> atTags = new LinkedHashSet<>();

        for (CharacterStyle span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            if (text.substring(spannable.getSpanStart(span), spannable.getSpanEnd(span)).startsWith("@")) {
                atTags.add(
                        text.substring(!withAts ? spannable.getSpanStart(span) + 1/*skip "@" sign*/
                                        : spannable.getSpanStart(span),
                                spannable.getSpanEnd(span)));
            }
        }

        return new ArrayList<>(atTags);
    }

    public List<String> getAllAtTags() {
        return getAllAtTags(false);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.mOnTagClickListener = listener;
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        mOnTagClickListener.onHashTagClicked(hashTag);
    }

    @Override
    public void onAtTagClicked(String atTag) {
        mOnTagClickListener.onAtTagClicked(atTag);

    }

    @Override
    public void onLinkTagClicked(String linkTag) {
        mOnTagClickListener.onLinkTagClicked(linkTag);
    }
}
