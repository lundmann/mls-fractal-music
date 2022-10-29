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

public final class NewtonSolver {
    private NewtonSolver() {
    }

    /**
     * Find a zero of the given complex polynomial by newton's method.
     *
     * @param p The given complex polynomial.
     * @param z0 A start value relative close to a guessed zero.
     * @param eps2 The square of the radius of the neighbourhood of 0.
     * @return A zero of the given complex
     */
    public static MutableComplex solve(ComplexPolynomial p, MutableComplex z0, double eps2) {
        if (p.degree() <= 1) {
            throw new IllegalArgumentException("Degree of polynomial must be at leat 2.");
        }

        if (eps2 <= 0.0) {
            throw new IllegalArgumentException("ε² must be positive.");
        }

        ComplexPolynomial d = p.derivative();
        MutableComplex z = z0.clone();
        double lastNorm = -1;

        while (true) {
            MutableComplex w = p.apply(z);
            double norm = w.norm();

            if (lastNorm > 0.0 && norm >= lastNorm) {
                throw new IllegalStateException("Iteration diverges.");
            }

            lastNorm = norm;

            if (norm <= eps2) {
                return z;
            }

            z.sub(w.div(d.apply(z)));
        }
    }
}
