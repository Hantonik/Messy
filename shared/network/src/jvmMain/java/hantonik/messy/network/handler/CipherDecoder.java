package hantonik.messy.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import java.util.List;

@RequiredArgsConstructor
public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Cipher cipher;

    private byte[] heapIn = new byte[0];

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        var i = buffer.readableBytes();

        if (this.heapIn.length < i)
            this.heapIn = new byte[i];

        buffer.readBytes(this.heapIn, 0, i);

        var heapBuffer = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
        var length = this.cipher.update(this.heapIn, 0, i, heapBuffer.array(), heapBuffer.arrayOffset());

        heapBuffer.writerIndex(length);
        out.add(heapBuffer);
    }
}
