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

import org.assertj.core.api.Fail;
import org.assertj.core.data.Offset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

final class ComplexTestUtil {
    private static final Offset<Double> STD_OFFSET = offset(1e-10);

    private ComplexTestUtil() {
    }

    static void assertCloseTo(MutableComplex z, MutableComplex w, Offset<Double> eps) {
        assertThat(z.real()).isCloseTo(w.real(), eps);
        assertThat(z.imag()).isCloseTo(w.imag(), eps);
    }

    static void assertCloseTo(MutableComplex z, MutableComplex w) {
        assertThat(z.real()).isCloseTo(w.real(), STD_OFFSET);
        assertThat(z.imag()).isCloseTo(w.imag(), STD_OFFSET);
    }

    static void assertContains(Iterable<MutableComplex> container, Offset<Double> offset, MutableComplex ... numbers) {
        assertContains(container, offset, Arrays.asList(numbers));
    }

    static void assertContains(Iterable<MutableComplex> container, Offset<Double> offset, Iterable<MutableComplex> numbers) {
        double r0 = offset.value;
        r0 = r0 * r0;

        for (MutableComplex z : numbers) {
            boolean contained = false;
            for (MutableComplex w: container) {
                MutableComplex r = w.clone().sub(z);
                if (r.norm() < r0) {
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                Fail.fail("Complex number " + z + " not contained.");
            }
        }
    }

    static List<MutableComplex> someNumbers() {
        List<MutableComplex> list = new ArrayList<>();

        list.add(new MutableComplex(1.2, 0.4));
        list.add(new MutableComplex(0.7, -1.6));
        list.add(new MutableComplex(2.35, 7.02));
        list.add(new MutableComplex(-0.008, -1.002));
        list.add(new MutableComplex(-45.008, -100.002));

        return list;
    }

    static List<MutableComplex> someSmallNumbers() {
        List<MutableComplex> list = new ArrayList<>();

        list.add(new MutableComplex(1.2, 0.4));
        list.add(new MutableComplex(0.7, -1.6));
        list.add(new MutableComplex(2.3, 7));
        list.add(new MutableComplex(6.3, -5));
        list.add(new MutableComplex(-0.8, -1.25));

        return list;
    }
}
