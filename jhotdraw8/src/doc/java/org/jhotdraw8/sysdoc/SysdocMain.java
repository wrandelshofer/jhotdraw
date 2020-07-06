/* @(#)SysdocMain.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.sysdoc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates software documentation in Html given Markdown (Pegdown) source
 * directories and Java source directories as input.
 * <p>
 * Arguments:
 * <ul>
 * <li>{@code -destdir dir}<br>
 * Specifies the output directory. Must be specified exactly once.</li>
 * <li>{@code -docdir dir}<br>
 * Generates a single Html file for all Markdown {@code *.md} files contained in
 * the specified directory. Must be specified once or more times.</li>
 * <li>{@code -srcdir dir}<br>
 * Analyses all Java source files contained in the specified directory. May by
 * specified multiple times.</li>
 * <li>{@code -tstdir dir}<br>
 * Analyses all Java source files contained in the specified directory. May by
 * specified multiple times.</li>
 * </ul>
 *
 *
 * @author Werner Randelshofer
*/
public class SysdocMain {

    private final static String USAGE = " -destdir dir -docdir dir {-docdir dir} {-srcdir dir} {-tstdir dir}";

    private final Path destdir;
    private final List<Path> docdirs;
    private final List<Path> srcdirs;
    private final List<Path> tstdirs;

    public SysdocMain(Path destdir, List<Path> docdirs, List<Path> srcdirs, List<Path> tstdirs) {
        this.destdir = destdir;
        this.docdirs = docdirs;
        this.srcdirs = srcdirs;
        this.tstdirs = tstdirs;
    }

    private void run() throws IOException {
        JavaFragmentCollector srcc = new JavaFragmentCollector(srcdirs);
        JavaFragmentCollector tstc = new JavaFragmentCollector(tstdirs);

        srcc.collect();
        tstc.collect();

    }

    public static void main(String... args) throws IOException {
        List<Path> tstdirs = new ArrayList<Path>();
        List<Path> srcdirs = new ArrayList<Path>();
        List<Path> docdirs = new ArrayList<Path>();
        Path destdir = null;
        boolean printUsage = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-docdir": {
                    if (args.length == i) {
                        printUsage = true;
                    } else {
                        docdirs.add(Paths.get(args[++i]));
                    }
                    break;
                }
                case "-srcdir": {
                    if (args.length == i) {
                        printUsage = true;
                    } else {
                        srcdirs.add(Paths.get(args[++i]));
                    }
                    break;
                }
                case "-tstdir": {
                    if (args.length == i) {
                        printUsage = true;
                    } else {
                        tstdirs.add(Paths.get(args[++i]));
                    }
                    break;
                }
                case "-destdir": {
                    if (args.length == i) {
                        printUsage = true;
                    } else {
                        destdir = Paths.get(args[++i]);
                    }
                    break;
                }
                default: {
                    printUsage = true;
                    break;
                }
            }
        }

        if (printUsage || srcdirs.isEmpty() || destdir == null) {
            System.out.println(SysdocMain.class.getName() + " " + USAGE);
            return;
        }
        new SysdocMain(destdir, docdirs, srcdirs, tstdirs).run();
    }
}
