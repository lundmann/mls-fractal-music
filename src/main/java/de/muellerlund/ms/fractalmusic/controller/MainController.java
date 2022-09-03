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

import de.muellerlund.math.complex.MutableComplex;
import de.muellerlund.ms.fractalmusic.calculation.Calculator;
import de.muellerlund.ms.fractalmusic.calculation.ExtendedComplex;
import de.muellerlund.ms.fractalmusic.calculation.fractals.SquareFractal;
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
            @RequestParam(required = false) Integer n,
            @RequestParam(required = false) String c0,
            @RequestParam(required = false) String z0
    ) {
        SquareFractal fractal = new SquareFractal();
        MutableComplex v0 = parseComplex(c0, MutableComplex.i(), locale);
        fractal.getC().assign(v0);
        MutableComplex w0 = parseComplex(z0, MutableComplex.one(), locale);
        n = n == null ? 10 : n;

        System.out.println(v0);
        System.out.println(w0);

        List<ExtendedComplex> numbers = Calculator.calculate(fractal, w0.complex(), n);
        BufferedImage image = createImage(numbers);

        return ImageHelper.asBytes(image, "png");
    }
}
