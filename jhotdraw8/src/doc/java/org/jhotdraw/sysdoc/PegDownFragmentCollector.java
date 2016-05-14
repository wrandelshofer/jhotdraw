/* @(#)PegDownFragmentCollector.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.sysdoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AnchorLinkNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;

/**
 * PegDownFragmentCollector.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PegDownFragmentCollector {

    private final List<Path> docdirs;
    private NavigableMap<Path, RootNode> parsedFiles;
    private List<Fragment> exportedFragments;
    private List<Reference> referencedFragments;

    public PegDownFragmentCollector(List<Path> docdirs) {
        this.docdirs = docdirs;
    }

    public void collect() throws IOException {
        parsedFiles = new TreeMap<>();
        exportedFragments = new ArrayList<>();
        referencedFragments = new ArrayList<>();
        for (Path dir : docdirs) {
System.out.println("PegDown collecting dir:"+dir);            
            for (Path f : Files.find(dir, 100, (pi, ai) -> pi.getFileName().toString().endsWith(".md") && ai.isRegularFile()).collect(Collectors.toList())) {
System.out.println("PegDown collecting file:"+f);            
                collect(f);
            }
        }
    }

    private void collect(Path docfile) throws IOException {
        PegDownProcessor pdp = new PegDownProcessor(Extensions.ALL);
        ByteArrayOutputStream inbuf = new ByteArrayOutputStream();
        Files.copy(docfile, inbuf);
        String markdown = new String(inbuf.toByteArray(), StandardCharsets.UTF_8);
        RootNode ast = pdp.parseMarkdown(markdown.toCharArray());
        FragmentDetector fd = new FragmentDetector(docfile);
        ast.accept(fd);
        parsedFiles.put(docfile, ast);
        exportedFragments.addAll(fd.getExportedFragments());
        referencedFragments.addAll(fd.getReferencedFragments());
    }

    public List<RootNode> getRootNodesFor(Path docdir) {
        List<RootNode> roots = new ArrayList<>();
        for (Map.Entry<Path, RootNode> entry : parsedFiles.entrySet()) {
            if (entry.getKey().getParent().equals(docdir)) {
                roots.add(entry.getValue());
            }
        }
        return roots;
    }

    private static class FragmentDetector implements Visitor {

        private List<Fragment> exportedFragments = new ArrayList<>();
        private List<Reference> referencedFragments = new ArrayList<>();
        private final Path file;
        private Fragment fragment;

        public FragmentDetector(Path file) {
            this.file = file;
        }

        public List<Fragment> getExportedFragments() {
            return exportedFragments;
        }

        public List<Reference> getReferencedFragments() {
            return referencedFragments;
        }

        @Override
        public void visit(AbbreviationNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(AnchorLinkNode n) {
            exportedFragments.add(fragment = new Fragment(file, n.getName()));
        }

        @Override
        public void visit(AutoLinkNode n) {
            referencedFragments.add(new Reference(fragment, n.getText()));
        }

        @Override
        public void visit(BlockQuoteNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(BulletListNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(CodeNode node) {
        }

        @Override
        public void visit(DefinitionListNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(DefinitionNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(DefinitionTermNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(ExpImageNode n) {
            referencedFragments.add(new Reference(fragment, n.url));
            visitChildren(n);
        }

        @Override
        public void visit(ExpLinkNode n) {
            referencedFragments.add(new Reference(fragment, n.url));
            visitChildren(n);
        }

        @Override
        public void visit(HeaderNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(HtmlBlockNode n) {
        }

        @Override
        public void visit(InlineHtmlNode n) {
        }

        @Override
        public void visit(ListItemNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(MailLinkNode n) {
            referencedFragments.add(new Reference(fragment, n.getText()));
        }

        @Override
        public void visit(OrderedListNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(ParaNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(QuotedNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(ReferenceNode n) {
            referencedFragments.add(new Reference(fragment, n.getUrl()));
            visitChildren(n);
        }

        @Override
        public void visit(RefImageNode n) {
            referencedFragments.add(new Reference(fragment, n.referenceKey.toString()));
            visitChildren(n);
        }

        @Override
        public void visit(RefLinkNode n) {
            if (n.referenceKey != null) {
                referencedFragments.add(new Reference(fragment, n.referenceKey.toString()));
            } else {
                referencedFragments.add(new Reference(fragment, n.toString()));
            }
            visitChildren(n);
        }

        @Override
        public void visit(RootNode node) {
            visitChildren(node);
        }

        @Override
        public void visit(SimpleNode n) {
        }

        @Override
        public void visit(SpecialTextNode n) {
        }

        @Override
        public void visit(StrikeNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(StrongEmphSuperNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableBodyNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableCaptionNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableCellNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableColumnNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableHeaderNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(TableRowNode n) {
            visitChildren(n);
        }

        @Override
        public void visit(VerbatimNode n) {
        }

        @Override
        public void visit(WikiLinkNode n) {
            referencedFragments.add(new Reference(fragment, n.getText()));
        }

        @Override
        public void visit(TextNode n) {
        }

        @Override
        public void visit(SuperNode sn) {
            visitChildren(sn);
        }

        @Override
        public void visit(Node n) {
        }
        // helpers

        protected void visitChildren(SuperNode node) {
            for (Node child : node.getChildren()) {
                child.accept(this);
            }
        }
    }

}
