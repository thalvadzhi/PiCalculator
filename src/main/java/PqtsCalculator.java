import java.util.ArrayList;

import org.apfloat.Apint;

public class PqtsCalculator implements Runnable {
   ArrayList<Apint[]> pqts;
   private Apint[] result;

   public PqtsCalculator(ArrayList<Apint[]> pqts) {
      this.pqts = pqts;
   }

   private Apint[] calculatePqt() {
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

   @Override
   public void run() {
      result = calculatePqt();
   }

   public Apint[] getResult() {
      return result;
   }

}
