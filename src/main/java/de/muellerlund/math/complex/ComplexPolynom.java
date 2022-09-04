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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ComplexPolynom implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 2727764094976172956L;

    private List<MutableComplex> coefficients = new ArrayList<>();

    public ComplexPolynom(MutableComplex ... coefficients) {
        int n = coefficients.length;

        for (int k = 0; k < coefficients.length; k++) {
            this.coefficients.add(coefficients[n - k - 1].clone());
        }
    }

    public int dimension() {
        return coefficients.size() - 1;
    }

    public MutableComplex coefficient(int k) {
        int n = dimension();
        return k > n || k < 0 ? MutableComplex.zero() : coefficients.get(k).clone();
    }

    @Override
    public ComplexPolynom clone() {
        try {
            ComplexPolynom newPolynom = (ComplexPolynom) super.clone();
            newPolynom.coefficients = new ArrayList<>();

            for (MutableComplex z : coefficients) {
                newPolynom.coefficients.add(z.clone());
            }

            return newPolynom;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public MutableComplex apply(MutableComplex z) {
        int n = dimension();

        if (n < 0) {
            return MutableComplex.zero();
        }

        MutableComplex[] powers = new MutableComplex[n + 1];
        MutableComplex p = MutableComplex.one();

        for (int k = 0; k <= n; k++) {
            powers[k] = p.clone();
            p.mult(z);
        }

        MutableComplex s = MutableComplex.zero();

        for (int k = 0; k <= n; k++) {
            s.add(coefficient(k).mult(powers[k]));
        }

        return s;
    }

    public ComplexPolynom derivative() {
        int n = dimension();

        if (n <= 0) {
            return new ComplexPolynom();
        }

        MutableComplex[] nc = new MutableComplex[n];

        for (int k = 1; k <= n; k++) {
            nc[n - k] = coefficient(k).rmult(k);
        }

        return new ComplexPolynom(nc);
    }

    public ComplexPolynom integrate() {
        return integrate(MutableComplex.zero());
    }

    public ComplexPolynom integrate(MutableComplex c) {
        int n = dimension();

        MutableComplex[] nc = new MutableComplex[n + 2];
        nc[n + 1] = c.clone();

        for (int k = 0; k <= n; k++) {
            nc[n - k] = coefficient(k).rmult(1.0 / (k + 1));
        }

        return new ComplexPolynom(nc);
    }
}
