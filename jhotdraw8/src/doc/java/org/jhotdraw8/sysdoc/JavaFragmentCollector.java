/* @(#)JavaFragmentCollector.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.sysdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFragmentCollector.
 *
 * @author Werner Randelshofer
*/
public class JavaFragmentCollector {

    private final List<Path> srcdirs;

    public JavaFragmentCollector(List<Path> srcdirs) {
        this.srcdirs = srcdirs;
    }

    public void collect() throws IOException {
        ArrayList<String> args = new ArrayList<>();
        args.add("-sourcepath");
        StringBuilder b = new StringBuilder();
        for (Path p : srcdirs) {
            if (b.length() != 0) {
                b.append(File.pathSeparatorChar);
            }
            b.append(p.toString());
        }
        args.add(b.toString());
        b.setLength(0);
        for (Path p : srcdirs) {
            List<Path> subdirs = Files.find(p, 1, (pi, ai) -> ai.isDirectory()).collect(Collectors.toList());
            for (Path s : subdirs) {
                if (b.length() != 0) {
                    b.append(':');
                }
                if (!s.equals(p)) {
                    b.append(s.getFileName());
                }
            }
        }
        if (b.length() == 0) {
            System.out.println("No Java Packages found.");
            return;
        }
        args.add("-subpackages");
        args.add(b.toString());
        System.out.println(args);
        com.sun.tools.javadoc.Main.execute(JavaFragmentCollector.class.getName(), JavaFragmentCollector.MyDoclet.class.getName(), args.toArray(new String[args.size()]));

        /*for (Fragment f : MyDoclet.fragments) {
            System.out.println(f);
        }
        for (Reference r : MyDoclet.references) {
            System.out.println(r);
        }*/
    }

    public static class MyDoclet extends Doclet {

        static ArrayList<Fragment> fragments = new ArrayList<>();
        static ArrayList<Reference> references = new ArrayList<>();

        public static boolean start(RootDoc root) {
            for (ClassDoc cd : root.classes()) {
                visitClassDoc(cd);
            }
            return true;
        }

        private static void visitClassDoc(ClassDoc cd) {
            final Fragment fragment = new Fragment(Paths.get(cd.qualifiedName()), cd.qualifiedName());
            fragments.add(fragment);
            visitDoc(fragment, cd);
            for (ConstructorDoc cod : cd.constructors()) {
                visitConstructorDoc(cod);
            }
            for (FieldDoc fd : cd.fields()) {
                visitFieldDoc(fd);
            }
            for (MethodDoc fd : cd.methods()) {
                visitMethodDoc(fd);
            }
            for (ClassDoc fd : cd.innerClasses()) {
                visitClassDoc(fd);
            }
        }

        private static void visitConstructorDoc(ConstructorDoc cd) {
            final Fragment fragment = new Fragment(Paths.get(cd.qualifiedName()), cd.qualifiedName() + "." + cd.name());
            fragments.add(fragment);
            visitDoc(fragment, cd);
        }

        private static void visitFieldDoc(FieldDoc cd) {
            final Fragment fragment = new Fragment(Paths.get(cd.qualifiedName()), cd.qualifiedName());
            fragments.add(fragment);
            visitDoc(fragment, cd);
        }

        private static void visitMethodDoc(MethodDoc cd) {
            final Fragment fragment = new Fragment(Paths.get(cd.qualifiedName()), cd.qualifiedName());
            fragments.add(fragment);
            visitDoc(fragment, cd);
        }

        private static void visitDoc(Fragment frag, Doc cd) {
            for (Tag tag : cd.tags()) {
                if ("@doc.ref".equals(tag.name())) {
                    for (String ref : tag.text().split("\\s+")) {
                        references.add(new Reference(frag, ref));
                    }
                }
            }
        }
    }
}
