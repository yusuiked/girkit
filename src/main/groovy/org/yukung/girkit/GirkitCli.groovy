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

cli = new CliBuilder(usage: 'girkit [option] <command>', header: 'options:', footer: """
e.g.
 \$ girkit --get tv_on
 \$ girkit --post tv_on
 \$ girkit --post tv_on --address 192.168.0.123
 \$ girkit --show tv_on
 \$ girkit --delete tv_on
 \$ girkit --rename tv_on,newname
 \$ girkit --device:add myhouse
 \$ girkit --post tv_on --device myhouse
 \$ girkit --device:delete myhouse
""")

cli.with {
    g longOpt: 'get', args: 1, argName: 'command', 'get IR Data'
    p longOpt: 'post', args: 1, argName: 'command', 'post IR Data'
    d longOpt: 'delete', args: 1, argName: 'command', 'delete IR Data'
    _ longOpt: 'rename', args: 2, argName: 'target,NEWNAME', valueSeparator: ',' as char, 'rename IR Data'
    s longOpt: 'show', args: 1, argName: 'command', 'print IR Data'
    l longOpt: 'list', 'show list of IR Data and Devices'
    a longOpt: 'address', args: 1, argName: 'IP address', 'IRKit IP Address'
    _ longOpt: 'device', args: 1, argName: 'device', 'use Internet API'
    _ longOpt: 'device:add', args: 1, argName: 'device', 'save clientkey and deviceid for Internet API'
    _ longOpt: 'device:delete', args: 1, argName: 'device', 'delete clientkey and deviceid'
    _ longOpt: 'device:show', args: 1, argName: 'device', 'print clientkey and deviceid'
    v longOpt: 'version', 'show version'
    h longOpt: 'help', 'show help'
}

options = cli.parse args
if (!options) System.exit 1

if (options.v) {
    def prop = new Properties()
    getClass().getResourceAsStream('/build-receipt.properties').withStream { input -> prop.load(input) }
    println "IRKit Client for Groovy v${prop.version}"
    System.exit 0
}

if (options.h ||
        (!options.g && !options.p && !options.l && !options.d
                && !options.'rename' && !options.s
                && !options.'device' && !options.'device:add'
                && !options.'device:show' && !options.'device:delete')) {
    cli.usage()
    System.exit 0
}

if (options.l) {
    println "~> ${App.DATA_FILE}"
    println '== Data'
    App.data['IR'].each { k, v ->
        println k
    }
    println '== Devices'
    App.data['Device'].each { k, v ->
        println "${k}\tInternet API"
    }
    Device.find().each { device ->
        println "${device.address.hostAddress}\t${device.instanceName} (bonjour)"
    }
    System.exit 0
}

if (options.s) {
    name = options.s
    println JsonOutput.toJson(App.data['IR']."$name")
    System.exit 0
}

if (options.d) {
    name = options.d
    print "delete IR-Data \"${name}\"? [Y/n] > "
    if (new Scanner(System.in).next().trim().toLowerCase() ==~ /n/) System.exit 1
    App.data['IR'].remove name
    App.save()
    println "\"${name}\" delete!"
    System.exit 0
}

if (options.'rename') {
    name = options.'renames'[0]
    newname = options.'renames'[1]
    if (!App.data['IR'].containsKey(name)) {
        System.err.println "IR Data \"${name}\" not found"
        System.exit 1
    }
    print "rename IR-Data \"${name}\" to \"${newname}?\" [Y/n] > "
    if (new Scanner(System.in).next().trim().toLowerCase() ==~ /n/) System.exit 1
    App.data['IR'][newname] = App.data['IR'][name]
    if (name != newname) App.data['IR'].remove name
    App.save()
    println "\"${name}\" to \"${newname}\" rename!"
    System.exit 0
}

if (options.'device:show') {
    name = options.'device:show'
    println JsonOutput.toJson(App.data['Device'][name])
    System.exit 0
}

if (options.'device:delete') {
    name = options.'device:delete'
    print "delete Device \"${name}\"? [Y/n] > "
    if (new Scanner(System.in).next().trim().toLowerCase() ==~ /n/) System.exit 1
    App.data['Device'].remove name
    App.save()
    println "\"${name}\" delete!"
    System.exit 0
}

if (options.a) {
    irkit = new Device(address: InetAddress.getByName(options.a))
} else if (options.'device') {
    info = App.data['Device'][options.'device']
    if (!info) {
        System.err.println "Device \"${options.'device'}\" not found"
        System.exit 1
    }
    irkit = new InternetAPI(clientKey: info.clientkey, deviceId: info.deviceid)
} else {
    println 'finding IRKit with bonjour...'
    irkit = Device.find().first()
}

if (!irkit) {
    System.err.println 'IRKit not found'
    System.exit 1
}

println "using ${irkit}"

if (options.g) {
    name = options.g
    if (App.data['IR'].containsKey(name)) {
        print "overwrite \"${name}\"? [Y/n] > "
        if (new Scanner(System.in).next().trim().toLowerCase() ==~ /n/) System.exit 1
    }
    res = irkit.getMessages()
    if (!res) {
        System.err.println 'IR Data not found'
        System.exit 1
    }
    if (irkit instanceof InternetAPI) {
        irData = res.message
    } else {
        irData = res
    }
    println JsonOutput.toJson(irData)
    App.data['IR'][name] = irData
    App.save()
    println "\"${name}\" saved!"
    System.exit 0
}

if (options.p) {
    name = options.p
    println "post \"${name}\""
    irData = App.data['IR'][name]
    if (!irData) {
        System.err.println "IR Data \"${name}\" not found"
        System.exit 1
    }
    try {
        res = irkit.postMessages irData
        if (res.statusCode == 200) {
            println 'success!'
        } else {
            println "unsuccessful, HTTP Status: ${res.statusCode}"
        }
    } catch (e) {
        System.err.println "${e.response.statusCode} ${e.response.statusMessage} ${e.message}"
    }
}

if (options.'device:add') {
    name = options.'device:add'
    if (App.data['Device'].containsKey(name)) {
        print "overwrite \"${name}\" [Y/n] > "
        if (new Scanner(System.in).next().trim().toLowerCase() ==~ /n/) System.exit 1
    }
    token = irkit.getToken()
    info = irkit.getClientKeyAndDeviceId(token)

    println "clientkey:\t${info.clientkey[0..6]}XXXXXX"
    println "deviceid:\t${info.deviceid[0..6]}XXXXXX"
    App.data['Device'][name] = info
    App.save()
    println "\"${name}\" saved!"
    System.exit 0
}
