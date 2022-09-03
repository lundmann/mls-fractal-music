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

package de.muellerlund.ms.fractalmusic.fractal;

import de.muellerlund.ms.fractalmusic.calculation.ExtendedComplex;
import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public final class FractalHelper {

    private FractalHelper() {
    }

    public static BufferedImage createImage(List<ExtendedComplex> numbers) {
        Rectangle2D.Double r = getBounds(numbers);
        double ratio = r.width / r.height;

        double x1 = r.x;
        double x2 = x1 + r.width;
        double y1 = r.y;
        double y2 = y1 + r.height;

        int width = 800;
        int height = (int) (width / ratio);

        if (height <= 0) {
            height = 100;
        }

        double mx = width / (x2 - x1);
        double bx = -mx * x1;
        double my = height / (y1 - y2);
        double by = -my * y2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        for (ExtendedComplex ez : numbers) {
            Complex z = ez.number();
            Color color = new Color(-ez.depth() * 17 & 255, -ez.depth() * 9 - ez.id() * 23 & 255, -ez.id() * 47 & 255);
            g.setColor(color);
            int x = (int) (mx * z.getReal() + bx);
            int y = (int) (my * z.getImaginary() + by);
            g.drawRect(x, y, 1, 1);
        }

        return image;
    }

    private static Rectangle2D.Double getBounds(List<ExtendedComplex> numbers) {
        double rMin = 0.0;
        double rMax = 0.0;
        double iMin = 0.0;
        double iMax = 0.0;

        for (ExtendedComplex ez : numbers) {
            Complex z = ez.number();
            rMin = Math.min(rMin, z.getReal());
            rMax = Math.max(rMax, z.getReal());
            iMin = Math.min(iMin, z.getImaginary());
            iMax = Math.max(iMax, z.getImaginary());
        }

        return new Rectangle2D.Double(rMin, iMin, rMax - rMin, iMax - iMin);
    }
}
