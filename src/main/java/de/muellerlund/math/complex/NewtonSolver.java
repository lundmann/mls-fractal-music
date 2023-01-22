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
    public static Zero solve(ComplexPolynomial p, MutableComplex z0, double eps2) {
        int d = p.degree();

        if (d <= 1) {
            throw new IllegalArgumentException("Degree of polynomial must be at leat 2.");
        }

        if (eps2 <= 0.0) {
            throw new IllegalArgumentException("ε² must be positive.");
        }

        ComplexPolynomial pd = p.derivative();
        MutableComplex z = z0.clone();
        MutableComplex w = p.apply(z);

        if (w.norm() < eps2) { // maybe we enter with a zero
            ComplexPolynomial q = pd;
            for (int k = 1; k <= d; k++) {
                if (q.apply(z).norm() >= eps2) {
                    return new Zero(z, k);
                }
                q = q.derivative();
            }

            // should not happen! 😜
            throw new IllegalStateException("Unexpected zero state.");
        }

        while (true) {
            MutableComplex dd = getDivider(pd, z);
            MutableComplex qt = w.clone().div(dd);
            if (qt.isInfinite()) {
                // should not happen! 😜
                throw new ArithmeticException("Division by zero.");
            }

            double mn = Double.MAX_VALUE;
            MutableComplex mz = MutableComplex.zero();
            int q = 0;

            for (int k = 0; k < d; k++) {
                MutableComplex nz = z.clone().sub(qt.clone().rmult(k + 1));
                MutableComplex nw = p.apply(nz);
                double nn = nw.norm();

                if (nn < mn) {
                    q = k;
                    mn = nn;
                    mz = nz;
                    w = nw;
                }
                else {
                    break;
                }
            }

            if (mn < eps2) {
                return new Zero(mz, q + 1);
            }

            z = mz;
        }
    }

    private static MutableComplex getDivider(ComplexPolynomial pd, MutableComplex z) {
        MutableComplex w = MutableComplex.zero();
        while (w.assign(pd.apply(z)).isZero()) {
            pd = pd.derivative();
            if (pd.degree() == 0 && pd.get(0).isZero()) {
                throw new IllegalArgumentException("Illegal zero-Polynomial.");
            }
        }

        return w;
    }

    public static List<Zero> solveAll(ComplexPolynomial p, MutableComplex z0, double eps2) {
        if (eps2 <= 0.0) {
            throw new IllegalArgumentException("ε² must be positive.");
        }

        if (z0 == null) {
            z0 = MutableComplex.zero();
        }

        List<Zero> zeros = new ArrayList<>();
        int n = p.degree();

        switch (n) {
            case -1:
            case 0:
                break;

            case 1:
                MutableComplex z = p.get(0).div(p.get(1)).neg();
                zeros.add(new Zero(z));
                break;

            case 2:
                ComplexPolynomial pn = p.normalize();
                MutableComplex p2 = pn.get(1).rmult(0.5);
                MutableComplex dis = p2.clone().mult(p2).sub(p.get(0)).sqrt();
                p2.neg();

                if (dis.norm() <= eps2) {
                    zeros.add(new Zero(p2, 2));
                }
                else {
                    zeros.add(new Zero(p2.clone().add(dis)));
                    zeros.add(new Zero(p2.clone().sub(dis)));
                }

                break;

            default:
                Zero eta = solve(p, z0, eps2);
                zeros.add(eta);

                for (int i = 0; i < eta.quantity(); i++) {
                    p = p.splitZero(eta.value());
                }

                zeros.addAll(solveAll(p, eta.value(), eps2));
                break;
        }

        return zeros;
    }
}
