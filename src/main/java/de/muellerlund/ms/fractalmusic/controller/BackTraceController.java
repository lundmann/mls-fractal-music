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

package de.muellerlund.ms.fractalmusic.controller;

import de.muellerlund.ms.fractalmusic.calculation.ExtendedComplex;
import de.muellerlund.ms.fractalmusic.util.ImageHelper;
import org.apache.commons.math3.complex.Complex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static de.muellerlund.ms.fractalmusic.fractal.FractalHelper.createImage;

@RestController
public class BackTraceController {

    private static final long NMAX = 10000000L;

    @Value("${mls.locale}")
    private Locale locale;

    @GetMapping(value = "/btm/png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] retrieveAsPng(
            @RequestParam(required = false) Integer imax,
            @RequestParam(required = false) Integer spread) {

        imax = imax == null ? 10 : imax;
        imax = Math.max(2, imax);
        imax = Math.min(12, imax);

        spread = spread == null ? 2 : spread;
        spread = Math.max(2, spread);
        spread = Math.min(10, spread);

        double nmax = Math.pow(spread, imax);
        if (nmax >= NMAX) {
            throw new IllegalArgumentException("To much values to calculate.");
        }

        List<ExtendedComplex> numbers = calculateBackTrace(imax, spread);
        BufferedImage image = createImage(numbers);

        return ImageHelper.asBytes(image, "png");
    }

    private static List<ExtendedComplex> calculateBackTrace(int imax, int spread) {

        List<ExtendedComplex> list = new ArrayList<>();
        ExtendedComplex ec = new ExtendedComplex(0, 0, Complex.ZERO);

        backtrace(list, ec, 0, imax, spread);

        return list;
    }

    private static void backtrace(List<ExtendedComplex> list, ExtendedComplex z, int depth, int imax, int spread) {
        if (depth < imax) {
            list.add(z);

            double phi0 = Math.PI / spread * depth;
            double r0 = Math.pow(0.5, depth);

            for (int k = 0; k < spread; k++) {
                double phi = Math.PI * 2 * k / spread + phi0;
                Complex w = new Complex(r0 * Math.cos(phi), r0 * Math.sin(phi));
                w = z.number().add(w);
                backtrace(list, new ExtendedComplex(k, depth, w), depth + 1, imax, spread);
            }
        }
    }
}
