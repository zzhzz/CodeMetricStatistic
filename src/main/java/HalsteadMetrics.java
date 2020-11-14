public class HalsteadMetrics {
    public int DistOperators = 0, DistOperands = 0, TotOperators = 0, TotOperands = 0;

    public HalsteadMetrics(int distop, int distopr) {DistOperators = distop; DistOperands = distopr;}
    public void setParameters(int TotOprt, int TotOper) {
        TotOperators=TotOprt;
        TotOperands=TotOper;
    }
    public String getVocabulary() {
        int vocabulary = DistOperators + DistOperands;
        return String.valueOf(vocabulary);
    }
    public String getProglen() {
        int proglen = TotOperators + TotOperands;
        return String.valueOf(proglen);
    }
    public String getCalcProgLen() {
        double calcProgLen = DistOperators * (Math.log(DistOperators) / Math.log(2)) + DistOperands * (Math.log(DistOperands) / Math.log(2));
        return String.valueOf(calcProgLen);
    }
    public String getVolume() {
        double volume = (TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2));
        return String.valueOf(volume);
    }
    public String getDifficulty() {
        double difficulty = (DistOperators / 2.0) * (TotOperands / (double) DistOperands);
        return String.valueOf(difficulty);
    }
    public String getEffort() {
        double effort = ((DistOperators / 2.0) * (TotOperands / (double) DistOperands)) * ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2)));
        return String.valueOf(effort);
    }
    public String getTimeReqProg() {
        double timeReqProg = (((DistOperators / 2.0) * (TotOperands / (double) DistOperands)) * ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2)))) / 18;
        return String.valueOf(timeReqProg);
    }
    public String getTimeDelBugs() {
        double timeDelBugs = ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2))) / 3000;
        return String.valueOf(timeDelBugs);
    }
}
