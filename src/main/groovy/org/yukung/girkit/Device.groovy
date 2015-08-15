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
import groovyx.net.http.RESTClient

import javax.jmdns.JmDNS

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

/**
 * @author yukung
 */
class Device {
    InetAddress address
    String instanceName

    private url() {
        "http://$address.hostAddress/"
    }

    static List<Device> find() {
        def hosts = []
        def jmdns = JmDNS.create()
        def services = jmdns.list('_irkit._tcp.local.')
        services.each { service ->
            if (service.name =~ /(?i)irkit/) {
                service.inet4Addresses.each { hosts << new Device(address: it, instanceName: service.name) }
            }
        }
        jmdns.close()
        hosts
    }

    def getMessages() {
        def client = new RESTClient(url())
        def res = client.get(path: 'messages', contentType: TEXT)
        res.data.length > 0 ? new JsonSlurper().parse(res.data) : [:]
    }

    def postMessages(data) {
        def client = new RESTClient(url())
        client.post(path: 'messages', contentType: TEXT, body: JsonOutput.toJson(data))
    }

    def getToken() {
        def client = new RESTClient(url())
        def res = client.post(path: 'keys', contentType: TEXT, body: '{}')
        res.status == 200 ? new JsonSlurper().parse(res.data).clienttoken : ''
    }

    def getClientKeyAndDeviceId(clientToken) {
        if (!(clientToken instanceof String)) {
            throw new IllegalArgumentException('token must be String')
        }
        def client = new RESTClient("https://api.getirkit.com/1/")
        def res = client.post(path: 'keys', requestContentType: URLENC, body: [clienttoken: clientToken])
        res.status == 200 ? res.data : [:]
    }
}
