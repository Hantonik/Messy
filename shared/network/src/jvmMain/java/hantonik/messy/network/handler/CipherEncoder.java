package hantonik.messy.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;

@RequiredArgsConstructor
public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {
    private final Cipher cipher;

    private byte[] heapIn = new byte[0];
    private byte[] heapOut = new byte[0];

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buffer, ByteBuf out) throws Exception {
        var i = buffer.readableBytes();
        var j = this.cipher.getOutputSize(i);

        if (this.heapIn.length < i)
            this.heapIn = new byte[i];

        buffer.readBytes(this.heapIn, 0, i);

        if (this.heapOut.length < j)
            this.heapOut = new byte[j];

        var length = this.cipher.update(this.heapIn, 0, i, this.heapOut);
        out.writeBytes(this.heapOut, 0, length);
    }
}
