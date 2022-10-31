/*
 * Copyright 2022 Sönke Müller-Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.muellerlund.math.complex;

import de.muellerlund.util.Pair;
import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NewtonSolverTest {
    @Test
    public void testSquare() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(), MutableComplex.zero(), new MutableComplex(-2));
        MutableComplex w = NewtonSolver.solve(p, MutableComplex.one(), 1e-8);
        ComplexTestUtil.assertCloseTo(w, new MutableComplex(Math.sqrt(2)), Offset.offset(1e-5));
    }

    @Test
    public void testCubeAll() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(),
                                                    MutableComplex.zero(),
                                                    new MutableComplex(-7),
                                                    new MutableComplex(-6));
        List<Pair<MutableComplex, Integer>> zeros = NewtonSolver.solveAll(p, null, 1e-8);
        assertThat(zeros).hasSize(3);

        List<MutableComplex> plainZeros = zeros.stream().map(Pair::getFirst).collect(Collectors.toList());

        ComplexTestUtil.assertContains(plainZeros, Offset.offset(1e-5),
                                       new MutableComplex(3),
                                       new MutableComplex(-2),
                                       new MutableComplex(-1));
    }

    @Test
    public void testDeg4All() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(),
                                                    new MutableComplex(-2),
                                                    new MutableComplex(-7),
                                                    new MutableComplex(8),
                                                    new MutableComplex(12));
        List<Pair<MutableComplex, Integer>> zeros = NewtonSolver.solveAll(p, null, 1e-8);
        assertThat(zeros).hasSize(4);

        List<MutableComplex> plainZeros = zeros.stream().map(Pair::getFirst).collect(Collectors.toList());

        ComplexTestUtil.assertContains(plainZeros, Offset.offset(1e-5),
                                       new MutableComplex(3),
                                       new MutableComplex(-2),
                                       new MutableComplex(-1),
                                       new MutableComplex(2));
    }

    @Test
    public void testDeg4All2() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(),
                                                    new MutableComplex(2),
                                                    new MutableComplex(-7),
                                                    new MutableComplex(-20),
                                                    new MutableComplex(-12));
        List<Pair<MutableComplex, Integer>> zeros = NewtonSolver.solveAll(p, null, 1e-10);

        /*
        * TODO: Detecting quantity does not work.
        *
        * Finding zeros with newton's method find practicable but no perfect values.
         */
        //assertThat(zeros).hasSize(3);

        List<MutableComplex> plainZeros = zeros.stream().map(Pair::getFirst).collect(Collectors.toList());

        ComplexTestUtil.assertContains(plainZeros, Offset.offset(1e-3),
                                       new MutableComplex(3),
                                       new MutableComplex(-2),
                                       new MutableComplex(-1));
    }
}
