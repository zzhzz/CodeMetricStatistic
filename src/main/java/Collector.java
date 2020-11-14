import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Collector {
    private Set<String> methodnames = new HashSet<>();
    private Map<String, Integer> map = new HashMap<>();
    private Map<String, Map<String, Integer>> names = new HashMap<>();
    private Map<String, Map<String, Integer>> opr = new HashMap<>();
    private Map<String, Map<String, Integer>> others = new HashMap<>();
    public void addOP(String methodName, String op) {
        methodnames.add(methodName);
        if (!opr.containsKey(methodName)) opr.put(methodName, new HashMap<>());
        if(!opr.get(methodName).containsKey(op)) opr.get(methodName).put(op, 0);
        opr.get(methodName).put(op, opr.get(methodName).get(op) + 1);
    }
    public void addOthers(String methodName, String type, Integer value) {
         methodnames.add(methodName);
         if(!others.containsKey(methodName)) others.put(methodName, new HashMap<>());
         if(!others.get(methodName).containsKey(type)) others.get(methodName).put(type, 0);
         others.get(methodName).put(type, others.get(methodName).get(type) + value);
    }
    public void addName(String methodName, String node) {
        methodnames.add(methodName);
        if (!names.containsKey(methodName)) names.put(methodName, new HashMap<>());
        if(!names.get(methodName).containsKey(node)) names.get(methodName).put(node, 0);
        names.get(methodName).put(node, names.get(methodName).get(node) + 1);
    }
    public void addCC(String methodName) {
        methodnames.add(methodName);
        if (!map.containsKey(methodName)) map.put(methodName, 0);
        map.put(methodName, map.get(methodName) + 1);
    }
    protected void cyclomaticComplexity(MetricRecorder recorder) {
        String label = "CyclomaticComplexity";
        for(String methodname : methodnames) {
            if(map.containsKey(methodname)) recorder.record(methodname, label, String.valueOf(map.get(methodname)));
            else recorder.record(methodname, label, String.valueOf(0));
        }
    }
    protected HalsteadMetrics HMetrics(String m_name) {
        int distOP = opr.get(m_name).size(), distOPr = names.get(m_name).size();
        int OPcnt = 0, OPrcnt=0;
        for (int f : opr.get(m_name).values()) OPcnt += f;
        for (int f : names.get(m_name).values()) OPrcnt += f;
        HalsteadMetrics hal = new HalsteadMetrics(distOP, distOPr);
        hal.setParameters(OPcnt, OPrcnt);
        return hal;
    }
    protected void HalsteadMetrics(MetricRecorder recorder) {
        for(String methodname : methodnames){
            HalsteadMetrics hal = HMetrics(methodname);
            recorder.record(methodname, "HalsteadCalcLength", hal.getCalcProgLen());
            recorder.record(methodname, "HalsteadLength", hal.getProglen());
            recorder.record(methodname, "HalsteadVocabulary", hal.getVocabulary());
            recorder.record(methodname, "HalsteadEffort", hal.getEffort());
            recorder.record(methodname, "HalsteadDifficulty", hal.getDifficulty());
            recorder.record(methodname, "HalsteadVolume", hal.getVolume());
            recorder.record(methodname, "HalsteadBugs", hal.getTimeDelBugs());
            recorder.record(methodname, "HalsteadTime", hal.getTimeReqProg());
            recorder.record(methodname, "NumberOfOperators", String.valueOf(hal.TotOperators));
            recorder.record(methodname, "NumberOfOperands", String.valueOf(hal.TotOperands));
        }
    }
    protected void ABC(MetricRecorder recorder) {
        for(String methodname : methodnames) {
            int a = others.get(methodname).get("NumberOfAssign");
            int b = others.get(methodname).get("NumberOfBranch");
            int c = others.get(methodname).get("NumberOfCond");
            recorder.record(methodname, "ABC", String.valueOf(Math.sqrt(a * a + b * b + c * c)));
        }
    }
    protected void otherMetrics(MetricRecorder recorder) {
        for(String methodname : methodnames) {
            for(Map.Entry<String, Integer> entry : others.get(methodname).entrySet()){
                recorder.record(methodname, entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }
}
