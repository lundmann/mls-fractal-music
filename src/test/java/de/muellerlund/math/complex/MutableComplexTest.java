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

import org.apache.commons.math3.complex.Complex;
import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class MutableComplexTest {
    private static final Offset<Double> STD_OFFSET = offset(1e-10);
    private static final Offset<Double> APPROX_OFFSET = offset(1e-5);

    private static void assertCloseToLog(MutableComplex z, MutableComplex w, Offset<Double> eps) {
        assertThat(z.real()).isCloseTo(w.real(), eps);
        assertThat(argFraction(z.imag())).isCloseTo(argFraction(w.imag()), eps);
    }

    private static void assertCloseTo(MutableComplex z, MutableComplex w) {
        assertThat(z.real()).isCloseTo(w.real(), STD_OFFSET);
        assertThat(z.imag()).isCloseTo(w.imag(), STD_OFFSET);
    }

    private static void assertCloseTo(MutableComplex z, MutableComplex w, Offset<Double> eps) {
        assertThat(z.real()).isCloseTo(w.real(), eps);
        assertThat(z.imag()).isCloseTo(w.imag(), eps);
    }

    private static void assertCloseTo(MutableComplex z, Complex w) {
        assertCloseTo(z, new MutableComplex(w.getReal(), w.getImaginary()));
    }

    private static void assertCloseTo(MutableComplex z, Complex w, Offset<Double> eps) {
        assertCloseTo(z, new MutableComplex(w.getReal(), w.getImaginary()), eps);
    }

    private static double argFraction(double y) {
        double x = y / (2.0 * Math.PI);
        x -= Math.floor(x);
        return x * 2.0 * Math.PI;
    }

    private static List<MutableComplex> someNumbers() {
        List<MutableComplex> list = new ArrayList<>();

        list.add(new MutableComplex(1.2, 0.4));
        list.add(new MutableComplex(0.7, -1.6));
        list.add(new MutableComplex(2.35, 7.02));
        list.add(new MutableComplex(-0.008, -1.002));
        list.add(new MutableComplex(-45.008, -100.002));

        return list;
    }

    private static List<MutableComplex> someSmallNumbers() {
        List<MutableComplex> list = new ArrayList<>();

        list.add(new MutableComplex(1.2, 0.4));
        list.add(new MutableComplex(0.7, -1.6));
        list.add(new MutableComplex(2.3, 7));
        list.add(new MutableComplex(6.3, -5));
        list.add(new MutableComplex(-0.8, -1.25));

        return list;
    }

    @Test
    public void testConstruction() {
        MutableComplex z = MutableComplex.zero();
        assertThat(z.real()).isCloseTo(0.0, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(0.0, STD_OFFSET);

        z = MutableComplex.one();
        assertThat(z.real()).isCloseTo(1.0, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(0.0, STD_OFFSET);

        z = MutableComplex.i();
        assertThat(z.real()).isCloseTo(0.0, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(1.0, STD_OFFSET);

        z = new MutableComplex(2.718);
        assertThat(z.real()).isCloseTo(2.718, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(0.0, STD_OFFSET);

        z = new MutableComplex(0.0, -2.718);
        assertThat(z.real()).isCloseTo(0.0, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(-2.718, STD_OFFSET);

        z = new MutableComplex(77.7, -2.718);
        assertThat(z.real()).isCloseTo(77.7, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(-2.718, STD_OFFSET);
        assertThat(z.complex().getReal()).isCloseTo(77.7, STD_OFFSET);
        assertThat(z.complex().getImaginary()).isCloseTo(-2.718, STD_OFFSET);

        z = new MutableComplex(new Complex(77.7, -2.718));
        assertThat(z.real()).isCloseTo(77.7, STD_OFFSET);
        assertThat(z.imag()).isCloseTo(-2.718, STD_OFFSET);
    }

    @Test
    public void testAssignment() {
        MutableComplex z = new MutableComplex(77.7, -2.718);
        MutableComplex w = z.assign(4.7, 2.5);
        assertCloseTo(z, new MutableComplex(4.7, 2.5));
        assertCloseTo(w, z);

        z = new MutableComplex();
        w = z.assign(new MutableComplex(-0.7, -1.3));
        assertCloseTo(z, new MutableComplex(-0.7, -1.3));
        assertCloseTo(w, z);

        z.assign(new MutableComplex(0.7, 1.3));
        w = z.clone();
        assertCloseTo(w, z);
    }

    @Test
    public void testInvolutions() {
        MutableComplex z = MutableComplex.zero();
        z.neg();
        assertCloseTo(z, MutableComplex.zero());

        z.assign(-3, 4);
        z.neg();
        assertCloseTo(z, new MutableComplex(3, -4));

        z.conj();
        assertCloseTo(z, new MutableComplex(3, 4));

        z.inv();
        Complex a = new Complex(3, 4).reciprocal();
        assertCloseTo(z, a);
        assertCloseTo(z, new MutableComplex(0.12, -0.16));
        z.inv();
        assertCloseTo(z, new Complex(3, 4));

        // same as operation chain
        z.assign(-3, 4).neg().conj().inv().inv();
        assertCloseTo(z, new Complex(3, 4));
    }

    @Test
    public void testPolarArgument() {
        MutableComplex z = MutableComplex.zero();
        assertThat(z.arg()).isCloseTo(0, STD_OFFSET);

        final int N = 36;

        for (int i = -N / 2; i < N / 2; i++) {
            double phi = 2 * Math.PI * i / N;
            z = MutableComplex.polar(1, phi);
            assertThat(z.arg()).isCloseTo(phi, STD_OFFSET);
            assertThat(new Complex(z.real(), z.imag()).getArgument()).isCloseTo(phi, STD_OFFSET);

            double[] v = z.polar();
            assertThat(v[0]).isCloseTo(1, STD_OFFSET);
            assertThat(v[1]).isCloseTo(phi, STD_OFFSET);
        }
    }

    @Test
    public void testAddition() {
        MutableComplex z = MutableComplex.one().add(MutableComplex.i());
        assertCloseTo(z, new MutableComplex(1, 1));
        z.sub(new MutableComplex(2, 4)).add(new MutableComplex(0, 2));
        assertCloseTo(z, new MutableComplex(-1, -1));
    }

    @Test
    public void testZeroMultiplication() {
        for (MutableComplex z : someNumbers()) {
            MutableComplex w = z.clone().rmult(0);
            assertCloseTo(w, MutableComplex.zero());
            w = z.clone().imult(0);
            assertCloseTo(w, MutableComplex.zero());
            w = z.clone().mult(MutableComplex.zero());
            assertCloseTo(w, MutableComplex.zero());
        }
    }

    @Test
    public void testMultiplication() {
        for (MutableComplex z : someNumbers()) {
            for (MutableComplex w : someNumbers()) {
                MutableComplex u = z.clone().mult(w);
                Complex v = new Complex(z.real(), z.imag()).multiply(new Complex(w.real(), w.imag()));
                assertCloseTo(u, v);

                u = z.clone().div(w);
                v = new Complex(z.real(), z.imag()).divide(new Complex(w.real(), w.imag()));
                assertCloseTo(u, v);
            }
        }
    }

    @Test
    public void testSquareAndRoot() {
        for (MutableComplex z : someNumbers()) {
            MutableComplex w = z.clone().sqr();
            Complex u = new Complex(z.real(), z.imag());
            u = u.multiply(u);
            assertCloseTo(w, u);

            w = z.clone().sqrt();
            u = new Complex(z.real(), z.imag()).sqrt();
            assertCloseTo(w, u);
        }
    }

    @Test
    public void testPowerByZero() {
        for (MutableComplex z : someNumbers()) {
            MutableComplex w = z.clone().pow(0);
            assertCloseTo(w, MutableComplex.one());
        }
    }

    @Test
    public void testPowerByInteger() {
        for (MutableComplex z : someNumbers()) {
            for (int p = -3; p <= 3; p++) {
                MutableComplex w = z.clone().pow(p);
                Complex u = new Complex(z.real(), z.imag());
                u = u.pow(p);
                assertCloseTo(w, u, APPROX_OFFSET);
            }
        }
    }

    @Test
    public void testPowerByComplex() {
        for (MutableComplex z : someSmallNumbers()) {
            for (MutableComplex w : someSmallNumbers()) {
                MutableComplex u = z.clone().pow(w);
                Complex v = new Complex(z.real(), z.imag());
                v = v.pow(new Complex(w.real(), w.imag()));
                assertCloseTo(u, v, APPROX_OFFSET);
            }
        }
    }

    @Test
    public void testExpAndLog() {
        for (MutableComplex z : someSmallNumbers()) {
            MutableComplex u = z.clone().exp();
            Complex v = new Complex(z.real(), z.imag()).exp();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().log();
            v = new Complex(z.real(), z.imag()).log();
            assertCloseTo(u, v, APPROX_OFFSET);
        }
    }

    @Test
    public void testTrigonometricFunctions() {
        for (MutableComplex z : someSmallNumbers()) {
            MutableComplex u = z.clone().sin();
            Complex v = new Complex(z.real(), z.imag()).sin();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().cos();
            v = new Complex(z.real(), z.imag()).cos();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().tan();
            v = new Complex(z.real(), z.imag()).tan();
            assertCloseTo(u, v, APPROX_OFFSET);
        }
    }

    @Test
    public void testHyperbolicFunctions() {
        for (MutableComplex z : someSmallNumbers()) {
            MutableComplex u = z.clone().sinh();
            Complex v = new Complex(z.real(), z.imag()).sinh();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().cosh();
            v = new Complex(z.real(), z.imag()).cosh();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().tanh();
            v = new Complex(z.real(), z.imag()).tanh();
            assertCloseTo(u, v, APPROX_OFFSET);
        }
    }

    @Test
    public void testInverseTrigonometricFunctions() {
        for (MutableComplex z : someSmallNumbers()) {
            MutableComplex u = z.clone().asin();
            Complex v = new Complex(z.real(), z.imag()).asin();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().acos();
            v = new Complex(z.real(), z.imag()).acos();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().atan();
            v = new Complex(z.real(), z.imag()).atan();
            assertCloseTo(u, v, APPROX_OFFSET);

            u = z.clone().acot().cot();
            assertCloseToLog(u, z, APPROX_OFFSET);
        }
    }

    @Test
    public void testAreaFunctions() {
        for (MutableComplex z : someSmallNumbers()) {
            MutableComplex u = z.clone().arsinh().sinh();
            assertCloseToLog(u, z, APPROX_OFFSET);

            u = z.clone().arcosh().cosh();
            assertCloseToLog(u, z, APPROX_OFFSET);

            u = z.clone().artanh().tanh();
            assertCloseToLog(u, z, APPROX_OFFSET);

            u = z.clone().arcoth().coth();
            assertCloseToLog(u, z, APPROX_OFFSET);
        }
    }
}
