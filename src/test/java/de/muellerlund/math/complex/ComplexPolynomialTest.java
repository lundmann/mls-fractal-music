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

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComplexPolynomialTest {
    @Test
    public void testConstruction() {
        ComplexPolynomial p = new ComplexPolynomial();
        assertThat(p.degree()).isEqualTo(-1);

        p = new ComplexPolynomial(MutableComplex.one());
        assertThat(p.degree()).isEqualTo(0);
        ComplexTestUtil.assertCloseTo(p.coefficient(0), MutableComplex.one());
        ComplexTestUtil.assertCloseTo(p.coefficient(-1), MutableComplex.zero());
        ComplexTestUtil.assertCloseTo(p.coefficient(1), MutableComplex.zero());

        p = new ComplexPolynomial(new MutableComplex(0, 2), MutableComplex.zero(), new MutableComplex(-1));
        assertThat(p.degree()).isEqualTo(2);
        ComplexTestUtil.assertCloseTo(p.coefficient(3), MutableComplex.zero());
        ComplexTestUtil.assertCloseTo(p.coefficient(2), new MutableComplex(0, 2));
        ComplexTestUtil.assertCloseTo(p.coefficient(1), MutableComplex.zero());
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(-1));
        ComplexTestUtil.assertCloseTo(p.coefficient(-1), MutableComplex.zero());
    }

    @Test
    public void testTrivialApply() {
        ComplexPolynomial p = new ComplexPolynomial();
        for (MutableComplex z : ComplexTestUtil.someNumbers()) {
            MutableComplex w = p.apply(z);
            ComplexTestUtil.assertCloseTo(w, MutableComplex.zero());
        }

        for (MutableComplex c : ComplexTestUtil.someNumbers()) {
            p = new ComplexPolynomial(c);
            for (MutableComplex z : ComplexTestUtil.someNumbers()) {
                MutableComplex w = p.apply(z);
                ComplexTestUtil.assertCloseTo(w, c);
            }
        }
    }

    @Test
    public void testAffineApply() {
        ComplexPolynomial p = new ComplexPolynomial(new MutableComplex(2), new MutableComplex(1, -1));
        for (MutableComplex z : ComplexTestUtil.someNumbers()) {
            MutableComplex w = p.apply(z);
            MutableComplex v = z.clone().rmult(2).add(new MutableComplex(1, -1));
            ComplexTestUtil.assertCloseTo(w, v);
        }
    }

    @Test
    public void testSquareApply() {
        ComplexPolynomial p = new ComplexPolynomial(new MutableComplex(0, 2), MutableComplex.zero(), new MutableComplex(-1));
        for (MutableComplex z : ComplexTestUtil.someNumbers()) {
            MutableComplex w = p.apply(z);
            MutableComplex v = z.clone().mult(z).imult(2).sub(MutableComplex.one());
            ComplexTestUtil.assertCloseTo(w, v);
        }
    }

    @Test
    public void testTrivialDerivatives() {
        ComplexPolynomial p = new ComplexPolynomial().derivative();
        assertThat(p.degree()).isEqualTo(-1);

        for (MutableComplex c : ComplexTestUtil.someNumbers()) {
            p = new ComplexPolynomial(c).derivative();
            assertThat(p.degree()).isEqualTo(-1);
        }

        for (MutableComplex c : ComplexTestUtil.someNumbers()) {
            p = new ComplexPolynomial(c, MutableComplex.one()).derivative();
            assertThat(p.degree()).isEqualTo(0);
            ComplexTestUtil.assertCloseTo(p.coefficient(0), c);
        }
    }

    @Test
    public void testDerivatives() {
        ComplexPolynomial p = new ComplexPolynomial(new MutableComplex(0, 2), new MutableComplex(4, -1),
                                                    new MutableComplex(-1)).derivative();
        assertThat(p.degree()).isEqualTo(1);
        ComplexTestUtil.assertCloseTo(p.coefficient(1), new MutableComplex(0, 4));
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(4, -1));

        p = new ComplexPolynomial(new MutableComplex(3, 1),
                                  new MutableComplex(0, 2),
                                  new MutableComplex(4, -1),
                                  new MutableComplex(-1)).derivative();
        assertThat(p.degree()).isEqualTo(2);
        ComplexTestUtil.assertCloseTo(p.coefficient(2), new MutableComplex(9, 3));
        ComplexTestUtil.assertCloseTo(p.coefficient(1), new MutableComplex(0, 4));
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(4, -1));
    }

    @Test
    public void testTrivialIntegrals() {
        ComplexPolynomial p = new ComplexPolynomial().integral();
        assertThat(p.degree()).isEqualTo(0);
        ComplexTestUtil.assertCloseTo(p.coefficient(0), MutableComplex.zero());

        for (MutableComplex c : ComplexTestUtil.someNumbers()) {
            p = new ComplexPolynomial().integral(c);
            assertThat(p.degree()).isEqualTo(0);
            ComplexTestUtil.assertCloseTo(p.coefficient(0), c);
        }
    }

    @Test
    public void testIntegrals() {
        ComplexPolynomial p = new ComplexPolynomial(new MutableComplex(0, 2), new MutableComplex(4, -1),
                                                    new MutableComplex(-1)).integral(new MutableComplex(1, -2));
        assertThat(p.degree()).isEqualTo(3);
        ComplexTestUtil.assertCloseTo(p.coefficient(3), new MutableComplex(0, 2.0 / 3.0));
        ComplexTestUtil.assertCloseTo(p.coefficient(2), new MutableComplex(2, -0.5));
        ComplexTestUtil.assertCloseTo(p.coefficient(1), new MutableComplex(-1));
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(1, -2));

        p = new ComplexPolynomial(new MutableComplex(3, 1),
                                  new MutableComplex(0, 2),
                                  new MutableComplex(4, -1),
                                  new MutableComplex(-1)).integral(new MutableComplex(1, -2));
        assertThat(p.degree()).isEqualTo(4);
        ComplexTestUtil.assertCloseTo(p.coefficient(4), new MutableComplex(0.75, 0.25));
        ComplexTestUtil.assertCloseTo(p.coefficient(3), new MutableComplex(0, 2.0 / 3.0));
        ComplexTestUtil.assertCloseTo(p.coefficient(2), new MutableComplex(2, -0.5));
        ComplexTestUtil.assertCloseTo(p.coefficient(1), new MutableComplex(-1));
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(1, -2));

        p = p.derivative();
        assertThat(p.degree()).isEqualTo(3);
        ComplexTestUtil.assertCloseTo(p.coefficient(3), new MutableComplex(3, 1));
        ComplexTestUtil.assertCloseTo(p.coefficient(2), new MutableComplex(0, 2));
        ComplexTestUtil.assertCloseTo(p.coefficient(1), new MutableComplex(4, -1));
        ComplexTestUtil.assertCloseTo(p.coefficient(0), new MutableComplex(-1));
    }
}
