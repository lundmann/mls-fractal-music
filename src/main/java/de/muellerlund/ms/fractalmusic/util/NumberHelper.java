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

package de.muellerlund.ms.fractalmusic.util;

import de.muellerlund.math.complex.MutableComplex;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
import org.apache.commons.math3.exception.MathParseException;

import java.util.Locale;

public final class NumberHelper {

    private NumberHelper() {
    }

    public static MutableComplex parseComplex(String toParse, MutableComplex defaultValue, Locale locale) {
        if (toParse == null) {
            return defaultValue.clone();
        }

        try {

            ComplexFormat format = ComplexFormat.getInstance(locale);
            Complex z = format.parse(toParse);
            return new MutableComplex(z);
        }
        catch (MathParseException e) {
            return defaultValue.clone();
        }
    }
}
