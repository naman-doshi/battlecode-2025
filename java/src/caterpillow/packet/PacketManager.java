package caterpillow.packet;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.TestPacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static caterpillow.Util.*;
import static caterpillow.Game.*;

public class PacketManager {
    final int TYPE_SIZE = 2;
    final int PAYLOAD_SIZE = 32 - TYPE_SIZE;

    public void read(int time) {
        for (Message msg : rc.readMessages(time)) {
            int type = (msg.getBytes() >>> PAYLOAD_SIZE);
            int payload = (msg.getBytes() & (-1 >>> 2));
            switch (type) {
                case 1: // make sure these packet types are synced
                    bot.handleTestPacket(new TestPacket(payload), msg.getSenderID());
                    break;
                case 2:
                    bot.handleAdoptionPacket(new AdoptionPacket(payload), msg.getSenderID());
                    break;
                case 3:
                    break;
                case 4:
                    break;
                default:
                    assert false;
            }
        }
    }

    // fml
    public void send(MapLocation loc, Packet packet) {
        try {
            if (packet instanceof TestPacket) {
                rc.sendMessage(loc, packet.enc() + (1 << PAYLOAD_SIZE)); // 1 is the packet type
            } else if (packet instanceof AdoptionPacket) {
                rc.sendMessage(loc, packet.enc() + (2 << PAYLOAD_SIZE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
