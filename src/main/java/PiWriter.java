import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PiWriter {
   private String outputFileName;
   private Pi pi;

   public PiWriter(String outputFileName, Pi pi) {
      this.outputFileName = outputFileName;
      this.pi = pi;
   }

   public void write() {
      try (DataOutputStream writer =
            new DataOutputStream(new FileOutputStream(outputFileName))) {

         writer.writeUTF(pi.getPiString());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
