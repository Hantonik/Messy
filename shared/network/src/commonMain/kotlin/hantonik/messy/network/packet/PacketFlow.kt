package hantonik.messy.network.packet

enum class PacketFlow {
    C2S, S2C;

    fun getOpposite() = if (this == C2S) S2C else C2S
}