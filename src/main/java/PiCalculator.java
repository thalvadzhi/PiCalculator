import static Constants.Constants.DIGITS_PER_TERM;
import static Constants.Constants.N_10;
import static Constants.Constants.N_3528;
import static Constants.Constants.QUANTA;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PiCalculator {
   private long precision;
   private ArrayList<Apint[]> pqts;
   private ArrayList<PartialSumCalculator> threads;
   private long numberOfThreads;
   private long numberOfTerms;
   private ExecutorService executor;
   private Apint precisionActual;
   private boolean verbose;

   public PiCalculator(long precision, long threads, boolean verbose) {
      this.precision = precision - 1;
      if (threads <= 0) {
         this.numberOfThreads = Runtime.getRuntime().availableProcessors();
      } else {
         this.numberOfThreads = threads;
      }
      this.threads = new ArrayList<>();
      this.pqts = new ArrayList<>();
      this.executor = Executors.newWorkStealingPool((int) this.numberOfThreads);
      this.verbose = verbose;
      this.precisionActual = ApintMath.pow(N_10, this.precision);
      this.numberOfTerms = (long) (this.precision / DIGITS_PER_TERM) + 1;
   }

   public Pi calcPiAlternative() {
      long quanta;
      if (this.numberOfTerms <= QUANTA / 2) {
         quanta = this.numberOfTerms / (2 * this.numberOfThreads);
      } else {
         quanta = QUANTA;
      }
      PiCalcTask.setQuanta(quanta);
      ForkJoinPool f = new ForkJoinPool((int) this.numberOfThreads);
      Apint[] pqt = f.invoke(new PiCalcTask(0, this.numberOfTerms));
      Apint pi = N_3528.multiply(precisionActual);
      pi = pi.multiply(pqt[1]);
      pi = pi.divide(pqt[2]);

      return new Pi(pi);
   }

   public Pi calculatePi() {
      long quanta = this.numberOfTerms / (2 * this.numberOfThreads);
      int i = 0;
      long upperBound;
      long time = System.currentTimeMillis();
      while ((i + 1) * quanta <= this.numberOfTerms) {
         //TODO maybe do this a little bit better
         if ((i + 2) * quanta > this.numberOfTerms
               && (i + 1) * quanta < this.numberOfTerms) {
            long temp = this.numberOfTerms - ((i + 1) * quanta);
            upperBound = (i + 1) * quanta + temp;
         } else {
            upperBound = (i + 1) * quanta;
         }
         PartialSumCalculator sumCalc =
               new PartialSumCalculator(i * quanta, upperBound, verbose);
         threads.add(sumCalc);
         executor.execute(sumCalc);
         i++;
      }

      executor.shutdown();

      while (!executor.isTerminated()) {
      }

      System.out.println("IT TOOK " + (System.currentTimeMillis() - time));
      collectPqts();
      Apint[] pqt;
      if (this.numberOfThreads > 1) {
         pqt = calculateFinalActual();
      } else {
         pqt = calculateFinalPqt(this.pqts);
      }

      Apint pi = N_3528.multiply(precisionActual);
      pi = pi.multiply(pqt[1]);
      pi = pi.divide(pqt[2]);

      return new Pi(pi);
   }

   private Apint[] calculateFinalPqt(ArrayList<Apint[]> pqts) {
      Apint[] left = pqts.get(0);
      pqts.remove(0);
      while (pqts.size() > 0) {
         Apint[] right = pqts.get(0);
         pqts.remove(0);

         Apint p = left[0].multiply(right[0]);
         Apint q = left[1].multiply(right[1]);

         Apint qt = right[1].multiply(left[2]);
         Apint pt = left[0].multiply(right[2]);

         Apint t = qt.add(pt);

         left = new Apint[] { p, q, t };
      }
      return left;
   }

   private Apint[] calculateFinalActual() {
      int sizeInterval = (int) (pqts.size() / (this.numberOfThreads));
      ArrayList<PqtsCalculator> threads = new ArrayList<>();
      ArrayList<Apint[]> newPqts = new ArrayList<>();

      ExecutorService exec = Executors.newWorkStealingPool((int) this.numberOfThreads);
      int i = 0;
      long upperBound;
      while (i * sizeInterval < pqts.size()) {
         if ((i + 2) * sizeInterval > pqts.size()
               && (i + 1) * sizeInterval < pqts.size()) {
            long temp = pqts.size() - ((i + 1) * sizeInterval);
            upperBound = (i + 1) * sizeInterval + temp;
         } else {
            upperBound = (i + 1) * sizeInterval;
         }
         ArrayList<Apint[]> pqt =
               new ArrayList<Apint[]>(pqts.subList(i * sizeInterval, (int) upperBound));
         PqtsCalculator calc = new PqtsCalculator(pqt);
         threads.add(calc);
         exec.execute(calc);
         i++;
      }

      exec.shutdown();

      while (!exec.isTerminated()) {
      }

      this.pqts.clear();
      ArrayList<Apint[]> s = new ArrayList<>();
      for (PqtsCalculator thread : threads) {
         s.add(thread.getResult());
      }

      return calculateFinalPqt(s);
   }

   private void collectPqts() {
      for (PartialSumCalculator sum : this.threads) {
         this.pqts.add(sum.getPQT());
      }
   }


}
