package crucian.benchmark;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created at 14-3-4 下午5:27.
 *
 * @author lirui
 */
public class ProcessUtility {
    private Runtime runtime = Runtime.getRuntime();

    public static class ExecuteResult {
        private String out;
        private String err;

        public ExecuteResult(String out, String err) {
            this.out = out;
            this.err = err;
        }

        public String getOut() {
            return out;
        }

        public String getErr() {
            return err;
        }
    }

    public ExecuteResult executeCommand(String command) {
        try {
            // System.out.println(command);
            Process process = runtime.exec(command);
            String out = readAll(process.getInputStream());
            if (!out.trim().isEmpty()) {
                System.out.println(out);
            }
            String err = readAll(process.getErrorStream());
            if (!out.trim().isEmpty()) {
                System.err.println(err);
            }
            return new ExecuteResult(out, err);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readAll(InputStream inputStream) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }
}
