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

package de.muellerlund.ms.fractalmusic.calculation.fractals;

import de.muellerlund.math.complex.ComplexPolynomial;
import de.muellerlund.math.complex.MutableComplex;
import de.muellerlund.math.complex.NewtonSolver;
import de.muellerlund.math.complex.Zero;
import de.muellerlund.ms.fractalmusic.calculation.ComplexFractal;

import java.util.ArrayList;
import java.util.List;

public class PolynomialFractal implements ComplexFractal {
    private final ComplexPolynomial p;

    public PolynomialFractal(MutableComplex ... c) {
        p = new ComplexPolynomial(c);
    }

    @Override
    public int dimensions() {
        return p.degree();
    }

    @Override
    public List<MutableComplex> preImages(MutableComplex z) {
        ComplexPolynomial q = p.clone();
        q.move(z.clone().neg());

        List<Zero> zeros = NewtonSolver.solveAll(q, null, 1e-15);
        List<MutableComplex> solutions = new ArrayList<>();

        for (Zero zero : zeros) {
            solutions.add(zero.value());
        }

        return solutions;
    }
}
