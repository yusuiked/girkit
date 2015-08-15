package samples

import org.yukung.girkit.InternetAPI

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
