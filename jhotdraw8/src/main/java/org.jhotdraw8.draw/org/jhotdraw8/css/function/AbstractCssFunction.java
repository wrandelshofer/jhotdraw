package org.jhotdraw8.css.function;

public abstract class AbstractCssFunction<T> implements CssFunction<T> {
    private final String name;

    public AbstractCssFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
