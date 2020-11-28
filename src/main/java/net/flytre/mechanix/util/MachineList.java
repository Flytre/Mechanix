package net.flytre.mechanix.util;

import java.util.ArrayList;
import java.util.Collection;

public class MachineList<E> extends ArrayList<E> {

    public MachineList(int initialCapacity) {
        super(initialCapacity);
    }

    public MachineList() {
        super();
    }

    public MachineList(Collection<? extends E> c) {
        super(c);
    }

    public E getStandard() {
        return get(0);
    }

    public E getGilded() {
        return get(1);
    }

    public E getVysterium() {
        return get(2);
    }

    public E getNeptunium() {
        return get(3);
    }

}
