/* @(#)AbstractCompositeFigureTest.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.render.RenderContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author werni
 */
public class AbstractCompositeFigureTest {

    @Test
    public void testInvariantsAfterInstantiation() {
        Figure f = new AbstractCompositeFigureImpl();

        assertTrue(f.getChildren().isEmpty());
        assertNull(f.getParent());
    }

    @Test
    public void testAddChildUpdatesParentAndChildrenProperties() {
        Figure parent = new AbstractCompositeFigureImpl();
        Figure child = new AbstractCompositeFigureImpl();

        boolean added = parent.addChild(child);
        assertTrue(added);

        assertTrue(parent.getChildren().contains(child));
        assertEquals(child.getParent(), parent);
    }

    @Test
    public void testRemoveChildUpdatesParentAndChildrenProperties() {
        Figure parent = new AbstractCompositeFigureImpl();
        Figure child = new AbstractCompositeFigureImpl();

        parent.addChild(child);
        parent.removeChild(child);

        assertFalse(parent.getChildren().contains(child));
        assertNull(child.getParent());
    }

    @Test
    public void testMoveChildToAnotherParentUpdatesParentAndChildrenProperties() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure parent2 = new AbstractCompositeFigureImpl();
        Figure child = new AbstractCompositeFigureImpl();

        parent1.addChild(child);
        parent2.addChild(child);

        assertFalse(parent1.getChildren().contains(child));
        assertTrue(parent2.getChildren().contains(child));
        assertEquals(child.getParent(), parent2);
    }

    @Test
    public void testMoveChildToSameParentUpdatesParentAndChildrenProperties() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();

        parent1.addChild(child1);
        parent1.addChild(child2);
        parent1.addChild(child1);

        assertEquals(2, parent1.getChildren().size());
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals(parent1, child1.getParent());
        assertEquals(child2, parent1.getChildren().get(0));
        assertEquals(child1, parent1.getChildren().get(1));
        assertEquals(parent1, child1.getParent());
    }

    @Test
    public void testMoveChildToSampeParentUpdatesParentAndChildrenProperties2() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();

        parent1.addChild(child2);
        parent1.addChild(child1);
        parent1.getChildren().add(0, child1);

        assertEquals(parent1.getChildren().size(), 2);
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals(child1.getParent(), parent1);
        assertEquals(parent1.getChildren().get(0), child1);
        assertEquals(parent1.getChildren().get(1), child2);
        assertEquals(child1.getParent(), parent1);
    }


    /**
     * Mock class.
     */
    public class AbstractCompositeFigureImpl extends AbstractCompositeFigure implements NonTransformableFigure {

        private static final long serialVersionUID = 1L;

        @NonNull
        @Override
        public Bounds getBoundsInLocal() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transform getWorldToLocal() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Transform getWorldToParent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void reshapeInLocal(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public Node createNode(RenderContext renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void reshapeInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void transformInLocal(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void transformInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void updateNode(@NonNull RenderContext renderer, @NonNull Node node) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public ObservableList<String> getStyleClass() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public Styleable getStyleableParent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @NonNull
        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDeletable() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isEditable() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSelectable() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSuitableChild(@NonNull Figure newChild) {
            return true;
        }

        @Override
        public boolean isSuitableParent(@NonNull Figure newChild) {
            return true;
        }

        @Override
        public boolean invalidateTransforms() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
