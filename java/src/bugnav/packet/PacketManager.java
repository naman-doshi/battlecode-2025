// CURRENTLY UNUSED

package bugnav.packet;

public class PacketManager {
    final int TYPE_SIZE = 2;
    final int PAYLOAD_SIZE = 32 - TYPE_SIZE;

    void receivePacket(int value) {
        PacketType type = PacketType.values()[value >>> PAYLOAD_SIZE];
        int payload = value & (-1 >>> 2);

        switch (type) {
            case UPDATE_CELL:
                // bruh moment
                break;
        }
    }
}
