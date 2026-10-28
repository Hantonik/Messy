package hantonik.messy.network.exception

class SkipPacketException(override val cause: Throwable) : RuntimeException(cause)