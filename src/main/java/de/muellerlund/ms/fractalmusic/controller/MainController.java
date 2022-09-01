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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class MainController {

    @Value("${mls.lostandfound.property}")
    private String property;

    @RequestMapping("/hw/")
    static String home() {
        return "Hello World!";
    }

    @RequestMapping("/hw2")
    String home2(@RequestParam(required = false) String hello) {
        return property + hello;
    }

    @GetMapping(value = "/fractal-music/png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] retrieveAsPng() throws IOException {
        //return new byte[0];
        String resourceKey = "/de/muellerlund/samples/heic0602inv.png";
        try (InputStream is = getClass().getResourceAsStream(resourceKey)) {
            if (is == null) {
                throw new IllegalStateException(resourceKey + " not found.");
            }

            return is.readAllBytes();
        }
    }
}
