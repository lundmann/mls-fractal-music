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

package de.muellerlund.ms.fractalmusic.calculation;

import de.muellerlund.math.complex.MutableComplex;

import org.apache.commons.math3.complex.Complex;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class Calculator {

    private final static BigInteger MAX_NUMBERS = BigInteger.valueOf(1 << 20);

    private Calculator() {
    }

    public static List<ExtendedComplex> calculate(ComplexFractal fractal, Complex z0, int maxDepth) {
        if (maxDepth < 1) {
            throw new IllegalArgumentException("Maximal recursion depth should be at least 1.");
        }

        BigInteger n = BigInteger.valueOf(fractal.dimensions());
        BigInteger nd = n.pow(maxDepth);

        if (nd.compareTo(MAX_NUMBERS) > 0) {
            throw new IllegalArgumentException("Number of needed calculations (" + nd + ") exceeds " + MAX_NUMBERS + ".");
        }

        List<ExtendedComplex> list = new ArrayList<>();
        calculate(list, fractal, new MutableComplex(z0), 0, maxDepth);
        return list;
    }

    private static void calculate(List<ExtendedComplex> list, ComplexFractal fractal, MutableComplex z0, int i, int maxDepth) {
        if (maxDepth <= 0) {
            return;
        }

        list.add(new ExtendedComplex(i, maxDepth, z0.complex()));

        int j = 0;
        for (MutableComplex w : fractal.preImages(z0)) {
            calculate(list, fractal, w, j++, maxDepth - 1);
        }
    }
}
