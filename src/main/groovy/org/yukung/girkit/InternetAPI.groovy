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
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

/**
 * @author yukung
 */
class InternetAPI {
    static final url = "https://api.getirkit.com/1/"
    final String clientKey, deviceId
    final RESTClient client

    InternetAPI(clientKey, deviceId, client) {
        this.clientKey = clientKey
        this.deviceId = deviceId
        this.client = client
    }

    def postMessages(List irData) {
        try {
            client.post(path: 'messages') {
                type ContentType.URLENC
                urlenc deviceid: deviceId, clientkey: clientKey, message: JsonOutput.toJson(irData)
            }
            return true
        } catch (RESTClientException e) {
            throw new IRKitException(e.getMessage(), e)
        }
        return false
    }

    def getMessages(Map query = [:]) {
        query << [clientkey: clientKey]
        try {
            def res = client.get(path: 'messages', query: query)
            res.data.size() > 0 ? new JsonSlurper().parse(res.data) as Map : [:]
        } catch (RESTClientException e) {
            throw new IRKitException(e.getMessage(), e)
        }
    }

    @Override
    String toString() {
        return "<${this.class.name} deviceid=\"${deviceId[0..6]}XXXXX\" clientkey=\"${clientKey[0..6]}XXXXX\">"
    }
}
