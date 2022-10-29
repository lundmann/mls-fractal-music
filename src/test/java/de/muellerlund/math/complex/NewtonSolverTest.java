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

public class NewtonSolverTest {
    @Test
    public void testSquare() {
        ComplexPolynomial p = new ComplexPolynomial(MutableComplex.one(), MutableComplex.zero(), new MutableComplex(-2));
        MutableComplex w = NewtonSolver.solve(p, MutableComplex.one(), 1e-8);
        ComplexTestUtil.assertCloseTo(w, new MutableComplex(Math.sqrt(2)), Offset.offset(1e-5));
    }
}
