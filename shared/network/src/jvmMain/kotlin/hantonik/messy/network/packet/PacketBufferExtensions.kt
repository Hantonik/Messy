package hantonik.messy.network.packet

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.readAtMostTo
import kotlinx.io.write

fun PacketBuffer.writeToByteBuf(byteBuf: ByteBuf) {
    val size = this.size.toInt()

    this.internalSource.readAtMostTo(byteBuf.nioBuffer(byteBuf.writerIndex(), size))
    byteBuf.writerIndex(byteBuf.writerIndex() + size)
}

fun PacketBuffer.toByteBuf(): ByteBuf {
    val size = this.size.toInt()
    val byteBuf = Unpooled.buffer(size)

    this.internalSource.readAtMostTo(byteBuf.nioBuffer(byteBuf.writerIndex(), size))
    byteBuf.writerIndex(byteBuf.writerIndex() + size)

    return byteBuf
}

fun ByteBuf.toPacketBuffer(): PacketBuffer {
    val packetBuffer = PacketBuffer()
    packetBuffer.internalSource.write(this.nioBuffer())

    return packetBuffer
}