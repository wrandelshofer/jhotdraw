/*
 * @(#)QualifiedName.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

public class QualifiedName implements Comparable<QualifiedName> {
    private final @Nullable String namespace;
    private final @NonNull String name;

    public QualifiedName(String namespace, @NonNull String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Nullable
    public String getNamespace() {
        return namespace;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QualifiedName)) {
            return false;
        }
        QualifiedName that = (QualifiedName) o;
        return Objects.equals(namespace, that.namespace) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }

    @Override
    public int compareTo(@NonNull QualifiedName o) {
        return this.name.compareTo(o.name);
    }

    @NonNull
    @Override
    public String toString() {
        return "QualifiedName{" +
                "namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
