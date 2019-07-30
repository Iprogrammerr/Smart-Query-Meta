package com.iprogrammerr.smart.query.meta.data;

import java.util.Objects;

public class Table {

    public final String name;
    public final MetaId metaId;

    public Table(String name, MetaId metaId) {
        this.name = name;
        this.metaId = metaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(name, table.name) &&
            Objects.equals(metaId, table.metaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metaId);
    }

    @Override
    public String toString() {
        return "Table{" +
            "name='" + name + '\'' +
            ", metaId=" + metaId +
            '}';
    }
}
