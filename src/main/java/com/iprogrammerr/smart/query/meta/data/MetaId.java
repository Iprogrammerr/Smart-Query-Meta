package com.iprogrammerr.smart.query.meta.data;

public class MetaId {

    public final String name;
    public final boolean autoIncrement;

    public MetaId(String name, boolean autoIncrement) {
        this.name = name;
        this.autoIncrement = autoIncrement;
    }

    @Override
    public boolean equals(Object object) {
        boolean equal;
        if (object == this) {
            equal = true;
        } else if (object != null && object.getClass().equals(getClass())) {
            MetaId other = (MetaId) object;
            equal = name.equals(other.name) && autoIncrement == other.autoIncrement;
        } else {
            equal = false;
        }
        return equal;

    }
}
