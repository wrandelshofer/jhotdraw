package CH.ifa.draw.test.standard;

import java.awt.Point;
import java.util.List;

import junit.framework.TestCase;

// JUnitDoclet begin import
import CH.ifa.draw.figures.PolyLineFigure;
import CH.ifa.draw.figures.RectangleFigure;
import CH.ifa.draw.util.CollectionsFactory;
// JUnitDoclet end import

/*
* Generated by JUnitDoclet, a tool provided by
* ObjectFab GmbH under LGPL.
* Please see www.junitdoclet.org, www.gnu.org
* and www.objectfab.de for informations about
* the tool, the licence and the authors.
*/


// JUnitDoclet begin javadoc_class
/**
* TestCase FigureEnumeratorTest is generated by
* JUnitDoclet to hold the tests for FigureEnumerator.
* @see CH.ifa.draw.standard.FigureEnumerator
*/
// JUnitDoclet end javadoc_class
public class FigureEnumeratorTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // instance variables, helper methods, ... put them in this marker
  CH.ifa.draw.standard.FigureEnumerator figureenumerator = null;
  // JUnitDoclet end class
  
  /**
  * Constructor FigureEnumeratorTest is
  * basically calling the inherited constructor to
  * initiate the TestCase for use by the Framework.
  */
  public FigureEnumeratorTest(String name) {
    // JUnitDoclet begin method FigureEnumeratorTest
    super(name);
    // JUnitDoclet end method FigureEnumeratorTest
  }
  
  /**
  * Factory method for instances of the class to be tested.
  */
  public CH.ifa.draw.standard.FigureEnumerator createInstance() throws Exception {
    // JUnitDoclet begin method testcase.createInstance
	List l = CollectionsFactory.current().createList();
	l.add(new RectangleFigure(new Point(10,10), new Point(100,100)));
	l.add(new PolyLineFigure(20, 20));
    return new CH.ifa.draw.standard.FigureEnumerator(l);
    // JUnitDoclet end method testcase.createInstance
  }
  
  /**
  * Method setUp is overwriting the framework method to
  * prepare an instance of this TestCase for a single test.
  * It's called from the JUnit framework only.
  */
  protected void setUp() throws Exception {
    // JUnitDoclet begin method testcase.setUp
    super.setUp();
    figureenumerator = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Method tearDown is overwriting the framework method to
  * clean up after each single test of this TestCase.
  * It's called from the JUnit framework only.
  */
  protected void tearDown() throws Exception {
    // JUnitDoclet begin method testcase.tearDown
    figureenumerator = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  // JUnitDoclet begin javadoc_method hasNextFigure()
  /**
  * Method testHasNextFigure is testing hasNextFigure
  * @see CH.ifa.draw.standard.FigureEnumerator#hasNextFigure()
  */
  // JUnitDoclet end javadoc_method hasNextFigure()
  public void testHasNextFigure() throws Exception {
    // JUnitDoclet begin method hasNextFigure
    // JUnitDoclet end method hasNextFigure
  }
  
  // JUnitDoclet begin javadoc_method nextFigure()
  /**
  * Method testNextFigure is testing nextFigure
  * @see CH.ifa.draw.standard.FigureEnumerator#nextFigure()
  */
  // JUnitDoclet end javadoc_method nextFigure()
  public void testNextFigure() throws Exception {
    // JUnitDoclet begin method nextFigure
    // JUnitDoclet end method nextFigure
  }
  
  // JUnitDoclet begin javadoc_method getEmptyEnumeration()
  /**
  * Method testGetEmptyEnumeration is testing getEmptyEnumeration
  * @see CH.ifa.draw.standard.FigureEnumerator#getEmptyEnumeration()
  */
  // JUnitDoclet end javadoc_method getEmptyEnumeration()
  public void testGetEmptyEnumeration() throws Exception {
    // JUnitDoclet begin method getEmptyEnumeration
    // JUnitDoclet end method getEmptyEnumeration
  }
  
  // JUnitDoclet begin javadoc_method reset()
  /**
  * Method testReset is testing reset
  * @see CH.ifa.draw.standard.FigureEnumerator#reset()
  */
  // JUnitDoclet end javadoc_method reset()
  public void testReset() throws Exception {
    // JUnitDoclet begin method reset
    // JUnitDoclet end method reset
  }
  
  
  
  // JUnitDoclet begin javadoc_method testVault
  /**
  * JUnitDoclet moves marker to this method, if there is not match
  * for them in the regenerated code and if the marker is not empty.
  * This way, no test gets lost when regenerating after renaming.
  * <b>Method testVault is supposed to be empty.</b>
  */
  // JUnitDoclet end javadoc_method testVault
  public void testVault() throws Exception {
    // JUnitDoclet begin method testcase.testVault
    // JUnitDoclet end method testcase.testVault
  }
  
  /**
  * Method to execute the TestCase from command line
  * using JUnit's textui.TestRunner .
  */
  public static void main(String[] args) {
    // JUnitDoclet begin method testcase.main
    junit.textui.TestRunner.run(FigureEnumeratorTest.class);
    // JUnitDoclet end method testcase.main
  }
}
