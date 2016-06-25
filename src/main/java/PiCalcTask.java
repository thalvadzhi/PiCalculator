import static Constants.Constants.N_1;
import static Constants.Constants.N_1123;
import static Constants.Constants.N_2;
import static Constants.Constants.N_21460;
import static Constants.Constants.N_3;
import static Constants.Constants.N_4;
import static Constants.Constants.N_882_2_OVER_4_TIMES_256;
import static Constants.Constants.N_MINUS_1;

import java.util.concurrent.RecursiveTask;

import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PiCalcTask extends RecursiveTask<Apint[]> {
   private long from, to;
   private static long quanta;

   public static void setQuanta(long q) {
      quanta = q;
   }

   public PiCalcTask(long from, long to) {
      this.from = from;
      this.to = to;
   }

   @Override
   protected Apint[] compute() {
      if (to - from <= quanta) {
         return binarySplit(from, to);
      } else {
         long mid = from + (to - from) / 2;
         PiCalcTask left = new PiCalcTask(from, mid);
         PiCalcTask right = new PiCalcTask(mid, to);
         left.fork();
         right.fork();
         Apint[] rightAns = right.join();
         Apint[] leftAns = left.join();
         return merge(rightAns, leftAns);
      }
   }

   private Apint[] merge(Apint[] right, Apint[] left) {

      Apint p = left[0].multiply(right[0]);
      Apint q = left[1].multiply(right[1]);

      Apint qt = right[1].multiply(left[2]);
      Apint pt = left[0].multiply(right[2]);

      Apint t = qt.add(pt);

      return new Apint[] { p, q, t };
   }

   public Apint[] binarySplit(long a, long b) {
      Apint Pab = new Apint(1);
      Apint Qab = new Apint(1);
      Apint Tab = new Apint(1);

      Apint N_a = new Apint(a);
      if (b - a == 1) {//b - a == 1
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

}
