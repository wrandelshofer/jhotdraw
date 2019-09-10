package org.jhotdraw8.io;

import javafx.scene.input.DataFormat;

public class DataFormats {
    private DataFormats() {
    }

    public static DataFormat registerDataFormat(String mimeType) {
        DataFormat fmt = DataFormat.lookupMimeType(mimeType);
        if (fmt == null) {
            fmt = new DataFormat(mimeType);
        }
        return fmt;
    }

}
