package com.artsncode.anytaghelper;

public class AnyTagHelperProperties {
    private int hashTagColor;
    private int atTagColor;
    private int linkTagColor;
    private char[] additionalTagChars;

    public AnyTagHelperProperties(int hashTagColor, int atTagColor, int linkTagColor, char... additionalTagChars) {
        this.hashTagColor = hashTagColor;
        this.atTagColor = atTagColor;
        this.linkTagColor = linkTagColor;
        this.additionalTagChars = additionalTagChars;
    }

    public int getHashTagColor() {
        return hashTagColor;
    }

    public void setHashTagColor(int hashTagColor) {
        this.hashTagColor = hashTagColor;
    }

    public int getAtTagColor() {
        return atTagColor;
    }

    public void setAtTagColor(int atTagColor) {
        this.atTagColor = atTagColor;
    }

    public int getLinkTagColor() {
        return linkTagColor;
    }

    public void setLinkTagColor(int linkTagColor) {
        this.linkTagColor = linkTagColor;
    }

    public char[] getAdditionalTagChars() {
        return additionalTagChars;
    }

    public void setAdditionalTagChars(char[] additionalTagChars) {
        this.additionalTagChars = additionalTagChars;
    }
}
