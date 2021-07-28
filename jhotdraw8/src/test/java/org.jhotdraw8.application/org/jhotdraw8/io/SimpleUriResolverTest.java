/*
 * @(#)SimpleUriResolverTest.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.io;


import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleUriResolverTest {
    @Test
    public void testAbsolutizeJarUriToFileBaseUri() throws URISyntaxException {
        URI uri = new URI("jar:file:///pathToJar/Some.jar!/pathToAssets/user-agent.css");
        URI base = new URI("file:///pathToUserHome/");
        final URI absolutized = new SimpleUriResolver().absolutize(base, uri);
        assertEquals(uri, absolutized);
    }

    @Test
    public void testAbsolutizeRelativeUriToJarBaseUri() throws URISyntaxException {
        URI base = new URI("jar:file:///pathToJar/Some.jar!/pathToAssets");
        URI uri = new URI("subDir/image.svg");
        URI expected = new URI("jar:file:///pathToJar/Some.jar!/pathToAssets/subDir/image.svg");
        final SimpleUriResolver instance = new SimpleUriResolver();
        final URI absolutized = instance.absolutize(base, uri);
        assertEquals(expected, absolutized);
    }

    @Test
    public void testAbsolutizeJarUriToParentFolder() throws URISyntaxException {
        URI uri = new URI("jar:file:///pathToJar/Some.jar!/pathToAssets/user-agent.css");
        final URI absolutized = new SimpleUriResolver().getParent(uri);
        assertEquals(new URI("jar:file:///pathToJar/Some.jar!/pathToAssets"), absolutized);
    }
}
