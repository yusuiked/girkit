Groovy-IRKit
====

[![Circle CI](https://circleci.com/gh/yukung/girkit.svg?style=shield&circle-token=a9d95fde08f43bd44a702f447087e8e329d01ddc)](https://circleci.com/gh/yukung/girkit)

[IRKit](http://getirkit.com) client for Groovy, inspired by [shokai/ruby-irkit](https://github.com/shokai/ruby-irkit).

Features
----

* Find IRKit device from within the same LAN.
* Read/Write IR-Data from within the same LAN.
* Acquire the client token from IRKit.
* Read/Write IR-Data from the Internet.

Requirement
----

* Java 8+

Usage
----

See [samples](https://github.com/yukung/girkit/tree/master/src/test/resources/samples)

### Read/Write IR-Data

IRKit has a HTTP API that can be used from within the same LAN.

```groovy
import org.yukung.girkit.Device

// find IRKit with Bonjour
irkit = Device.find().first()

// or, specify with IP address.
//irkit = new Device(address: InetAddress.getByName('192.168.0.10'))
if (!irkit) {
    System.err.println "irkit not found."
    System.exit 1
}

println irkit.dump()

irData = irkit.getMessages()
if (!irData) {
    System.err.println "IR data not found."
    System.exit 1
}

println irData.dump()

println 'rewrite IR data'
irkit.postMessages irData
println irData.dump()
```

### Internet API

To access IRKit from outside of the LAN, use Internet API. it uses `api.getirkit.com` as a proxy.

Get `clientkey` and `deviceid`.

```groovy
import org.yukung.girkit.Device

irkit = Device.find().first()
if (!irkit) {
    System.err.println "irkit not found"
    System.exit 1
}

token = irkit.getToken()
println "token:\t${token}"
res = irkit.getClientKeyAndDeviceId(token)

println "clientkey:\t${res.clientkey}"
println "deviceid:\t${res.deviceid}"
```

#### Read/Write with Internet API

```groovy
CLIENT_KEY = System.getenv('CLIENT_KEY') ?: 'your_client_key'
DEVICE_ID = System.getenv('DEVICE_ID') ?: 'your_device_id'

irkit = new InternetAPI(clientKey: CLIENT_KEY, deviceId: DEVICE_ID)
if (!irkit) {
    System.err.println("device not found.")
    System.exit 1
}

println irkit.dump()

irData = irkit.getMessages()
if (!irData) {
    System.err.println("IR data not found")
    System.exit 1
}

println irData.dump()

println 'rewrite IR data'
irkit.postMessages(irData.message)
irData.dump()
```

Installation
----

TBD

Author
----

[@yukung](https://github.com/yukung)

## License

Licensed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

