public class HalsteadMetrics {
    public int DistOperators = 0, DistOperands = 0, TotOperators = 0, TotOperands = 0;

    public HalsteadMetrics() {}
    public void setParameters(int DistOprt, int DistOper, int TotOprt, int TotOper) {
        DistOperators=DistOprt;
        DistOperands=DistOper;
        TotOperators=TotOprt;
        TotOperands=TotOper;
    }
    public int getVocabulary() {
        int vocabulary = DistOperators + DistOperands;
        System.out.println("Vocabulary= "+ vocabulary);
        return vocabulary;
    }
    public int getProglen() {
        int proglen = TotOperators + TotOperands;
        System.out.println("Program Length= "+ proglen);
        return proglen;
    }
    public double getCalcProgLen() {
        double calcProgLen = DistOperators * (Math.log(DistOperators) / Math.log(2)) + DistOperands * (Math.log(DistOperands) / Math.log(2));
        System.out.println("Calculated Program Length= "+ calcProgLen);
        return calcProgLen;
    }
    public double getVolume() {
        double volume = (TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2));
        System.out.println("Volume= "+ volume);
        return volume;
    }
    public double getDifficulty() {
        double difficulty = (DistOperators / 2.0) * (TotOperands / (double) DistOperands);
        System.out.println("Difficulty= "+ difficulty);
        return difficulty;
    }
    public double getEffort() {
        double effort = ((DistOperators / 2.0) * (TotOperands / (double) DistOperands)) * ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2)));
        System.out.println("Effort= "+ effort);
        return effort;
    }
    public double getTimeReqProg() {
        double timeReqProg = (((DistOperators / 2.0) * (TotOperands / (double) DistOperands)) * ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2)))) / 18;
        System.out.println("Time Required to Program= "+ timeReqProg + " seconds");
        return timeReqProg;
    }
    public double getTimeDelBugs() {
        double timeDelBugs = ((TotOperators + TotOperands) * (Math.log(DistOperators + DistOperands) / Math.log(2))) / 3000;
        System.out.println("Number of delivered bugs= "+ timeDelBugs);
        return timeDelBugs;
    }
}
