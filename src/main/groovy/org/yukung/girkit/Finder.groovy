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

import javax.jmdns.JmDNS

/**
 * @author yukung
 */
class Finder {

    static List<Inet4Address> find() {
        def hosts = []
        def jmdns = JmDNS.create()
        def services = jmdns.list('_irkit._tcp.local.')
        services.each { info ->
            if (info.name =~ /irkit/) {
                info.inet4Addresses.each { hosts << it.hostAddress }
            }
        }
        jmdns.close()
        hosts
    }
}
