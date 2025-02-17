package com.github.mortonl.zebra.elements.shared;

public enum ZebraOrientation {
    NORMAL("N"),
    ROTATED("R"),
    INVERTED("I"),
    BOTTOM_UP("B");

    private final String value;

    ZebraOrientation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
