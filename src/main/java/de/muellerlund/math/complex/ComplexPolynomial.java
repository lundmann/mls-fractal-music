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

/**
 * Class representing complex polynomials.
 */
public final class ComplexPolynomial implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 2727764094976172956L;

    private List<MutableComplex> coefficients = new ArrayList<>();

    /**
     * "Natural" constructor, the highest order coefficient appears first.
     *
     * <p>If no coefficient is given an empty polynomial was created which has only boundary conditions purposes.</p>
     * <p>Beware: The first coefficient should not be zero if at least two coefficients are given!</p>
     *
     * @param coefficients Complex coefficients, the highest order coefficient appears first.
     */
    public ComplexPolynomial(MutableComplex ... coefficients) {
        int n = coefficients.length;

        for (int k = 0; k < coefficients.length; k++) {
            this.coefficients.add(coefficients[n - k - 1].clone());
        }
    }

    /**
     * Returns the degree of this polynomial which is the order of the first given coefficient.
     *
     * <p>If the polynomial is empty the degree is defined by {@code -1}.</p>
     *
     * @return The degree of this polynomial.
     */
    public int degree() {
        return coefficients.size() - 1;
    }

    /**
     * Returns a clone of the k'th coefficient of this polynomial.
     *
     * <p>If k is not in range of {@code [0, this.degree()]} this method returns zero.</p>
     * @param k The index of the requested coefficient.
     *
     * @return a clone of the k'th coefficient of this polynomial.
     */
    public MutableComplex coefficient(int k) {
        int n = degree();
        return k > n || k < 0 ? MutableComplex.zero() : coefficients.get(k).clone();
    }

    /**
     * Returns a deep clone of this polynomial.
     *
     * @return a deep clone of this polynomial.
     */
    @Override
    public ComplexPolynomial clone() {
        try {
            ComplexPolynomial newPolynom = (ComplexPolynomial) super.clone();
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

    /**
     * Applies this polynomial at the given number.
     *
     * @param z The complex number.
     *
     * @return the result of this application.
     */
    public MutableComplex apply(MutableComplex z) {
        int n = degree();

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

    public ComplexPolynomial normalize() {
        ComplexPolynomial p = clone();
        int n = degree();

        if (n >= 1) {
            MutableComplex d = coefficient(n);
            p.coefficients.get(n).assign(MutableComplex.one());

            for (int k = 0; k < n; k++) {
                p.coefficients.get(k).div(d);
            }
        }

        return p;
    }

    /**
     * Returns the (first order) derivative of this polynomial.
     *
     * <p>If the degree is 0 (constant) or -1 (empty) this method returns the empty polynomial.</p>
     *
     * @return the (first order) derivative of this polynomial.
     * @see <a href="https://en.wikipedia.org/wiki/Formal_derivative">Formal derivative</a>
     */
    public ComplexPolynomial derivative() {
        int n = degree();

        if (n <= 0) {
            return new ComplexPolynomial();
        }

        MutableComplex[] nc = new MutableComplex[n];

        for (int k = 1; k <= n; k++) {
            nc[n - k] = coefficient(k).rmult(k);
        }

        return new ComplexPolynomial(nc);
    }

    /**
     * Convenience method to create an integral with a zero offset.
     *
     * @return {@code integral(MutableComplex.zero())}
     * @see #integral(MutableComplex)
     */
    public ComplexPolynomial integral() {
        return integral(MutableComplex.zero());
    }

    /**
     * Returns the (first order) integral of this polynomial.
     *
     * @param c The (complex) constant (offset of the integral).
     *
     * @return the (first order) integral of this polynomial.
     * @see <a href="https://en.wikipedia.org/wiki/Integral">Integral</a>
     */
    public ComplexPolynomial integral(MutableComplex c) {
        int n = degree();

        MutableComplex[] nc = new MutableComplex[n + 2];
        nc[n + 1] = c.clone();

        for (int k = 0; k <= n; k++) {
            nc[n - k] = coefficient(k).rmult(1.0 / (k + 1));
        }

        return new ComplexPolynomial(nc);
    }

    public ComplexPolynomial multiply(ComplexPolynomial q) {
        int n = degree();
        int m = q.degree();

        if (n < 0) {
            return this;
        }

        if (m < 0) {
            return q;
        }

        if (n < m) {
            return q.multiply(this);
        }

        int nm = n + m;
        MutableComplex[] nc = new MutableComplex[nm + 1];

        if (m == 0) {
            MutableComplex b0 = q.coefficient(0);
            for (int j = 0; j <= n; j++) {
                nc[n - j] = coefficient(j).mult(b0);
            }
        }
        else {
            for (int j = 0; j <= nm; j++) {
                //int cj = nm - j;
                MutableComplex sum = MutableComplex.zero();
                for (int i = 0; i <= n; i++) {
                    int ai = n - i;
                    int bi = m - j + i;
                    if (ai >= 0 && ai <= n && bi >= 0 && bi <= m) {
                        sum.add(coefficient(ai).mult(q.coefficient(bi)));
                    }
                }
                nc[j] = sum.clone();
            }
        }

        return new ComplexPolynomial(nc);
    }

    /**
     * Returns the result of {@code this / (z - η)}.
     * @param eta (η) A zero of this.
     * @return this divide by (z - η).
     */
    public ComplexPolynomial splitZero(MutableComplex eta) {
        int n = degree();

        if (n <= 0) {
            return new ComplexPolynomial();
        }

        MutableComplex[] nc = new MutableComplex[n];
        MutableComplex carry = MutableComplex.zero();

        for (int k = n - 1; k >= 0; k--) {
            MutableComplex c = coefficient(k + 1);
            nc[n - k - 1] = c.clone().add(carry);
            carry.add(c).mult(eta);
        }

        return new ComplexPolynomial(nc);
    }
}
