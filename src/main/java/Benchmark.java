
public class Benchmark {
   public static void start(long precision) {
      int processors = Runtime.getRuntime().availableProcessors();
      long oneThread = 1;
      System.out.printf("Starting benchmark for %d digits!\n", precision);
      for (int i = 1; i <= 2 * processors; i++) {
         PiCalculator calc = new PiCalculator(precision, i, false);
         long time = System.currentTimeMillis();
         calc.calculatePi();
         long finishedAt = System.currentTimeMillis() - time;
         if (i == 1) {
            oneThread = finishedAt;
         }
         float acceleration = (float) oneThread / (float) finishedAt;
         System.out.printf("Finished in %d ms, on %d threads, acceleration is %f\n",
               finishedAt, i, acceleration);
      }


   }
}
