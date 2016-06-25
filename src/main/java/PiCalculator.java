import static Constants.Constants.DIGITS_PER_TERM;
import static Constants.Constants.N_10;
import static Constants.Constants.N_3528;
import static Constants.Constants.QUANTA;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PiCalculator {
   private long precision;
   private ArrayList<Apint[]> pqts;
   private long numberOfThreads;
   private long numberOfTerms;
   private Apint precisionActual;
   private boolean verbose;

   public PiCalculator(long precision, long threads, boolean verbose) {
      this.precision = precision - 1;
      if (threads <= 0) {
         this.numberOfThreads = Runtime.getRuntime().availableProcessors();
      } else {
         this.numberOfThreads = threads;
      }
      this.pqts = new ArrayList<>();
      this.verbose = verbose;
      this.precisionActual = ApintMath.pow(N_10, this.precision);
      this.numberOfTerms = (long) (this.precision / DIGITS_PER_TERM) + 1;
   }

   public Pi calculatePi() {
      long quanta;
      if (this.numberOfTerms <= QUANTA / 2) {
         quanta = this.numberOfTerms / (2 * this.numberOfThreads);
      } else {
         quanta = QUANTA;
      }
      PiCalcTask.setQuanta(quanta);
      if (verbose) {
         System.out.printf("Starting calculation on %d threads", this.numberOfThreads);
      }
      ForkJoinPool f = new ForkJoinPool((int) this.numberOfThreads);
      Apint[] pqt = f.invoke(new PiCalcTask(0, this.numberOfTerms, verbose));
      Apint pi = N_3528.multiply(precisionActual);
      pi = pi.multiply(pqt[1]);
      pi = pi.divide(pqt[2]);

      return new Pi(pi);
   }

}
