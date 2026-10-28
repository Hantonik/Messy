//package hantonik.messy.network.handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.ByteToMessageDecoder;
//
//import java.util.List;
//
//public class VarInt21FrameDecoder extends ByteToMessageDecoder {
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
//        buffer.markReaderIndex();
//
//        var byteArray = new byte[3];
//
//        for (var i = 0; i < byteArray.length; i++) {
//            if (!buffer.isReadable()) {
//                buffer.resetReaderIndex();
//
//                return;
//            }
//
//            byteArray[i] = buffer.readByte();
//
//            if (byteArray[i] >= 0) {
//                var packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(byteArray));
//
//                try {
//                    var j = packetBuffer.readVarInt();
//
//                    if (buffer.readableBytes() >= j) {
//                        out.add(buffer.readBytes(j));
//
//                        return;
//                    }
//
//                    buffer.resetReaderIndex();
//                } finally {
//                    packetBuffer.release();
//                }
//
//                return;
//            }
//        }
//
//        throw new CorruptedFrameException("Length wider than 21-bit");
//    }
//}
