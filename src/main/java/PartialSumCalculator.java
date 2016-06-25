import static Constants.Constants.N_1;
import static Constants.Constants.N_1123;
import static Constants.Constants.N_2;
import static Constants.Constants.N_21460;
import static Constants.Constants.N_3;
import static Constants.Constants.N_4;
import static Constants.Constants.N_882_2_OVER_4_TIMES_256;
import static Constants.Constants.N_MINUS_1;

import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PartialSumCalculator implements Runnable {

   private long from, to;
   private Apint[] PQT;
   private boolean verbose;

   public PartialSumCalculator(long from, long to, boolean verbose) {
      this.PQT = new Apint[3];
      this.from = from;
      this.to = to;
      this.verbose = verbose;
   }

   public Apint[] binarySplit(long a, long b) {
      Apint Pab = new Apint(1);
      Apint Qab = new Apint(1);
      Apint Tab = new Apint(1);

      Apint N_a = new Apint(a);
      if (b - a == 1) {
         if (a == 0) {
            Pab = N_1;
            Qab = N_1;
         } else {
            Apint fourA = new Apint(a);

            fourA = fourA.multiply(N_4);
            Pab = Pab.multiply(fourA.subtract(N_1)).multiply(fourA.subtract(N_2))
                  .multiply(fourA.subtract(N_3));
            Qab = N_882_2_OVER_4_TIMES_256.multiply(ApintMath.pow(N_a, 3));
         }
         Tab = Pab.multiply(N_1123.add((N_21460.multiply(N_a))));
         if (a % 2 == 1) {//number is not even
            Tab = Tab.multiply(N_MINUS_1);
         }

      } else {
         long m = (a + b) / 2;
         //P Q T
         Apint[] am = binarySplit(a, m);

         Apint[] mb = binarySplit(m, b);

         Pab = am[0].multiply(mb[0]);
         Qab = am[1].multiply(mb[1]);

         Apint qTimesT = mb[1].multiply(am[2]);
         Apint pTimesT = am[0].multiply(mb[2]);

         Tab = qTimesT.add(pTimesT);
      }
      return new Apint[] { Pab, Qab, Tab };
   }

   private Apint[] calculatePqt() {
      return binarySplit(from, to);
   }

   public void run() {
      long threadId = Thread.currentThread().getId();
      long startTime = System.currentTimeMillis();
      if (verbose) {
         System.out.printf("Thread with id: %d was started.\n", threadId);
      }

      PQT = calculatePqt();

      long finishTime = System.currentTimeMillis();
      long elapsedTime = finishTime - startTime;
      if (verbose) {
         System.out.printf("Thread with id: %d finished execution in %d miliseconds.\n",
               threadId, elapsedTime);
      }
   }

   public Apint[] getPQT() {
      return PQT;
   }


}
