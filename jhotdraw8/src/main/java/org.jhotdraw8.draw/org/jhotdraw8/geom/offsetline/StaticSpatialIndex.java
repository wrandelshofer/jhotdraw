package org.jhotdraw8.geom.offsetline;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.IntPredicate;

public class StaticSpatialIndex {
    private int[] m_indices;
    private double[] m_boxes;
    private static int NodeSize = 16;
    private int[] m_levelBounds;

    public StaticSpatialIndex(int numItems) {
        this(numItems, 16);
    }

    public StaticSpatialIndex(int numItems, int NodeSize) {
        assert numItems > 0 : "number of items must be greater than 0";
        assert NodeSize >= 2 && NodeSize <= 65535 : "node size must be between 2 and 65535";
        StaticSpatialIndex.NodeSize = NodeSize;
        // calculate the total number of nodes in the R-tree to allocate space for
        // and the index of each tree level (used in search later)
        m_numItems = numItems;
        int n = numItems;
        int numNodes = numItems;

        m_numLevels = computeNumLevels(numItems);
        m_levelBounds = new int[m_numLevels];
        m_levelBounds[0] = n * 4;
        // now populate level bounds and numNodes
        int i = 1;
        do {
            n = (int) Math.ceil((double) (n) / NodeSize);
            numNodes += n;
            m_levelBounds[i] = numNodes * 4;
            i += 1;
        } while (n != 1);

        m_numNodes = numNodes;
        m_boxes = (new double[numNodes * 4]);
        m_indices = (new int[numNodes]);
        m_pos = 0;
        m_minX = Double.POSITIVE_INFINITY;
        m_minY = Double.POSITIVE_INFINITY;
        m_maxX = Double.NEGATIVE_INFINITY;
        m_maxY = Double.NEGATIVE_INFINITY;
    }

    double minX() {
        return m_minX;
    }

    double minY() {
        return m_minY;
    }

    double maxX() {
        return m_maxX;
    }

    double maxY() {
        return m_maxY;
    }

    void add(double minX, double minY, double maxX, double maxY) {
        int index = m_pos >> 2;
        m_indices[index] = index;
        m_boxes[m_pos++] = minX;
        m_boxes[m_pos++] = minY;
        m_boxes[m_pos++] = maxX;
        m_boxes[m_pos++] = maxY;

        if (minX < m_minX) {
            m_minX = minX;
        }
        if (minY < m_minY) {
            m_minY = minY;
        }
        if (maxX > m_maxX) {
            m_maxX = maxX;
        }
        if (maxY > m_maxY) {
            m_maxY = maxY;
        }
    }

    void finish() {
        assert m_pos >> 2 == m_numItems : "added item count should equal static size given";

        // if number of items is less than node size then skip sorting since each node of boxes must be
        // fully scanned regardless and there is only one node
        if (m_numItems <= NodeSize) {
            m_indices[m_pos >> 2] = 0;
            // fill root box with total extents
            m_boxes[m_pos++] = m_minX;
            m_boxes[m_pos++] = m_minY;
            m_boxes[m_pos++] = m_maxX;
            m_boxes[m_pos++] = m_maxY;
            return;
        }

        double width = m_maxX - m_minX;
        double height = m_maxY - m_minY;
        int[] hilbertValues = new int[m_numItems];

        int pos = 0;

        for (int i = 0; i < m_numItems; ++i) {
            pos = 4 * i;
            double minX = m_boxes[pos++];
            double minY = m_boxes[pos++];
            double maxX = m_boxes[pos++];
            double maxY = m_boxes[pos++];

            // hilbert max input value for x and y
            final double hilbertMax = (double) ((1 << 16) - 1);
            // mapping the x and y coordinates of the center of the box to values in the range
            // [0 -> n - 1] such that the min of the entire set of bounding boxes maps to 0 and the max of
            // the entire set of bounding boxes maps to n - 1 our 2d space is x: [0 -> n-1] and
            // y: [0 -> n-1], our 1d hilbert curve value space is d: [0 -> n^2 - 1]
            double x = Math.floor(hilbertMax * ((minX + maxX) / 2 - m_minX) / width);
            int hx = (int) (x);
            double y = Math.floor(hilbertMax * ((minY + maxY) / 2 - m_minY) / height);
            int hy = (int) (y);
            hilbertValues[i] = hilbertXYToIndex(hx, hy);
        }

        // sort items by their Hilbert value (for packing later)
        sort(hilbertValues, m_boxes, m_indices, 0, m_numItems - 1);

        // generate nodes at each tree level, bottom-up
        pos = 0;
        for (int i = 0; i < m_numLevels - 1; i++) {
            int end = m_levelBounds[i];

            // generate a parent node for each block of consecutive <nodeSize> nodes
            while (pos < end) {
                double nodeMinX = Double.POSITIVE_INFINITY;
                double nodeMinY = Double.POSITIVE_INFINITY;
                double nodeMaxX = -1 * Double.POSITIVE_INFINITY;
                double nodeMaxY = -1 * Double.POSITIVE_INFINITY;
                int nodeIndex = pos;

                // calculate bbox for the new node
                for (int j = 0; j < NodeSize && pos < end; j++) {
                    double minX = m_boxes[pos++];
                    double minY = m_boxes[pos++];
                    double maxX = m_boxes[pos++];
                    double maxY = m_boxes[pos++];
                    if (minX < nodeMinX) {
                        nodeMinX = minX;
                    }
                    if (minY < nodeMinY) {
                        nodeMinY = minY;
                    }
                    if (maxX > nodeMaxX) {
                        nodeMaxX = maxX;
                    }
                    if (maxY > nodeMaxY) {
                        nodeMaxY = maxY;
                    }
                }

                // add the new node to the tree data
                m_indices[m_pos >> 2] = nodeIndex;
                m_boxes[m_pos++] = nodeMinX;
                m_boxes[m_pos++] = nodeMinY;
                m_boxes[m_pos++] = nodeMaxX;
                m_boxes[m_pos++] = nodeMaxY;
            }
        }
    }

    @FunctionalInterface
    public interface Visitor {
        boolean visit(int level, double xmin, double ymin, double xmax, double ymax);
    }

    // Visit all the bounding boxes in the spatial index. Visitor function has the signature
    // boolean(int level, double xmin, double ymin, double xmax, double ymax).
    // Visiting stops early if false is returned.
    void visitBoundingBoxes(Visitor visitor) {
        int nodeIndex = 4 * m_numNodes - 4;
        int level = m_numLevels - 1;

        Deque<Integer> stack = new ArrayDeque<>(16);

        boolean done = false;
        while (!done) {
            int end = Math.min(nodeIndex + NodeSize * 4, m_levelBounds[level]);
            for (int pos = nodeIndex; pos < end; pos += 4) {
                int index = m_indices[pos >> 2];
                if (!visitor.visit(level, m_boxes[pos], m_boxes[pos + 1], m_boxes[pos + 2], m_boxes[pos + 3])) {
                    return;
                }

                if (nodeIndex >= m_numItems * 4) {
                    stack.push(index);
                    stack.push(level - 1);
                }
            }

            if (stack.size() > 1) {
                level = stack.pop();
                nodeIndex = stack.pop();
            } else {
                done = true;
            }
        }
    }

    // Visit only the item bounding boxes in the spatial index. Visitor function has the signature
    // boolean(int index, double xmin, double ymin, double xmax, double ymax). Visiting stops early if
    // false is returned.
    void visitItemBoxes(Visitor visitor) {
        for (int i = 0; i < m_levelBounds[0]; i += 4) {
            if (!visitor.visit(m_indices[i >> 2], m_boxes[i], m_boxes[i + 1], m_boxes[i + 2], m_boxes[i + 3])) {
                return;
            }
        }
    }

    // See other overloads for details.
    void query(double minX, double minY, double maxX, double maxY, List<Integer> results) {
        IntPredicate visitor = (index) -> {
            results.add(index);
            return true;
        };

        visitQuery(minX, minY, maxX, maxY, visitor);
    }

    // Query the spatial index adding indexes to the results vector given. This overload accepts an
    // existing vector to use as a stack and takes care of clearing the stack before use.
    void query(double minX, double minY, double maxX, double maxY, List<Integer> results,
               Deque<Integer> stack) {
        IntPredicate visitor = (index) -> {
            results.add(index);
            return true;
        };

        visitQuery(minX, minY, maxX, maxY, visitor, stack);
    }

    // See other overloads for details.

    void visitQuery(double minX, double minY, double maxX, double maxY, IntPredicate visitor) {
        Deque<Integer> stack = new ArrayDeque<>(16);
        visitQuery(minX, minY, maxX, maxY, visitor, stack);
    }

    // Query the spatial index, invoking a visitor function for each index that overlaps the bounding
    // box given. Visitor function has the signature boolean(int index), if visitor returns false
    // the query stops early, otherwise the query continues. This overload accepts an existing vector
    // to use as a stack and takes care of clearing the stack before use.
    void visitQuery(double minX, double minY, double maxX, double maxY, IntPredicate visitor,
                    Deque<Integer> stack) {
        assert m_pos == 4 * m_numNodes : "data not yet indexed - call Finish() before querying";

        int nodeIndex = 4 * m_numNodes - 4;
        int level = m_numLevels - 1;

        stack.clear();

        boolean done = false;

        while (!done) {
            // find the end index of the node
            int end = Math.min(nodeIndex + NodeSize * 4, m_levelBounds[level]);

            // search through child nodes
            for (int pos = nodeIndex; pos < end; pos += 4) {
                int index = m_indices[pos >> 2];
                // check if node bbox intersects with query bbox
                if (maxX < m_boxes[pos] || maxY < m_boxes[pos + 1] || minX > m_boxes[pos + 2] ||
                        minY > m_boxes[pos + 3]) {
                    // no intersect
                    continue;
                }

                if (nodeIndex < m_numItems * 4) {
                    done = !visitor.test(index);
                    if (done) {
                        break;
                    }
                } else {
                    // push node index and level for further traversal
                    stack.push(index);
                    stack.push(level - 1);
                }
            }

            if (stack.size() > 1) {
                level = stack.pop();
                nodeIndex = stack.pop();
            } else {
                done = true;
            }
        }
    }

    static int hilbertXYToIndex(int x, int y) {
        int a = x ^ y;
        int b = 0xFFFF ^ a;
        int c = 0xFFFF ^ (x | y);
        int d = x & (y ^ 0xFFFF);

        int A = a | (b >> 1);
        int B = (a >> 1) ^ a;
        int C = ((c >> 1) ^ (b & (d >> 1))) ^ c;
        int D = ((a & (c >> 1)) ^ (d >> 1)) ^ d;

        a = A;
        b = B;
        c = C;
        d = D;
        A = (a & (a >> 2)) ^ (b & (b >> 2));
        B = (a & (b >> 2)) ^ (b & ((a ^ b) >> 2));
        C ^= (a & (c >> 2)) ^ (b & (d >> 2));
        D ^= (b & (c >> 2)) ^ ((a ^ b) & (d >> 2));

        a = A;
        b = B;
        c = C;
        d = D;
        A = (a & (a >> 4)) ^ (b & (b >> 4));
        B = (a & (b >> 4)) ^ (b & ((a ^ b) >> 4));
        C ^= (a & (c >> 4)) ^ (b & (d >> 4));
        D ^= (b & (c >> 4)) ^ ((a ^ b) & (d >> 4));

        a = A;
        b = B;
        c = C;
        d = D;
        C ^= ((a & (c >> 8)) ^ (b & (d >> 8)));
        D ^= ((b & (c >> 8)) ^ ((a ^ b) & (d >> 8)));

        a = C ^ (C >> 1);
        b = D ^ (D >> 1);

        int i0 = x ^ y;
        int i1 = b | (0xFFFF ^ (i0 | a));

        i0 = (i0 | (i0 << 8)) & 0x00FF00FF;
        i0 = (i0 | (i0 << 4)) & 0x0F0F0F0F;
        i0 = (i0 | (i0 << 2)) & 0x33333333;
        i0 = (i0 | (i0 << 1)) & 0x55555555;

        i1 = (i1 | (i1 << 8)) & 0x00FF00FF;
        i1 = (i1 | (i1 << 4)) & 0x0F0F0F0F;
        i1 = (i1 | (i1 << 2)) & 0x33333333;
        i1 = (i1 | (i1 << 1)) & 0x55555555;

        return (i1 << 1) | i0;
    }


    private double m_minX;
    private double m_minY;
    private double m_maxX;
    private double m_maxY;
    private int m_numItems;
    private int m_numLevels;

    private int m_numNodes;

    private int m_pos;

    static int computeNumLevels(int numItems) {
        int n = numItems;
        int levelBoundsSize = 1;
        do {
            n = (int) (Math.ceil((float) (n) / NodeSize));
            levelBoundsSize += 1;
        } while (n != 1);

        return levelBoundsSize;
    }

    // quicksort that partially sorts the bounding box data alongside the Hilbert values
    void sort(int[] values, double[] boxes, int[] indices, int left,
              int right) {
        assert left <= right : "left index should never be past right index";

        // check against NodeSize (only need to sort down to NodeSize buckets)
        if (left / NodeSize >= right / NodeSize) {
            return;
        }

        int pivot = values[(left + right) >> 1];
        int i = left - 1;
        int j = right + 1;

        while (true) {
            do {
                i++;
            }
            while (values[i] < pivot);
            do {
                j--;
            }
            while (values[j] > pivot);
            if (i >= j) {
                break;
            }
            swap(values, boxes, indices, i, j);
        }

        sort(values, boxes, indices, left, j);
        sort(values, boxes, indices, j + 1, right);
    }

    static void swap(int[] values, double[] boxes, int[] indices, int i,
                     int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;

        int k = 4 * i;
        int m = 4 * j;

        double a = boxes[k];
        double b = boxes[k + 1];
        double c = boxes[k + 2];
        double d = boxes[k + 3];
        boxes[k] = boxes[m];
        boxes[k + 1] = boxes[m + 1];
        boxes[k + 2] = boxes[m + 2];
        boxes[k + 3] = boxes[m + 3];
        boxes[m] = a;
        boxes[m + 1] = b;
        boxes[m + 2] = c;
        boxes[m + 3] = d;

        int e = indices[i];
        indices[i] = indices[j];
        indices[j] = e;
    }
}