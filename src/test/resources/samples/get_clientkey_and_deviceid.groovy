package samples

import org.yukung.girkit.Device

irkit = Device.find().first()
if (!irkit) {
    System.err.println "irkit not found"
    System.exit 1
}

token = irkit.getToken()
println "token:\t${token}"
res = irkit.getClientKeyAndDeviceId token

println "clientkey:\t${res.clientkey}"
println "deviceid:\t${res.deviceid}"
