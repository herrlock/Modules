package de.herrlock.jersey.objects;

/**
 * @author HerrLock
 */
public final class ValueObject {
    private int value;

    public ValueObject() {
        // do nothing
    }

    public int getValue() {
        return this.value;
    }

    public void setValue( final int value ) {
        this.value = value;
    }

}
