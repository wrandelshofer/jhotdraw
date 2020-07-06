package org.jhotdraw8.app;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullBooleanKey;

public interface FileBasedApplication extends Application {
    @NonNull
    NonNullBooleanKey ALLOW_MULTIPLE_ACTIVITIES_WITH_SAME_URI = new NonNullBooleanKey("allowMultipleActivitiesWithSameURI", false);
}
