package org.yukung.girkit

class IRKitException extends RuntimeException {
    IRKitException(String message) {
        super(message)
    }

    IRKitException(Throwable cause) {
        super(cause)
    }

    IRKitException(String message, Throwable cause) {
        super(message, cause)
    }
}
