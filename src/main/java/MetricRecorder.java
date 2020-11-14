import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetricRecorder {
    Map<String, Map<String, String>> metrics = new HashMap<>();
    String fname;
    public MetricRecorder(String fname) {this.fname = fname;}
    public void record(String methodName, String metricName, String value) {
        if(!metrics.containsKey(methodName)) metrics.put(methodName, new HashMap<>());
        metrics.get(methodName).put(metricName, value);
    }
    public void export() throws IOException {
        Gson gson = new Gson();
        String json_str = gson.toJson(metrics);
        FileWriter writer = new FileWriter(fname);
        writer.write(json_str);
        writer.close();
    }
}
