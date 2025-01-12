package caterpillow.util;

import java.util.ArrayList;
import java.util.List;

// bro what is happening

public class WRandom {
    public int totw;
    public List<Integer> weights;

    public WRandom(int... ws) {
        weights = new ArrayList<>();
        totw = 0;
        for (int w : ws) {
            weights.add(w);
            totw += w;
        }
    }

    public int query() {
        return 0;
    }
}
