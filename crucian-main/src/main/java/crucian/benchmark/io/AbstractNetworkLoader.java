package crucian.benchmark.io;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created at 14-3-4 上午9:42.
 *
 * @author lirui
 */
public abstract class AbstractNetworkLoader<T extends AbstractConstraint, V extends Node<T>, L extends Link<T>> implements NetworkLoader<Network<T, V, L>> {
    /**
     * 创建空的网络
     *
     * @return 空网络
     */
    protected abstract Network<T, V, L> createNetwork();

    /**
     * 处理内容，生成网络
     *
     * @param content 内容
     * @param network 网络
     */
    protected abstract void process(String content, Network<T, V, L> network);

    @Override
    public final Network<T, V, L> load(Object source) throws Exception {
        Network<T, V, L> network = createNetwork();
        String content;
        if (source instanceof String) {
            content = (String) source;
        } else if (source instanceof InputStream) {
            InputStreamReader inputStreamReader = new InputStreamReader((InputStream) source, "UTF-8");
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int bytes = buffer.length;
            while (bytes == buffer.length) {
                bytes = inputStreamReader.read(buffer, 0, buffer.length);
                sb.append(buffer, 0, bytes);
            }

            content = sb.toString();
        } else {
            throw new IllegalArgumentException("不支持来源类型" + source.getClass());
        }

        process(content, network);
        return network;
    }

    public Network<T, V, L> loadFromFile(String fileName) throws Exception {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            return load(fileInputStream);
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
