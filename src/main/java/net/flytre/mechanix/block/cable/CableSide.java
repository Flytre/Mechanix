package net.flytre.mechanix.block.cable;

import net.minecraft.util.StringIdentifiable;

public enum CableSide implements StringIdentifiable {
    NONE("none"),
    CONNECTED("connected"),
    WRENCHED("wrenched");

    private final String name;

    CableSide(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}