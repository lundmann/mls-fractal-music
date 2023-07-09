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

package de.muellerlund.ms.fractalmusic.controller;

import de.muellerlund.math.complex.MutableComplex;
import de.muellerlund.ms.fractalmusic.calculation.Calculator;
import de.muellerlund.ms.fractalmusic.calculation.ComplexFractal;
import de.muellerlund.ms.fractalmusic.calculation.ExtendedComplex;
import de.muellerlund.ms.fractalmusic.fractal.FractalHelper;
import de.muellerlund.ms.fractalmusic.util.ImageHelper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static de.muellerlund.ms.fractalmusic.fractal.FractalHelper.createImage;
import static de.muellerlund.ms.fractalmusic.util.NumberHelper.parseComplex;

@RestController
public class MainController {

    @Value("${mls.locale}")
    private Locale locale;

    @GetMapping(value = "/fractal-music/sample/png/", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] retrieveSamplePng() throws IOException {
        String resourceKey = "/de/muellerlund/samples/heic0602inv.png";
        try (InputStream is = getClass().getResourceAsStream(resourceKey)) {
            if (is == null) {
                throw new IllegalStateException(resourceKey + " not found.");
            }

            return is.readAllBytes();
        }
    }

    @GetMapping(value = "/fractal-music/png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] retrieveAsPng(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer imax,
            @RequestParam(required = false) String z0,
            @RequestParam(required = false) String a5,
            @RequestParam(required = false) String a4,
            @RequestParam(required = false) String a3,
            @RequestParam(required = false) String a2,
            @RequestParam(required = false) String a1,
            @RequestParam(required = false) String a0
    ) {
        List<ExtendedComplex> numbers = apply(type, imax, z0, a5, a4, a3, a2, a1, a0);
        BufferedImage image = createImage(numbers);

        return ImageHelper.asBytes(image, "png");
    }

    private List<ExtendedComplex> apply(
            String type,
            Integer imax,
            String sz0,
            String ... sc
    ) {
        MutableComplex z0 = parseComplex(sz0, MutableComplex.one(), locale);

        int n = sc.length; // as invoked it must be 6
        int deg = n;
        int k = n;

        for (int i = 0; i < n; i++) {
            if (sc[i] != null) {
                deg = n - i - 1;
                k = i;
                break;
            }
        }

        MutableComplex[] coefficients = new MutableComplex[deg + 1];
        for (int i = k; i < n; i++) {
            MutableComplex dv = i == k ? MutableComplex.one() : MutableComplex.zero();
            MutableComplex z = parseComplex(sc[i], dv, locale);
            coefficients[i - k] = z;
        }

        ComplexFractal fractal = FractalHelper.find(type, coefficients);
        imax = imax == null ? 10 : imax;

        return Calculator.calculate(fractal, z0.complex(), imax);
    }
}
