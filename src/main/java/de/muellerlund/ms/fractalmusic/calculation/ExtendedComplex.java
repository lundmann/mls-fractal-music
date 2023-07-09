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

package de.muellerlund.ms.fractalmusic.calculation;

import org.apache.commons.math3.complex.Complex;

import java.util.Objects;

public record ExtendedComplex(int id, int depth, Complex number) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtendedComplex that = (ExtendedComplex) o;
        return id == that.id && depth == that.depth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, depth);
    }

    @Override
    public String toString() {
        return "ExtendedComplex{" +
               "id=" + id +
               ", depth=" + depth +
               ", number=" + number +
               '}';
    }
}
