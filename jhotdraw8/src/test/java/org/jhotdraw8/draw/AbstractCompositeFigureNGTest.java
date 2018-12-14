/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.render.RenderContext;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author werni
 */
public class AbstractCompositeFigureNGTest {
    
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
        
        parent.addChild(child);
        
        assertTrue(parent.getChildren().contains(child));
        assertEquals((Object)child.getParent(),(Object)parent);
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
        assertEquals((Object)child.getParent(),(Object)parent2);
    }
    @Test
    public void testMoveChildToSameParentUpdatesParentAndChildrenProperties() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();
        
        parent1.addChild(child1);
        parent1.addChild(child2);
        parent1.addChild(child1);
        
        assertEquals(parent1.getChildren().size(),2   );
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals((Object)child1.getParent(),parent1);
        assertEquals((Object)parent1.getChildren().get(0),child2);
        assertEquals((Object)parent1.getChildren().get(1),child1);
        assertEquals((Object)child1.getParent(),parent1);
    }
    @Test
    public void testMoveChildToSampeParentUpdatesParentAndChildrenProperties2() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();
        
        parent1.addChild(child2);
        parent1.addChild(child1);
        parent1.getChildren().add(0,child1);
        
        assertEquals(parent1.getChildren().size(),2   );
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals((Object)child1.getParent(),parent1);
        assertEquals((Object)parent1.getChildren().get(0),child1);
        assertEquals((Object)parent1.getChildren().get(1),child2);
        assertEquals((Object)child1.getParent(),parent1);
    }


    /** Mock class. */
    public class AbstractCompositeFigureImpl extends AbstractCompositeFigure implements NonTransformableFigure {

        private static final long serialVersionUID = 1L;

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
        public Node createNode(RenderContext renderer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void reshapeInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInLocal(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void transformInParent(Transform transform) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void updateNode(@Nonnull RenderContext renderer, @Nonnull Node node) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableList<String> getStyleClass() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Styleable getStyleableParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isDeletable() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isEditable() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isSelectable() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean invalidateTransforms() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
