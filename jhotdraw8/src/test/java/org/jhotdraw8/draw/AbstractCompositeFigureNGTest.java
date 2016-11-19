/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw;

import org.jhotdraw8.draw.RenderContext;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

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
        
        parent.add(child);
        
        assertTrue(parent.getChildren().contains(child));
        assertEquals(child.getParent(),parent);
    }
    @Test
    public void testRemoveChildUpdatesParentAndChildrenProperties() {
        Figure parent = new AbstractCompositeFigureImpl();
        Figure child = new AbstractCompositeFigureImpl();
        
        parent.add(child);
        parent.remove(child);
        
        assertFalse(parent.getChildren().contains(child));
        assertNull(child.getParent());
    }
    @Test
    public void testMoveChildToAnotherParentUpdatesParentAndChildrenProperties() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure parent2 = new AbstractCompositeFigureImpl();
        Figure child = new AbstractCompositeFigureImpl();
        
        parent1.add(child);
        parent2.add(child);
        
        assertFalse(parent1.getChildren().contains(child));
        assertTrue(parent2.getChildren().contains(child));
        assertEquals(child.getParent(),parent2);
    }
    @Test
    public void testMoveChildToSameParentUpdatesParentAndChildrenProperties() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();
        
        parent1.add(child1);
        parent1.add(child2);
        parent1.add(child1);
        
        assertEquals(parent1.getChildren().size(),2   );
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals(child1.getParent(),parent1);
        assertEquals(parent1.getChildren().get(0),child2);
        assertEquals(parent1.getChildren().get(1),child1);
        assertEquals(child1.getParent(),parent1);
    }
    @Test
    public void testMoveChildToSampeParentUpdatesParentAndChildrenProperties2() {
        Figure parent1 = new AbstractCompositeFigureImpl();
        Figure child1 = new AbstractCompositeFigureImpl();
        Figure child2 = new AbstractCompositeFigureImpl();
        
        parent1.add(child2);
        parent1.add(child1);
        parent1.getChildren().add(0,child1);
        
        assertEquals(parent1.getChildren().size(),2   );
        assertTrue(parent1.getChildren().contains(child1));
        assertEquals(child1.getParent(),parent1);
        assertEquals(parent1.getChildren().get(0),child1);
        assertEquals(parent1.getChildren().get(1),child2);
        assertEquals(child1.getParent(),parent1);
    }


    /** Mock class. */
    public class AbstractCompositeFigureImpl extends AbstractCompositeFigure implements NonTransformableFigure {

        @Override
        public Bounds getBoundsInLocal() {
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
        public void updateNode(RenderContext renderer, Node node) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isLayoutable() {
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
        public ObservableList<Figure> getChildren() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Figure getParent() {
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
