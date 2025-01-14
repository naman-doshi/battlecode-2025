package caterpillow.packet;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;
import caterpillow.packet.packets.*;
import caterpillow.util.Pair;
import caterpillow.util.TowerTracker;

import java.util.*;

import static caterpillow.Game.*;
import static caterpillow.util.TowerTracker.coinTowers;
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

    public void processMessage(Message msg) throws GameActionException {
//        int type = (msg.getBytes() >>> PAYLOAD_SIZE);
        int type = getBits(msg.getBytes(), PAYLOAD_SIZE, TYPE_SIZE);
//        int payload = (msg.getBytes() & (-1 >>> TYPE_SIZE));
        int payload = getBits(msg.getBytes(), 0, PAYLOAD_SIZE);
        int sender = msg.getSenderID();
        switch (type) {
            case 0:
                AdoptionPacket adoptionPacket = new AdoptionPacket();
                bot.handleAdoptionPacket(adoptionPacket, sender);
            case 1: // make sure these packet types are synced
                assert false : "we dont send these anymore\n";
                OriginPacket originPacket = new OriginPacket(decodeLoc(payload));
                bot.handleOriginPacket(originPacket, sender);
                break;
            case 2:
                SeedPacket seedPacket = new SeedPacket(payload);
                bot.handleSeedPacket(seedPacket, sender);
                break;
            case 3:
//                int id = payload >>> StrategyPacket.STRATEGY_DATA_SIZE;
                int id = getBits(payload, StrategyPacket.STRATEGY_DATA_SIZE, StrategyPacket.STRATEGY_ID_SIZE);
//                int data = payload & (-1 >>> (StrategyPacket.STRATEGY_ID_SIZE + TYPE_SIZE));
                int data = getBits(payload, 0, StrategyPacket.STRATEGY_DATA_SIZE);
                StrategyPacket strategyPacket = new StrategyPacket(id, data);
                bot.handleStrategyPacket(strategyPacket, sender);
                break;
            case 4:
                int encLoc = getBits(payload, 0, ENC_LOC_SIZE);
                TowerTracker.coinTowers = getBits(payload, ENC_LOC_SIZE + TowerTracker.MAX_TOWER_BITS, TowerTracker.MAX_TOWER_BITS);
                TowerTracker.hasReceivedInitPacket = true;

                if (coinTowers == 0) {
                    TowerTracker.broken = true;
                }
                bot.handleOriginPacket(new OriginPacket(decodeLoc(encLoc)), sender);
                break;
            default:
                assert false;
        }
    }

    public void read() throws GameActionException {
        Message[] prev = rc.readMessages(time - 1);
        for (int i = prev_read; i < prev.length; i++) {
            processMessage(prev[i]);
        }
        Message[] cur = rc.readMessages(time);
        prev_read = cur.length;
        for (Message msg : cur) {
            processMessage(msg);
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
        switch (packet) {
            case AdoptionPacket adoptionPacket -> {
                type = 0;
                payload = 0;
            }
            case OriginPacket originPacket -> {
                type = 1;
                payload = encodeLoc(originPacket.loc);
            }
            case SeedPacket seedPacket -> {
                type = 2;
                payload = seedPacket.seed;
            }
            case StrategyPacket strategyPacket -> {
                type = 3;
                payload = 0;
                payload = writeBits(payload, strategyPacket.strategyData, 0, StrategyPacket.STRATEGY_DATA_SIZE);
                payload = writeBits(payload, strategyPacket.strategyID, StrategyPacket.STRATEGY_DATA_SIZE, StrategyPacket.STRATEGY_ID_SIZE);
//            payload = strategyPacket.strategyData + (strategyPacket.strategyID << StrategyPacket.STRATEGY_DATA_SIZE);
            }
            case InitPacket initPacket -> {
                type = 4;
                payload = 0;
                payload = writeBits(payload, encodeLoc(initPacket.loc), 0, ENC_LOC_SIZE);
                payload = writeBits(payload, initPacket.coinTowers, ENC_LOC_SIZE + TowerTracker.MAX_TOWER_BITS, TowerTracker.MAX_TOWER_BITS);
            }
            case null, default -> {
                System.out.println("wtf is this packet");
                return;
            }
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
                assert bot != null;
                assert bot.getLocation() != null;
                if (bot.getID() == el.first) {
                    assert bot.getLocation() != null;
                    if (rc.canSendMessage(bot.getLocation())) {
                        assert bot.getLocation() != null;
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
