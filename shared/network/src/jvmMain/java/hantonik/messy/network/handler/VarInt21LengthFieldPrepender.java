//package hantonik.messy.network.handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToByteEncoder;
//
//public class VarInt21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
//    @Override
//    protected void encode(ChannelHandlerContext ctx, ByteBuf buffer, ByteBuf out) {
//        var i = buffer.readableBytes();
//        var j = PacketBuffer.getVarIntSize(i);
//
//        if (j > 3)
//            throw new IllegalArgumentException("Unable to fit " + i + " into 3");
//        else {
//            var packetBuffer = new PacketBuffer(out);
//
//            packetBuffer.ensureWritable(i + j);
//            packetBuffer.writeVarInt(i);
//            packetBuffer.writeBytes(buffer, buffer.readerIndex(), i);
//        }
//    }
//}
