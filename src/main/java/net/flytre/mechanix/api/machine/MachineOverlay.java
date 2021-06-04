package net.flytre.mechanix.api.machine;

public interface MachineOverlay {

    default boolean renderOverlayOnSides() {
        return true;
    }
}
