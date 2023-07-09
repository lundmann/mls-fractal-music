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

public class Zero {
    private final MutableComplex value;
    private int quantity = 1;

    public Zero(MutableComplex value, int quantity) {
        this.value = value.clone();
        this.quantity = quantity;
    }

    public Zero(MutableComplex value) {
        this(value, 1);
    }

    public MutableComplex value() {
        return value;
    }

    public int quantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Zero{" +
               "value=" + value +
               ", quantity=" + quantity +
               '}';
    }
}
