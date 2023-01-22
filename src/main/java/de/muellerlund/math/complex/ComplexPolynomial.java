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
import java.util.Arrays;
import java.util.List;

/**
 * Class representing complex polynomials.
 */
public final class ComplexPolynomial implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 2727764094976172956L;

    private List<MutableComplex> coefficients;

    private ComplexPolynomial(int n) {
        coefficients = new ArrayList<>();

        for (int k = 0; k <= n; k++) {
            coefficients.add(MutableComplex.zero());
        }
    }

    /**
     * "Natural" constructor, the highest order coefficient appears first.
     *
     * <p>If no coefficient is given an empty polynomial was created which has only boundary conditions purposes.</p>
     * <p>Beware: The first coefficient should not be zero if at least two coefficients are given!</p>
     *
     * @param coefficients Complex coefficients, the highest order coefficient appears first.
     */
    public ComplexPolynomial(MutableComplex ... coefficients) {
        this.coefficients = new ArrayList<>();

        for (MutableComplex c : coefficients) {
            this.coefficients.add(c.clone());
        }
    }

    public static ComplexPolynomial one() {
        return new ComplexPolynomial(MutableComplex.one());
    }

    public static ComplexPolynomial byZeros(Iterable<MutableComplex> zeroes) {
        ComplexPolynomial p = one();

        for (MutableComplex zero : zeroes) {
            ComplexPolynomial q = new ComplexPolynomial(MutableComplex.one(), zero.clone().neg());
            p = p.multiply(q);
        }

        return p;
    }

    public static ComplexPolynomial byZeros(MutableComplex ... zeroes) {
        return byZeros(Arrays.asList(zeroes));
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

    public MutableComplex get(int index) {
        int n = degree();
        return index >= 0 && index <= n ? coefficients.get(n - index).clone() : MutableComplex.zero();
    }

    private void set(int index, MutableComplex z) {
        int n = degree();
        coefficients.set(n - index, z.clone());
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
            s.add(get(k).mult(powers[k]));
        }

        return s;
    }

    public ComplexPolynomial normalize() {
        ComplexPolynomial p = clone();
        int n = degree();

        if (n >= 0) {
            MutableComplex d = get(n);
            p.set(n, MutableComplex.one());

            for (int k = 0; k < n; k++) {
                p.set(k, get(k).div(d));
            }
        }

        return p;
    }

    public void move(MutableComplex z) {
        if (!coefficients.isEmpty()) {
            set(0, get(0).add(z));
        }
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

        ComplexPolynomial p = new ComplexPolynomial(n - 1);

        for (int k = 1; k <= n; k++) {
            p.set(k - 1, get(k).rmult(k));
        }

        return p;
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

        ComplexPolynomial p = new ComplexPolynomial(n + 1);
        p.set(0, c);

        for (int k = 0; k <= n; k++) {
            p.set(k + 1, get(k).rmult(1.0 / (k + 1)));
        }

        return p;
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
        ComplexPolynomial p = new ComplexPolynomial(nm);

        if (m == 0) {
            MutableComplex b0 = q.get(0);
            for (int j = 0; j <= n; j++) {
                p.set(j, get(j).mult(b0));
            }
        }
        else {
            for (int j = 0; j <= nm; j++) {
                MutableComplex sum = MutableComplex.zero();
                for (int i = 0; i <= n; i++) {
                    if (j - i >= 0 && j - i <= m) {
                        sum.add(get(i).mult(q.get(j - i)));
                    }
                }
                p.set(j, sum);
            }
        }

        return p;
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

        ComplexPolynomial p = new ComplexPolynomial(n - 1);
        MutableComplex carry = MutableComplex.zero();

        for (int k = n - 1; k >= 0; k--) {
            MutableComplex c = get(k + 1);
            carry.add(c);
            p.set(k, carry);
            carry.mult(eta);
        }

        return p;
    }
}
