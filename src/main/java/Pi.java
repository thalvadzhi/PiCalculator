import org.apfloat.Apint;

public class Pi {
   private Apint pi;

   public Pi(Apint pi) {
      this.pi = pi;
   }

   public Apint getPiValue() {
      return pi;
   }

   public String getPiString() {
      String piString = pi.toString(true);
      return piString.substring(0, 1) + "." + piString.substring(1, piString.length());
   }

   @Override
   public String toString() {
      return getPiString();
   }
}
