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

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NewtonSolverTest {
    @Test
    public void testSquare() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(), MutableComplex.zero(), new MutableComplex(-2));
        Zero eta = NewtonSolver.solve(p, MutableComplex.one(), 1e-8);
        ComplexTestUtil.assertCloseTo(eta.value(), new MutableComplex(Math.sqrt(2)), Offset.offset(1e-5));
    }

    @Test
    public void testSquare2() {
        List<MutableComplex> givenZeros = Arrays.asList(
                new MutableComplex(1),
                new MutableComplex(1)
        );
        ComplexPolynomial p = ComplexPolynomial.byZeros(givenZeros);
        List<Zero> zeros = NewtonSolver.solveAll(p, null, 1e-8);
        assertThat(zeros).hasSize(1);

        Zero zero = zeroByValue(zeros, new MutableComplex(1), 1e-5);
        assertThat(zero).isNotNull();
        assertThat(zero.quantity()).isEqualTo(2);
    }

    @Test
    public void testCubeAll() {
        List<MutableComplex> givenZeros = Arrays.asList(
                new MutableComplex(3),
                new MutableComplex(-2, 1),
                new MutableComplex(0, 2)
        );
        ComplexPolynomial p = ComplexPolynomial.byZeros(givenZeros);
        List<Zero> zeros = NewtonSolver.solveAll(p, null, 1e-8);
        assertThat(zeros).hasSize(3);

        List<MutableComplex> solutions = zeros.stream().map(Zero::value).collect(Collectors.toList());
        ComplexTestUtil.assertContains(solutions, Offset.offset(1e-5), givenZeros);
    }

    @Test
    public void testCubeAll2() {
        List<MutableComplex> givenZeros = Arrays.asList(
                new MutableComplex(1),
                new MutableComplex(1),
                new MutableComplex(1)
        );
        ComplexPolynomial p = ComplexPolynomial.byZeros(givenZeros);
        List<Zero> zeros = NewtonSolver.solveAll(p, null, 1e-8);
        assertThat(zeros).hasSize(1);

        Zero zero = zeroByValue(zeros, new MutableComplex(1), 1e-5);
        assertThat(zero).isNotNull();
        assertThat(zero.quantity()).isEqualTo(3);
    }

    @Test
    public void testDeg4All() {
        List<MutableComplex> givenZeros = Arrays.asList(
                new MutableComplex(3, -0.5),
                new MutableComplex(-2, 1),
                new MutableComplex(0, 2),
                new MutableComplex(1, 1)
        );
        ComplexPolynomial p = ComplexPolynomial.byZeros(givenZeros);
        List<Zero> zeros = NewtonSolver.solveAll(p, null, 1e-15);
        assertThat(zeros).hasSize(4);

        List<MutableComplex> solutions = zeros.stream().map(Zero::value).collect(Collectors.toList());
        ComplexTestUtil.assertContains(solutions, Offset.offset(1e-5), givenZeros);
    }

    @Test
    public void testDeg4All2() {
        List<MutableComplex> givenZeros = Arrays.asList(
                new MutableComplex(3, -0.5),
                new MutableComplex(-2, 1),
                new MutableComplex(0, 2),
                new MutableComplex(0, 2)
        );
        ComplexPolynomial p = ComplexPolynomial.byZeros(givenZeros);
        List<Zero> zeros = NewtonSolver.solveAll(p, null, 1e-15);
        assertThat(zeros).hasSize(3);

        List<MutableComplex> solutions = zeros.stream().map(Zero::value).collect(Collectors.toList());
        ComplexTestUtil.assertContains(solutions, Offset.offset(1e-5), givenZeros);

        Zero zero = zeroByValue(zeros, new MutableComplex(3, -0.5), 1e-5);
        assertThat(zero).isNotNull();
        assertThat(zero.quantity()).isEqualTo(1);

        zero = zeroByValue(zeros, new MutableComplex(-2, 1), 1e-5);
        assertThat(zero).isNotNull();
        assertThat(zero.quantity()).isEqualTo(1);

        zero = zeroByValue(zeros, new MutableComplex(0, 2), 1e-5);
        assertThat(zero).isNotNull();
        assertThat(zero.quantity()).isEqualTo(2);
    }

    private static Zero zeroByValue(Iterable<Zero> zeros, MutableComplex value, double eps2) {
        for (Zero zero : zeros) {
            if (zero.value().clone().sub(value).norm() < eps2) {
                return zero;
            }
        }

        return null;
    }
}
