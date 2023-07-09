/*
 * Copyright 2023 Sönke Müller-Lund
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

import org.apache.commons.math3.complex.Complex;

import java.io.Serial;
import java.io.Serializable;

public final class MutableComplex implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static MutableComplex zero() {
        return new MutableComplex();
    }

    public static MutableComplex one() {
        return new MutableComplex(1);
    }

    public static MutableComplex i() {
        return new MutableComplex(0, 1);
    }

    // real and imaginary part
    private double re;
    private double im;

    /**
     * Creates the complex zero value.
     */
    public MutableComplex() {
        this(0, 0);
    }

    /**
     * Creates a complex number from the given double value setting the real part and left the imaginary part left to
     * zero.
     *
     * @param re The real part to be set.
     */
    public MutableComplex(double re) {
        this(re, 0);
    }

    /**
     * Creates a complex number from the given real and imaginary part.
     *
     * @param re The real part to be set.
     * @param im The imaginary part to be set.
     */
    public MutableComplex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public MutableComplex(Complex z) {
        re = z.getReal();
        im = z.getImaginary();
    }

    public MutableComplex assign(double re, double im) {
        this.re = re;
        this.im = im;

        return this;
    }

    public MutableComplex assign(MutableComplex z) {
        re = z.re;
        im = z.im;

        return this;
    }

    /**
     * Returns the real part of this complex number.
     *
     * @return The real part of this complex number.
     */
    public double real() {
        return re;
    }

    /**
     * Returns the imaginary part of this complex number.
     *
     * @return The imaginary part of this complex number.
     */
    public double imag() {
        return im;
    }

    public Complex complex() {
        return new Complex(re, im);
    }

    @Override
    public MutableComplex clone() {
        try {
            return (MutableComplex) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new Error(ex);
        }
    }

    @Override
    public String toString() {
        return complex().toString();
    }

    public boolean isZero() {
        return re == 0.0 && im == 0.0;
    }

    public boolean isNaN() {
        return Double.isNaN(re) || Double.isNaN(im);
    }

    public boolean isInfinite() {
        return Double.isInfinite(re) || Double.isInfinite(im);
    }

    public MutableComplex neg() {
        re = -re;
        im = -im;

        return this;
    }

    public MutableComplex conj() {
        im = -im;

        return this;
    }

    public MutableComplex inv() {
        double n = norm();
        re /= n;
        im /= -n;

        return this;
    }

    public double norm() {
        return re * re + im * im;
    }

    public double abs() {
        return Math.sqrt(norm());
    }

    public double arg() {
        return arg0(abs());
    }

    private double arg0(double r) {
        if (r == 0) {
            return 0;
        }

        double ac = Math.acos(re / r);

        return im >= 0 ? ac : -ac;
    }

    public static MutableComplex polar(double r, double phi) {
        return new MutableComplex(r * Math.cos(phi), r * Math.sin(phi));
    }

    public double[] polar() {
        double[] v = new double[2];

        v[0] = abs();
        v[1] = arg0(v[0]);

        return v;
    }

    public MutableComplex add(MutableComplex w) {
        re += w.re;
        im += w.im;

        return this;
    }

    public MutableComplex sub(MutableComplex w) {
        re -= w.re;
        im -= w.im;

        return this;
    }

    public MutableComplex mult(MutableComplex w) {
        double t = re * w.re - im * w.im;
        im = re * w.im + im * w.re;
        re = t;

        return this;
    }

    public MutableComplex rmult(double t) {
        re *= t;
        im *= t;

        return this;
    }

    public MutableComplex imult(double t) {
        double s = -im * t;
        im = re * t;
        re = s;

        return this;
    }

    public MutableComplex div(MutableComplex w) {
        double nw = w.norm();

        if (nw > 0.0) {
            double t = (re * w.re + im * w.im) / nw;
            im = (im * w.re - re * w.im) / nw;
            re = t;
        }
        else {
            //noinspection divzero
            re /= 0.0;
            //noinspection divzero
            im /= 0.0;
        }

        return this;
    }

    public MutableComplex sqr() {
        double t = re * re - im * im;
        im *= 2 * re;
        re = t;

        return this;
    }

    public MutableComplex sqrt() {
        double a = abs();
        int sgn = im >= 0 ? 1 : -1;

        im = sgn * Math.sqrt((a - re) / 2);
        re = Math.sqrt((a + re) / 2);

        return this;
    }

    public MutableComplex pow(int n) {
        if (n == 0) {
            re = 1;
            im = 0;
        }
        else if (n < 0) {
            pow(-n).inv();
        }
        else if (n > 1) {
            double r = abs();
            double phi = arg0(r);
            double rn = Math.pow(r, n);
            double phin = phi * n;

            re = rn * Math.cos(phin);
            im = rn * Math.sin(phin);
        }
        // nothing to do for n == 1

        return this;
    }

    public MutableComplex pow(MutableComplex w) {
        MutableComplex lz = clone().log();
        return assign(w.re, w.im).mult(lz).exp();
    }

    public MutableComplex exp() {
        double ex = Math.exp(re);
        return assign(ex * Math.cos(im), ex * Math.sin(im));
    }

    public MutableComplex log() {
        double r = abs();

        // Use this order and *not* assign() here!
        im = arg0(r);
        re = Math.log(r);

        return this;
    }

    public MutableComplex sin() {
        double t = Math.sin(re) * Math.cosh(im);

        // Use this order and *not* assign() here!
        im = Math.cos(re) * Math.sinh(im);
        re = t;

        return this;
    }

    public MutableComplex cos() {
        double t = Math.cos(re) * Math.cosh(im);

        // Use this order and *not* assign() here!
        im = -Math.sin(re) * Math.sinh(im);
        re = t;

        return this;
    }

    public MutableComplex tan() {
        MutableComplex w = clone().cos();

        return sin().div(w);
    }

    public MutableComplex cot() {
        MutableComplex w = clone().sin();

        return cos().div(w);
    }

    public MutableComplex asin() {
        MutableComplex w = clone().sqr().neg();
        w.re += 1;
        w.sqrt().add(imult(1)).log().imult(-1);

        re = w.re;
        im = w.im;

        return this;
    }

    public MutableComplex acos() {
        MutableComplex w = clone().sqr().neg();
        w.re += 1;
        w.sqrt().imult(1).add(this).log().imult(-1);

        re = w.re;
        im = w.im;

        return this;
    }

    public MutableComplex atan() {
        imult(1);
        MutableComplex w1 = MutableComplex.one().add(this);
        MutableComplex w2 = MutableComplex.one().sub(this);

        w1.div(w2).log().imult(-0.5);

        return assign(w1);
    }

    public MutableComplex acot() {
        MutableComplex w = clone().atan();

        return assign(Math.PI / 2, 0).sub(w);
    }

    public MutableComplex sinh() {
        MutableComplex w1 = clone();
        w1.exp();
        MutableComplex w2 = clone().neg();
        w2.exp();
        w1.sub(w2);
        w1.rmult(0.5);

        return assign(w1);
    }

    public MutableComplex cosh() {
        MutableComplex w1 = clone();
        w1.exp();
        MutableComplex w2 = clone().neg();
        w2.exp();
        w1.add(w2);
        w1.rmult(0.5);

        return assign(w1);
    }

    public MutableComplex tanh() {
        MutableComplex w1 = clone().sinh();
        MutableComplex w2 = clone().cosh();
        w1.div(w2);

        return assign(w1);
    }

    public MutableComplex coth() {
        MutableComplex w1 = clone().sinh();
        MutableComplex w2 = clone().cosh();
        w2.div(w1);

        return assign(w2);
    }

    public MutableComplex arsinh() {
        MutableComplex w = clone().sqr().add(MutableComplex.one()).sqrt();
        w.add(this).log();

        return assign(w);
    }

    public MutableComplex arcosh() {
        MutableComplex w1 = clone().add(MutableComplex.one()).sqrt();
        MutableComplex w2 = clone().sub(MutableComplex.one()).sqrt();
        w1.mult(w2);
        w1.add(this).log();

        return assign(w1);
    }

    public MutableComplex artanh() {
        MutableComplex w = clone();
        w.re -= 1.0;
        w.neg();
        re += 1.0;
        div(w).log();

        return rmult(0.5);
    }

    public MutableComplex arcoth() {
        MutableComplex w = clone();
        w.re -= 1.0;
        re += 1.0;
        div(w).log();

        return rmult(0.5);
    }
}
