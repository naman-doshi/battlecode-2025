package caterpillow.packet;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;
import caterpillow.packet.packets.AdoptionPacket;
import caterpillow.packet.packets.OriginPacket;
import caterpillow.packet.packets.SeedPacket;
import caterpillow.packet.packets.StrategyPacket;
import caterpillow.util.Pair;

import java.util.*;

import static caterpillow.Game.*;
import static caterpillow.util.Util.*;

public class PacketManager {
    public static final int TYPE_SIZE = 4;
    public static final int PAYLOAD_SIZE = 32 - TYPE_SIZE;
    public static final int MAX_PAYLOAD = 1 << PAYLOAD_SIZE;
    int prev_read = 0;

    // no priorities, can do later if needed
    // hopefully this doesn't get clogged
    LinkedList<Pair<Integer, Packet>> queue;

    public PacketManager() {
        queue = new LinkedList<>();
    }

    public void processMessage(Message msg) {
        int type = (msg.getBytes() >>> PAYLOAD_SIZE);
        int payload = (msg.getBytes() & (-1 >>> TYPE_SIZE));
        int sender = msg.getSenderID();
        switch (type) {
            case 0:
                AdoptionPacket adoptionPacket = new AdoptionPacket();
                bot.handleAdoptionPacket(adoptionPacket, sender);
            case 1: // make sure these packet types are synced
                OriginPacket originPacket = new OriginPacket(decodeLoc(payload));
                bot.handleOriginPacket(originPacket, sender);
                break;
            case 2:
                SeedPacket seedPacket = new SeedPacket(payload);
                bot.handleSeedPacket(seedPacket, sender);
                break;
            case 3:
                int id = payload >>> StrategyPacket.STRATEGY_DATA_SIZE;
                int data = payload & (-1 >>> StrategyPacket.STRATEGY_ID_SIZE);
                StrategyPacket strategyPacket = new StrategyPacket(id, data);
                bot.handleStrategyPacket(strategyPacket, sender);
                break;
            case 4:
                break;
            default:
                assert false;
        }
    }

    public void read() {
        Message[] prev = rc.readMessages(time - 1);
        for (int i = prev_read; i < prev.length; i++) {
            processMessage(prev[i]);
            println(prev[i].getBytes() >>> PAYLOAD_SIZE);
        }
        Message[] cur = rc.readMessages(time);
        prev_read = cur.length;
        for (Message msg : cur) {
            processMessage(msg);
            println(msg.getBytes() >>> PAYLOAD_SIZE);
        }
    }

    // fml
    public void send(int botID, Packet packet) {
        queue.add(new Pair(botID, packet));
    }

    public void send(MapLocation loc, Packet packet) throws GameActionException {
        send(rc.senseRobotAtLocation(loc).getID(), packet);
    }

    private void processPacket(MapLocation loc, Packet packet) throws GameActionException {
        int payload;
        int type;
        if (packet instanceof AdoptionPacket adoptionPacket) {
            type = 0;
            payload = 0;
        } else if (packet instanceof OriginPacket originPacket) {
            type = 1;
            payload = encodeLoc(originPacket.loc);
        } else if (packet instanceof SeedPacket seedPacket) {
            type = 2;
            payload = seedPacket.seed;
        } else if (packet instanceof StrategyPacket strategyPacket) {
            type = 3;
            payload = strategyPacket.strategyData + (strategyPacket.strategyID << StrategyPacket.STRATEGY_DATA_SIZE);
        } else {
            System.out.println("wtf is this packet");
            return;
        }
        assert payload >= 0: "payload is negative";
        assert payload < MAX_PAYLOAD : "payload too large";
        rc.sendMessage(loc, payload + (type << PAYLOAD_SIZE));
    }

    public void flush() throws GameActionException {
        // rip packets
        while (queue.size() > 100) {
            queue.pop();
        }

        Iterator<Pair<Integer, Packet>> it = queue.iterator();
        while (it.hasNext()) {
            Pair<Integer, Packet> el = it.next();
            for (RobotInfo bot : rc.senseNearbyRobots()) {
                if (bot.getID() == el.first) {
                    if (rc.canSendMessage(bot.getLocation())) {
                        processPacket(bot.getLocation(), el.second);
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    public boolean hasQueued() {
        return !queue.isEmpty();
    }
}
