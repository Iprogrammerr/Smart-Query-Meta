package com.iprogrammerr.smart.query.meta.data;

import java.util.Objects;

public class MetaId {

    public final String name;
    public final boolean autoIncrement;
    public final boolean composite;

    public MetaId(String name, boolean autoIncrement, boolean composite) {
        this.name = name;
        this.autoIncrement = autoIncrement;
        this.composite = composite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaId metaId = (MetaId) o;
        return autoIncrement == metaId.autoIncrement &&
            composite == metaId.composite &&
            Objects.equals(name, metaId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, autoIncrement, composite);
    }
}
