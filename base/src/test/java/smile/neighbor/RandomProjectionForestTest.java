/*
 * Copyright (c) 2010-2025 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */
package smile.neighbor;

import smile.math.MathEx;
import smile.test.data.USPS;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("rawtypes")
public class RandomProjectionForestTest {
    double[][] x = USPS.x;
    double[][] testx = USPS.testx;

    public RandomProjectionForestTest() {
        MathEx.setSeed(19650218); // to get repeatable results.
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testKnn() {
        System.out.println("knn");

        RandomProjectionForest forest = RandomProjectionForest.of(x, 5, 10, false);
        LinearSearch<double[], double[]> naive = LinearSearch.of(x, MathEx::distance);
        int[] recall = new int[testx.length];
        for (int i = 0; i < testx.length; i++) {
            int k = 7;
            Neighbor[] n1 = forest.search(testx[i], k);
            Neighbor[] n2 = naive.search(testx[i], k);
            for (Neighbor m2 : n2) {
                for (Neighbor m1 : n1) {
                    if (m1.index() == m2.index()) {
                        recall[i]++;
                        break;
                    }
                }
            }
        }

        System.out.format("q1     of recall is %d%n", MathEx.q1(recall));
        System.out.format("median of recall is %d%n", MathEx.median(recall));
        System.out.format("q3     of recall is %d%n", MathEx.q3(recall));
        assertEquals(7, MathEx.q3(recall));
    }

    @Test
    public void testAngular() {
        System.out.println("angular");

        RandomProjectionForest forest = RandomProjectionForest.of(x, 15, 10, true);
        LinearSearch<double[], double[]> naive = LinearSearch.of(x, MathEx::angular);
        int[] recall = new int[testx.length];
        for (int i = 0; i < testx.length; i++) {
            int k = 7;
            Neighbor[] n1 = forest.search(testx[i], k);
            Neighbor[] n2 = naive.search(testx[i], k);
            for (Neighbor m2 : n2) {
                for (Neighbor m1 : n1) {
                    if (m1.index() == m2.index()) {
                        recall[i]++;
                        break;
                    }
                }
            }
        }

        System.out.format("q1     of recall is %d%n", MathEx.q1(recall));
        System.out.format("median of recall is %d%n", MathEx.median(recall));
        System.out.format("q3     of recall is %d%n", MathEx.q3(recall));
        assertEquals(7, MathEx.q3(recall));
    }

    @Test
    public void testSpeed() {
        System.out.println("Speed");

        RandomProjectionForest forest = RandomProjectionForest.of(x, 5, 10, false);
        long start = System.currentTimeMillis();
        for (double[] xi : testx) {
            forest.search(xi, 10);
        }
        double time = (System.currentTimeMillis() - start) / 1000.0;
        System.out.format("10-NN: %.2fs%n", time);
    }
}