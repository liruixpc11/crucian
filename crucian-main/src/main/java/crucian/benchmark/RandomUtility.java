package crucian.benchmark;

import java.util.Random;

/**
 * Created at 14-3-3 下午2:29.
 *
 * @author lirui
 */
public class RandomUtility {
    private Random random = new Random(System.nanoTime());

    public double nextDouble() {
        return random.nextDouble();
    }

    public int nextInt(int maxE) {
        return random.nextInt(maxE);
    }

    public long nextPoison(double lambda) {
        long x = 0;
        double b = 1;
        double c = Math.exp(-lambda);
        double u;
        do {
            u = nextDouble();
            b *= u;
            if (b >= c) {
                x++;
            }
        } while (b >= c);

        return x;
    }

    public double nextNormal(double avg, double sigma2) {
        final double N = 12;

        double x = 0;
        for (int i = 0; i < N; i++) {
            x = x + nextDouble();
        }

        x = (x - N / 2) / (Math.sqrt(N / 12));
        x = avg + x * Math.sqrt(sigma2);

        return x;
    }
}
