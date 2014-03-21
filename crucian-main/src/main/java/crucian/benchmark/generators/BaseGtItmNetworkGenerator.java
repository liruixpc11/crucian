package crucian.benchmark.generators;

import crucian.benchmark.ProcessUtility;
import crucian.benchmark.RandomUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created at 14-3-4 下午4:48.
 *
 * @author lirui
 */
public abstract class BaseGtItmNetworkGenerator<N> implements NetworkGenerator<N> {
    private static final String itmPath = "/home/lirui/app/ns-allinone-2.34/bin/";

    private RandomUtility randomUtility = new RandomUtility();
    private ProcessUtility processUtility = new ProcessUtility();

    private int minNodeCount;
    private int maxNodeCount;
    private double minResource;
    private double maxResource;
    private int nextLayer = 1;

    public BaseGtItmNetworkGenerator(int minNodeCount, int maxNodeCount, double minResource, double maxResource) {
        this.minNodeCount = minNodeCount;
        this.maxNodeCount = maxNodeCount;
        this.minResource = minResource;
        this.maxResource = maxResource;
    }

    @Override
    public N create() {
        try {
            String configFile = createConfigFile();
            String graphFile = createGraphFile(configFile);
            N network = loadFromFile(graphFile);
            new File(configFile).delete();
            new File(graphFile).delete();
            return network;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract N loadFromFile(String graphFile) throws Exception;

    private String createGraphFile(String configFile) {
        String sbgFile = configFile + "-0.gb";
        String altFile = configFile + "-0.alt";

        executeCommand(itmPath + "itm " + configFile + "");
        executeCommand(itmPath + "sgb2alt " + sbgFile + " " + altFile);

        new File(sbgFile).delete();
        return altFile;
    }

    private String createConfigFile() throws Exception {
        String tempConfigFile = formatTempFileName("config");
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(tempConfigFile))) {
            printWriter.println(configFileContent());
        }
        return tempConfigFile;
    }

    protected abstract String configFileContent();

    private void executeCommand(String command) {
        processUtility.executeCommand(command);
    }

    private String formatTempFileName(String postfix) {
        String filename = System.getProperty("java.io.tmpdir") + "/" + System.nanoTime();
        if (postfix != null && !postfix.isEmpty()) {
            return filename + "." + postfix;
        }

        return filename;
    }

    protected int nodeCount() {
        if (maxNodeCount == minNodeCount) return maxNodeCount;
        return randomUtility.nextInt(maxNodeCount - minNodeCount) + minNodeCount;
    }

    protected double resource() {
        return randomUtility.nextDouble() * (maxResource - minResource) + minResource;
    }
}
