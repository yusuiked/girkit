package samples

import org.yukung.girkit.Device

irkit = Device.find().first()
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
