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

import java.util.ArrayList;
import java.util.List;

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
        int d = p.degree();

        if (d <= 1) {
            throw new IllegalArgumentException("Degree of polynomial must be at leat 2.");
        }

        if (eps2 <= 0.0) {
            throw new IllegalArgumentException("ε² must be positive.");
        }

        ComplexPolynomial pd = p.derivative();
        MutableComplex z = z0.clone();

        while (true) {
            MutableComplex w = p.apply(z);
            double norm = w.norm();

            if (norm <= eps2) {
                return z;
            }

            MutableComplex qt = w.clone().div(pd.apply(z));
            MutableComplex[] next = new MutableComplex[d];

            for (int k = 0; k < d; k++) {
                next[k] = z.clone().sub(qt.clone().rmult(k + 1));
            }

            int q = -1;
            double mn = Double.MAX_VALUE;

            for (int k = 0; k < d; k++) {
                double nn = z.clone().sub(next[k]).norm();

                if (nn < mn) {
                    q = k + 1;
                    mn = nn;
                }
            }

            z = next[q - 1];
        }
    }

    public static List<MutableComplex> solveAll(ComplexPolynomial p, MutableComplex z0, double eps2) {
        if (eps2 <= 0.0) {
            throw new IllegalArgumentException("ε² must be positive.");
        }

        if (z0 == null) {
            z0 = MutableComplex.zero();
        }

        List<MutableComplex> zeros = new ArrayList<>();
        int n = p.degree();

        switch (n) {
            case -1:
            case 0:
                break;

            case 1:
                MutableComplex z = p.coefficient(0).div(p.coefficient(1)).neg();
                zeros.add(z);
                break;

            case 2:
                ComplexPolynomial pn = p.normalize();
                MutableComplex p2 = pn.coefficient(1).rmult(0.5);
                MutableComplex dis = p2.clone().mult(p2).sub(p.coefficient(0)).sqrt();
                zeros.add(p2.clone().neg().add(dis));
                zeros.add(p2.clone().neg().sub(dis));

                break;

            default:
                MutableComplex eta = solve(p, z0, eps2);
                zeros.add(eta);
                zeros.addAll(solveAll(p.splitZero(eta), eta, eps2));
                break;
        }

        return zeros;
    }
}
