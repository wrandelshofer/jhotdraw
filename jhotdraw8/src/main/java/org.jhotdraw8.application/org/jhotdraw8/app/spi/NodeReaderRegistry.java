/*
 * @(#)NodeReaderRegistry.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class NodeReaderRegistry {
    private NodeReaderRegistry() {
    }

    @NonNull
    public static List<NodeReader> getNodeReaders(@NonNull URL url) {
        List<NodeReader> list = new ArrayList<>();
        for (Iterator<NodeReaderProvider> i = ServiceLoader.load(NodeReaderProvider.class).iterator(); i.hasNext(); ) {
            NodeReaderProvider spi = i.next();
            if (spi.canDecodeInput(url)) {
                list.add(spi.createReader());
            }
        }
        return list;
    }

    @NonNull
    public static List<NodeReader> getNodeReaders(@NonNull String path) {
        List<NodeReader> list = new ArrayList<>();
        for (Iterator<NodeReaderProvider> i = ServiceLoader.load(NodeReaderProvider.class).iterator(); i.hasNext(); ) {
            NodeReaderProvider spi = i.next();
            if (spi.canDecodeInput(path)) {
                list.add(spi.createReader());
            }
        }
        return list;
    }

    @Nullable
    public static NodeReader getNodeReader(@NonNull URL url) {
        List<NodeReader> list = getNodeReaders(url);
        return list.isEmpty() ? null : list.get(0);
    }

    @Nullable
    public static NodeReader getNodeReader(@NonNull String path) throws MalformedURLException {
        List<NodeReader> list = getNodeReaders(path);
        return list.isEmpty() ? null : list.get(0);
    }
}
