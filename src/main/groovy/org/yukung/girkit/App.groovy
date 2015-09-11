/*
 * Copyright 2015 Yusuke Ikeda
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
 */

package org.yukung.girkit

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.validator.routines.UrlValidator

import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author yukung
 */
@Singleton
class App {

    static final String DATA_FILE = System.getenv('IRKIT_DATA_FILE') ?:
            "${System.getProperty('user.home')}${File.separator}.irkit.json"

    static data

    static {
        if (Files.exists(Paths.get(DATA_FILE)) || new UrlValidator(['http', 'https'] as String[]).isValid(DATA_FILE)) {
            data = new JsonSlurper().parse(
                    DATA_FILE.startsWith('http') ? DATA_FILE.toURL() : Paths.get(DATA_FILE).toUri().toURL()
            )
        } else {
            data = [IR: [:], Device: [:]]
        }
    }

    static save() {
        new File(DATA_FILE).withWriter { writer ->
            writer.write(JsonOutput.toJson(data))
        }
    }
}
