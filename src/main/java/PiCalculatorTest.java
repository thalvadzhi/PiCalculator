import org.apfloat.Apint;
import org.apfloat.ApintMath;

import Constants.Constants;


public class PiCalculatorTest {
   private long precision;

   private Apint N_882;
   private Apint N_4;
   private Apint N_1123;
   private Apint N_21460;
   private Apint N_777924;
   private Apint N_1;
   private Apint N_0;
   private Apint N_2;
   private Apint N_3;
   private Apint N_MINUS_1;
   private Apint N_BIG;
   private Apint N_882_2_OVER_4_TIMES_256;
   private Apint N_256;
   private Apint N_10;
   private Apint N_3528;
   private Apint precisionActual;


   public PiCalculatorTest(long precision) {
      this.precision = precision;
      Constants.N_MINUS_1 = new Apint(-1);
      N_0 = new Apint(0);
      N_1 = new Apint(1);
      N_2 = new Apint(2);
      N_3 = new Apint(3);
      N_4 = new Apint(4);
      N_10 = new Apint(10);
      N_256 = new Apint(256);
      N_882 = new Apint(882);
      N_1123 = new Apint(1123);
      N_3528 = new Apint(3528);
      N_21460 = new Apint(21460);
      N_777924 = new Apint(777924);
      N_882_2_OVER_4_TIMES_256 = new Apint(49787136);

      precisionActual = ApintMath.pow(N_10, this.precision);
   }

   public Apint[] bs(long a, long b) {
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
         Apint[] am = bs(a, m);

         Apint[] mb = bs(m, b);

         Pab = am[0].multiply(mb[0]);
         Qab = am[1].multiply(mb[1]);

         Apint qTimesT = mb[1].multiply(am[2]);
         Apint pTimesT = am[0].multiply(mb[2]);

         Tab = qTimesT.add(pTimesT);
      }
      return new Apint[] { Pab, Qab, Tab };
   }

   public Apint calculatePiBs2() {
      double digitsPerTerm = 5.87;
      double digits = Math.pow(10, this.precision);
      long n = (long) (this.precision / digitsPerTerm) + 1;
      Apint[] pqt = bs(0, 6);
      Apint[] pqt2 = bs(6, 12);
      Apint[] pqt3 = bs(12, 18);
      Apint p = pqt[0].multiply(pqt2[1]);
      Apint q = pqt[1].multiply(pqt2[1]);

      Apint temp = pqt2[1].multiply(pqt[2]);
      Apint temp1 = pqt[0].multiply(pqt2[2]);
      Apint t = temp.add(temp1);

      q = q.multiply(pqt3[1]);

      temp = pqt3[1].multiply(t);
      temp1 = p.multiply(pqt3[2]);

      Apint tm = temp.add(temp1);

      Apint[] pqtACTUAL = bs(0, n);
      Apint piiii = N_3528.multiply(precisionActual);
      piiii = piiii.multiply(q);
      piiii = piiii.divide(t);
      //Apint Q = pqt[1].multiply(pqt2[1]);
      //Apint T = pqt[2].multiply(pqt2[2]);

      Apint dig = new Apint((long) digits);
      Apint pi = N_3528.multiply(precisionActual);
      pi = pi.multiply(pqt[1]);
      pi = pi.divide(pqt[2]);

      Apint pi2 = N_3528.multiply(precisionActual);
      pi2 = pi2.multiply(pqt2[1]);
      pi2 = pi2.divide(pqt2[2]);

      Apint pipi = pi.multiply(pi2);
      return pi;
   }

   public Apint calculatePiBs() {
      double digitsPerTerm = 5.87;
      double digits = Math.pow(10, this.precision);
      long n = (long) (this.precision / digitsPerTerm) + 1;
      Apint[] pqt = bs(0, n);

      Apint dig = new Apint((long) digits);
      Apint pi = N_3528.multiply(precisionActual);
      pi = pi.multiply(pqt[1]);
      pi = pi.divide(pqt[2]);
      return pi;
   }

   public Apint calculatePiInInt() {

      Apint precision = ApintMath.pow(N_10, this.precision);
      Apint sumAk = precision;
      Apint sumBk = new Apint(0);
      Apint ak = precision;
      Apint bk = new Apint(1);
      Apint k = new Apint(1);
      int i = 0;
      while (true) {
         Apint fourK = k.multiply(N_4);
         ak = ak.multiply(fourK.subtract(N_1)).multiply(fourK.subtract(N_2))
               .multiply(fourK.subtract(N_3)).multiply(N_MINUS_1);
         ak = ak.divide(N_882_2_OVER_4_TIMES_256.multiply(ApintMath.pow(k, 3)));

         bk = ak.multiply(k);
         sumAk = sumAk.add(ak);
         sumBk = sumBk.add(bk);
         k = k.add(N_1);
         if (i == this.precision) {
            break;
         }
         i++;
      }

      Apint total = N_1123.multiply(sumAk);
      total = total.add(N_21460.multiply(sumBk));

      Apint pi = N_3528.multiply(precision).multiply(precision);
      pi = pi.divide(total);
      return pi;
   }
}
