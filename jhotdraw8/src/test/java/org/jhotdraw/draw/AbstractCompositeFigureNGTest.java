/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
        assertEquals(child.getParent(),equals(parent2));
    }


    /** Mock class. */
    public class AbstractCompositeFigureImpl extends AbstractCompositeFigure {

        @Override
        public Bounds getBoundsInLocal() {
            throw new UnsupportedOperationException("Not supported yet."); 
        }

        @Override
        public void reshape(Transform transform) {
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
        
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }    
}
