package caterpillow.graveyard;

import battlecode.common.MapInfo;
import caterpillow.util.GameBiConsumer;
import caterpillow.util.GameConsumer;

import java.util.ArrayList;

public class Graveyard {
    private static int listenerSize = 0, biListenerSize = 0;
    private static GameConsumer<MapInfo>[] listeners;
    private static GameBiConsumer<MapInfo, MapInfo>[] biListeners;
    private static ArrayList<Integer> occupiedListeners, occupiedBiListeners;

    private static final int MAX = 10;

    @SuppressWarnings("unchecked")
    public static void init() {
        listeners = (GameConsumer<MapInfo>[]) new GameConsumer[MAX];
        biListeners = (GameBiConsumer<MapInfo, MapInfo>[]) new GameBiConsumer[MAX];
        occupiedListeners = new ArrayList<>();
        occupiedBiListeners = new ArrayList<>();
        for (int i = MAX - 1; i >= 0; i--) {
            occupiedListeners.add(i);
            occupiedBiListeners.add(i);
        }
    }
    public static void registerListener(GameConsumer<MapInfo> listener) {
        int i = occupiedListeners.getLast();
        occupiedListeners.removeLast();
        listeners[i] = listener;
        listenerSize++;
    }
    public static void registerListener(GameBiConsumer<MapInfo, MapInfo> listener) {
        int i = occupiedBiListeners.getLast();
        occupiedBiListeners.removeLast();
        biListeners[i] = listener;
        biListenerSize++;
    }

    public static void unregisterListener(GameConsumer<MapInfo> listener) {
        listenerSize--;
        for (int i = MAX - 1; i >= 0; i--) {
            if (listeners[i] == listener) {
                occupiedListeners.add(i);
                listeners[i] = null;
                return;
            }
        }
    }

    public static void unregisterListener(GameBiConsumer<MapInfo, MapInfo> listener) {
        biListenerSize--;
        for (int i = MAX - 1; i >= 0; i--) {
            if (biListeners[i] == listener) {
                occupiedBiListeners.add(i);
                biListeners[i] = null;
                return;
            }
        }
    }

    //    public static void update() throws GameActionException {
//        // these are all copies, so this is safe
//        MapInfo[] infos = rc.senseNearbyMapInfos();
//        for (int i = infos.length - 1; i >= 0; i--) {
//            MapInfo cur = infos[i];
//            int x = cur.getMapLocation().x;
//            int y = cur.getMapLocation().y;
//            MapInfo old = mapInfos[x][y];
//            if (old == null || old.getPaint() != cur.getPaint() || old.getMark() != cur.getMark() || old.isResourcePatternCenter() != cur.isResourcePatternCenter()) {
//                switch (listenerSize) {
//                    case 10: listeners[9].accept(cur);
//                    case 9:  listeners[8].accept(cur);
//                    case 8:  listeners[7].accept(cur);
//                    case 7:  listeners[6].accept(cur);
//                    case 6:  listeners[5].accept(cur);
//                    case 5:  listeners[4].accept(cur);
//                    case 4:  listeners[3].accept(cur);
//                    case 3:  listeners[2].accept(cur);
//                    case 2:  listeners[1].accept(cur);
//                    case 1:  listeners[0].accept(cur);
//                    case 0:  break; // No listeners to process
//                    default: throw new IllegalArgumentException("listenerSize exceeds 10");
//                }
//                switch (biListenerSize) {
//                    case 10: biListeners[9].accept(old, cur);
//                    case 9:  biListeners[8].accept(old, cur);
//                    case 8:  biListeners[7].accept(old, cur);
//                    case 7:  biListeners[6].accept(old, cur);
//                    case 6:  biListeners[5].accept(old, cur);
//                    case 5:  biListeners[4].accept(old, cur);
//                    case 4:  biListeners[3].accept(old, cur);
//                    case 3:  biListeners[2].accept(old, cur);
//                    case 2:  biListeners[1].accept(old, cur);
//                    case 1:  biListeners[0].accept(old, cur);
//                    case 0:  break; // No listeners to process
//                    default: throw new IllegalArgumentException("listenerSize exceeds 10");
//                }
//            }
//            mapInfos[x][y] = cur;
//            lastUpdateTime[x][y] = time;
//        }
//        int nxt = nearbyCnt = infos.length;
//        int x = rc.getLocation().x;
//        int y = rc.getLocation().y;
//        int maxX = mapWidth;
//        int maxY = mapHeight;
//        if (x < 4 || y < 4 || x + 4 >= maxX || y + 4 >= maxY) {
//            nearby[--nxt] = mapInfos[x][y];
//            if (x > 1)
//                nearby[--nxt] = mapInfos[x - 1][y];
//            if (y > 1)
//                nearby[--nxt] = mapInfos[x][y - 1];
//            if (y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 1];
//            if (x + 1 < maxX)
//                nearby[--nxt] = mapInfos[x + 1][y];
//            if (x > 1 && y > 1)
//                nearby[--nxt] = mapInfos[x - 1][y - 1];
//            if (x > 1 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 1];
//            if (x + 1 < maxX && y > 1)
//                nearby[--nxt] = mapInfos[x + 1][y - 1];
//            if (x + 1 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 1];
//            if (x > 2)
//                nearby[--nxt] = mapInfos[x - 2][y];
//            if (y > 2)
//                nearby[--nxt] = mapInfos[x][y - 2];
//            if (y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 2];
//            if (x + 2 < maxX)
//                nearby[--nxt] = mapInfos[x + 2][y];
//            if (x > 2 && y > 1)
//                nearby[--nxt] = mapInfos[x - 2][y - 1];
//            if (x > 2 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 1];
//            if (x > 1 && y > 2)
//                nearby[--nxt] = mapInfos[x - 1][y - 2];
//            if (x > 1 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 2];
//            if (x + 1 < maxX && y > 2)
//                nearby[--nxt] = mapInfos[x + 1][y - 2];
//            if (x + 1 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 2];
//            if (x + 2 < maxX && y > 1)
//                nearby[--nxt] = mapInfos[x + 2][y - 1];
//            if (x + 2 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 1];
//            if (x > 2 && y > 2)
//                nearby[--nxt] = mapInfos[x - 2][y - 2];
//            if (x > 2 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 2];
//            if (x + 2 < maxX && y > 2)
//                nearby[--nxt] = mapInfos[x + 2][y - 2];
//            if (x + 2 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 2];
//            if (x > 3)
//                nearby[--nxt] = mapInfos[x - 3][y];
//            if (y > 3)
//                nearby[--nxt] = mapInfos[x][y - 3];
//            if (y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 3];
//            if (x + 3 < maxX)
//                nearby[--nxt] = mapInfos[x + 3][y];
//            if (x > 3 && y > 1)
//                nearby[--nxt] = mapInfos[x - 3][y - 1];
//            if (x > 3 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 1];
//            if (x > 1 && y > 3)
//                nearby[--nxt] = mapInfos[x - 1][y - 3];
//            if (x > 1 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 3];
//            if (x + 1 < maxX && y > 3)
//                nearby[--nxt] = mapInfos[x + 1][y - 3];
//            if (x + 1 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 3];
//            if (x + 3 < maxX && y > 1)
//                nearby[--nxt] = mapInfos[x + 3][y - 1];
//            if (x + 3 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 1];
//            if (x > 3 && y > 2)
//                nearby[--nxt] = mapInfos[x - 3][y - 2];
//            if (x > 3 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 2];
//            if (x > 2 && y > 3)
//                nearby[--nxt] = mapInfos[x - 2][y - 3];
//            if (x > 2 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 3];
//            if (x + 2 < maxX && y > 3)
//                nearby[--nxt] = mapInfos[x + 2][y - 3];
//            if (x + 2 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 3];
//            if (x + 3 < maxX && y > 2)
//                nearby[--nxt] = mapInfos[x + 3][y - 2];
//            if (x + 3 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 2];
//            if (x > 4)
//                nearby[--nxt] = mapInfos[x - 4][y];
//            if (y > 4)
//                nearby[--nxt] = mapInfos[x][y - 4];
//            if (y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x][y + 4];
//            if (x + 4 < maxX)
//                nearby[--nxt] = mapInfos[x + 4][y];
//            if (x > 4 && y > 1)
//                nearby[--nxt] = mapInfos[x - 4][y - 1];
//            if (x > 4 && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x - 4][y + 1];
//            if (x > 1 && y > 4)
//                nearby[--nxt] = mapInfos[x - 1][y - 4];
//            if (x > 1 && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x - 1][y + 4];
//            if (x + 1 < maxX && y > 4)
//                nearby[--nxt] = mapInfos[x + 1][y - 4];
//            if (x + 1 < maxX && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x + 1][y + 4];
//            if (x + 4 < maxX && y > 1)
//                nearby[--nxt] = mapInfos[x + 4][y - 1];
//            if (x + 4 < maxX && y + 1 < maxY)
//                nearby[--nxt] = mapInfos[x + 4][y + 1];
//            if (x > 3 && y > 3)
//                nearby[--nxt] = mapInfos[x - 3][y - 3];
//            if (x > 3 && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x - 3][y + 3];
//            if (x + 3 < maxX && y > 3)
//                nearby[--nxt] = mapInfos[x + 3][y - 3];
//            if (x + 3 < maxX && y + 3 < maxY)
//                nearby[--nxt] = mapInfos[x + 3][y + 3];
//            if (x > 4 && y > 2)
//                nearby[--nxt] = mapInfos[x - 4][y - 2];
//            if (x > 4 && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x - 4][y + 2];
//            if (x > 2 && y > 4)
//                nearby[--nxt] = mapInfos[x - 2][y - 4];
//            if (x > 2 && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x - 2][y + 4];
//            if (x + 2 < maxX && y > 4)
//                nearby[--nxt] = mapInfos[x + 2][y - 4];
//            if (x + 2 < maxX && y + 4 < maxY)
//                nearby[--nxt] = mapInfos[x + 2][y + 4];
//            if (x + 4 < maxX && y > 2)
//                nearby[--nxt] = mapInfos[x + 4][y - 2];
//            if (x + 4 < maxX && y + 2 < maxY)
//                nearby[--nxt] = mapInfos[x + 4][y + 2];
//        } else {
//            nearby[--nxt] = mapInfos[x][y];
//            nearby[--nxt] = mapInfos[x - 1][y];
//            nearby[--nxt] = mapInfos[x][y - 1];
//            nearby[--nxt] = mapInfos[x][y + 1];
//            nearby[--nxt] = mapInfos[x + 1][y];
//            nearby[--nxt] = mapInfos[x - 1][y - 1];
//            nearby[--nxt] = mapInfos[x - 1][y + 1];
//            nearby[--nxt] = mapInfos[x + 1][y - 1];
//            nearby[--nxt] = mapInfos[x + 1][y + 1];
//            nearby[--nxt] = mapInfos[x - 2][y];
//            nearby[--nxt] = mapInfos[x][y - 2];
//            nearby[--nxt] = mapInfos[x][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y];
//            nearby[--nxt] = mapInfos[x - 2][y - 1];
//            nearby[--nxt] = mapInfos[x - 2][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 2];
//            nearby[--nxt] = mapInfos[x - 1][y + 2];
//            nearby[--nxt] = mapInfos[x + 1][y - 2];
//            nearby[--nxt] = mapInfos[x + 1][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y - 1];
//            nearby[--nxt] = mapInfos[x + 2][y + 1];
//            nearby[--nxt] = mapInfos[x - 2][y - 2];
//            nearby[--nxt] = mapInfos[x - 2][y + 2];
//            nearby[--nxt] = mapInfos[x + 2][y - 2];
//            nearby[--nxt] = mapInfos[x + 2][y + 2];
//            nearby[--nxt] = mapInfos[x - 3][y];
//            nearby[--nxt] = mapInfos[x][y - 3];
//            nearby[--nxt] = mapInfos[x][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y];
//            nearby[--nxt] = mapInfos[x - 3][y - 1];
//            nearby[--nxt] = mapInfos[x - 3][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 3];
//            nearby[--nxt] = mapInfos[x - 1][y + 3];
//            nearby[--nxt] = mapInfos[x + 1][y - 3];
//            nearby[--nxt] = mapInfos[x + 1][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 1];
//            nearby[--nxt] = mapInfos[x + 3][y + 1];
//            nearby[--nxt] = mapInfos[x - 3][y - 2];
//            nearby[--nxt] = mapInfos[x - 3][y + 2];
//            nearby[--nxt] = mapInfos[x - 2][y - 3];
//            nearby[--nxt] = mapInfos[x - 2][y + 3];
//            nearby[--nxt] = mapInfos[x + 2][y - 3];
//            nearby[--nxt] = mapInfos[x + 2][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 2];
//            nearby[--nxt] = mapInfos[x + 3][y + 2];
//            nearby[--nxt] = mapInfos[x - 4][y];
//            nearby[--nxt] = mapInfos[x][y - 4];
//            nearby[--nxt] = mapInfos[x][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y];
//            nearby[--nxt] = mapInfos[x - 4][y - 1];
//            nearby[--nxt] = mapInfos[x - 4][y + 1];
//            nearby[--nxt] = mapInfos[x - 1][y - 4];
//            nearby[--nxt] = mapInfos[x - 1][y + 4];
//            nearby[--nxt] = mapInfos[x + 1][y - 4];
//            nearby[--nxt] = mapInfos[x + 1][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y - 1];
//            nearby[--nxt] = mapInfos[x + 4][y + 1];
//            nearby[--nxt] = mapInfos[x - 3][y - 3];
//            nearby[--nxt] = mapInfos[x - 3][y + 3];
//            nearby[--nxt] = mapInfos[x + 3][y - 3];
//            nearby[--nxt] = mapInfos[x + 3][y + 3];
//            nearby[--nxt] = mapInfos[x - 4][y - 2];
//            nearby[--nxt] = mapInfos[x - 4][y + 2];
//            nearby[--nxt] = mapInfos[x - 2][y - 4];
//            nearby[--nxt] = mapInfos[x - 2][y + 4];
//            nearby[--nxt] = mapInfos[x + 2][y - 4];
//            nearby[--nxt] = mapInfos[x + 2][y + 4];
//            nearby[--nxt] = mapInfos[x + 4][y - 2];
//            nearby[--nxt] = mapInfos[x + 4][y + 2];
//        }
//    }
}
