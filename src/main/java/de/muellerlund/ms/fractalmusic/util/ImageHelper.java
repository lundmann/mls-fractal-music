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

package de.muellerlund.ms.fractalmusic.util;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageHelper {
    private ImageHelper() {
    }

    public static byte[] asBytes(RenderedImage image, String format) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(image.getHeight() * image.getWidth() * 4);
        try {
            ImageIO.write(image, format, stream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }
}
