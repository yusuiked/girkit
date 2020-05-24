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

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import wslite.http.HTTPRequest
import wslite.http.HTTPResponse
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

/**
 * @author yukung
 */
class InternetAPISpec extends Specification {

    @Shared
    def messages

    def setupSpec() {
        def builder = new JsonBuilder()
        builder {
            message new JsonSlurper().parse(getClass().getResource('/test.json'))
            hostname 'IRKitD2A4'    // Dummy
            deviceid 'FBEC7F5148274DADB608799D43175FD1' // Dummy
        }
        messages = builder.content
    }

    def "should post messages"() {
        given: 'Mock InternetAPI'
        def client = Mock(RESTClient)
        def mockResponse = new HTTPResponse()
        mockResponse.with {
            statusCode = 200
            statusMessage = 'OK'
        }
        client.post(*_) >> new Response(new HTTPRequest(), mockResponse)
        def irkit = new InternetAPI('clientKey', messages.deviceid, client)

        when:
        def res = irkit.postMessages(messages.message as List)

        then:
        notThrown(IRKitException)

        and:
        res == true
    }

    def "should throw IRKitException with invalid clientkey or deviceid"() {
        given: 'Mock InternetAPI'
        def client = Mock(RESTClient)
        def mockResponse = new HTTPResponse()
        mockResponse.with {
            statusCode = 401
            statusMessage = 'Unauthorized'
        }
        client.post(*_) >> { throw new RESTClientException(mockResponse.statusMessage, new HTTPRequest(), mockResponse) }
        def irkit = new InternetAPI('invalid clientkey', 'invalid deviceid', client)

        when:
        irkit.postMessages(messages.message as List)

        then:
        thrown(IRKitException)
    }

    def "should get messages"() {
        given: 'Mock InternetAPI'
        def client = Mock(RESTClient)
        def mockResponse = new HTTPResponse()
        mockResponse.with {
            statusCode = 200
            statusMessage = 'OK'
            data = JsonOutput.toJson(messages)
        }
        client.get(*_) >> new Response(new HTTPRequest(), mockResponse)
        def irkit = new InternetAPI('clientKey', messages.deviceid, client)

        when:
        def irData = irkit.getMessages()

        then:
        irData != null

        and:
        irData.message.format == 'raw'

        and:
        irData.message.freq == 38

        and:
        irData.message.data.class == ArrayList

        and:
        irData.hostname == messages.hostname

        and:
        irData.deviceid == messages.deviceid
    }

    def "should throw HttpResponseException with invalid clientkey or deviceid"() {
        given: 'Mock InternetAPI'
        def client = Mock(RESTClient)
        def mockResponse = new HTTPResponse()
        mockResponse.with {
            statusCode = 401
            statusMessage = 'Unauthorized'
        }
        client.get(*_) >> { throw new RESTClientException(mockResponse.statusMessage, new HTTPRequest(), mockResponse) }
        def irkit = new InternetAPI('invalid clientkey', 'invalid deviceid', client)

        when:
        irkit.getMessages()

        then:
        thrown(IRKitException)
    }
}
