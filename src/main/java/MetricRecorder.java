import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetricRecorder {
    Map<String, Map<String, String>> metrics = new HashMap<>();
    public void record(String methodName, String metricName, String value) {
        if(!metrics.containsKey(methodName)) metrics.put(methodName, new HashMap<>());
        metrics.get(methodName).put(metricName, value);
    }
    public void export(String dir_path, String fname) throws IOException {
        Gson gson = new Gson();
        String json_str = gson.toJson(metrics);
        System.out.println(dir_path);
        File dir = new File(dir_path);
        if(!dir.exists()) dir.mkdirs();
        FileWriter writer = new FileWriter(dir_path + "/" + fname);
        writer.write(json_str);
        writer.close();
    }
}
