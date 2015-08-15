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

import groovy.json.JsonSlurper
import groovyx.net.http.HttpResponseException
import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * @author yukung
 */
class InternetAPISpec extends Specification {

    @IgnoreIf({ env.CI })
    def "should get messages"() {
        given:
        def CLIENT_KEY = System.getenv('CLIENT_KEY')
        def DEVICE_ID = System.getenv('DEVICE_ID')
        def device = new InternetAPI(clientKey: CLIENT_KEY, deviceId: DEVICE_ID)

        when:
        def irData = device.getMessages()

        then:
        irData != null

        and:
        irData.message.format == 'raw'

        and:
        irData.message.freq == 38

        and:
        irData.message.data.class == ArrayList

        and:
        irData.hostname =~ /(?i)irkit/

        and:
        irData.deviceid == DEVICE_ID
    }

    def "should throw HttpResponseException with invalid clientkey or deviceid"() {
        given:
        def device = new InternetAPI(clientKey: 'invalid clientkey', deviceId: 'invalid deviceid')

        when:
        device.getMessages()

        then:
        def e = thrown(HttpResponseException)

        and:
        e.message == 'Unauthorized'
    }

    def "should post messages"() {
        given:
        def CLIENT_KEY = System.getenv('CLIENT_KEY')
        def DEVICE_ID = System.getenv('DEVICE_ID')
        def device = new InternetAPI(clientKey: CLIENT_KEY, deviceId: DEVICE_ID)
        def irData = new JsonSlurper().parse(getClass().getResource('/test.json'))

        when:
        device.postMessages(irData)

        then:
        notThrown(HttpResponseException)
    }
}
