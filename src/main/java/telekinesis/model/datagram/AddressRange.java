package telekinesis.model.datagram;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.net.InetAddress;

@JsonSerialize(using = AddressRange.Serializer.class)
public class AddressRange {
    private final InetAddress from;
    private final InetAddress to;
    private final int fromInt;
    private final int toInt;

    public AddressRange(String jsonValue) throws IOException {
        String[] addresses = jsonValue.split("-");
        String[] cidr = jsonValue.split("/");

        if (addresses.length == 2) {
            from = InetAddress.getByName(addresses[0]);
            to = InetAddress.getByName(addresses[1]);
        } else if (cidr.length == 2) {
            String base = cidr[0].substring(0, cidr[0].lastIndexOf(".")+1);
            int bits = Integer.valueOf(cidr[1]);

            int ips = 0;
            for (int n = 0; n < (32 - bits); n++) {
                ips = (ips << 1) | 1;
            }

            from = InetAddress.getByName(cidr[0]);
            to = InetAddress.getByName(base+String.valueOf(ips));
        } else {
            throw new IOException("expected 2 addresses");
        }

        fromInt = Unpooled.wrappedBuffer(from.getAddress()).readInt();
        toInt = Unpooled.wrappedBuffer(to.getAddress()).readInt();
    }

    public InetAddress getFrom() {
        return from;
    }

    public InetAddress getTo() {
        return to;
    }

    public boolean containsAddress(int ip) {
        return fromInt <= ip && toInt >= ip;
    }

    public static class Serializer extends StdSerializer<AddressRange> {

        public Serializer() {
            super((Class) null);
        }

        @Override
        public void serialize(AddressRange addressRange, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(String.format("%s-%s", addressRange.getFrom().getHostAddress(), addressRange.getTo().getHostAddress()));
        }
    }

}
