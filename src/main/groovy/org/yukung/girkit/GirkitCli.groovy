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
 \$ girkit --device:add myhouse
 \$ girkit --post tv_on --device myhouse
 \$ girkit --device:delete myhouse
""")

cli.with {
    s longOpt: 'show', args: 1, argName: 'command', 'print IR Data'
    l longOpt: 'list', 'show list of IR Data and Devices'
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
        (!options.l && !options.s)) {
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
        println "${device.address}\t${device.instanceName} (bonjour)"
    }
    System.exit 0
}

if (options.s) {
    def name = options.s
    println JsonOutput.toJson(App.data['IR']."$name")
    System.exit 0
}
