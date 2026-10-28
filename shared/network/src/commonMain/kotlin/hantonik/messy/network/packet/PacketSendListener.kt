package hantonik.messy.network.packet

import hantonik.messy.network.api.packet.Packet
import kotlinx.coroutines.Runnable
import kotlin.jvm.JvmStatic

interface PacketSendListener {
    companion object {
        @JvmStatic
        fun thanRun(action: Runnable) = object : PacketSendListener {
            override fun onSuccess() = action.run()

            override fun onFailure(): Packet<*>? {
                action.run()

                return null
            }
        }
    }

    fun onSuccess() {

    }

    fun onFailure(): Packet<*>? = null
}