package hantonik.messy.server.core.data.entity.mappers

import hantonik.messy.data.device.DeviceInfo
import hantonik.messy.server.core.data.entity.device.DeviceEntity

fun DeviceEntity.toDeviceInfo(): DeviceInfo {
    if (this.macAddress == null)
        throw IllegalStateException("Cannot access data before database read")

    return DeviceInfo(this.macAddress, this.name, this.os, this.deviceType)
}