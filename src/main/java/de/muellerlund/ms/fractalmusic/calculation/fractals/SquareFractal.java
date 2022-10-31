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

package de.muellerlund.ms.fractalmusic.calculation.fractals;

import de.muellerlund.math.complex.MutableComplex;
import de.muellerlund.ms.fractalmusic.calculation.ComplexFractal;

import java.util.ArrayList;
import java.util.List;

public class SquareFractal implements ComplexFractal {
    // Remember: final but mutable!
    private final MutableComplex c;

    public SquareFractal() {
        c = MutableComplex.one();
    }

    public MutableComplex getC() {
        return c;
    }

    @Override
    public int dimensions() {
        return 2;
    }

    @Override
    public List<MutableComplex> preImages(MutableComplex z) {
        List<MutableComplex> numbers = new ArrayList<>();

        MutableComplex w = z.clone().sub(c).sqrt();
        numbers.add(w);
        numbers.add(w.clone().neg());

        return numbers;
    }
}
