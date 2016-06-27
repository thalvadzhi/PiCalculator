import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PiWriter {
   private String outputFileName;
   private Pi pi;

   public PiWriter(String outputFileName, Pi pi) {
      this.outputFileName = outputFileName;
      this.pi = pi;
   }

   public void write() {
      try (FileWriter writer =
            new FileWriter(new File(outputFileName));
           PrintWriter output = new PrintWriter(writer)) {

         output.print(pi.getPiString());
         output.flush();
         writer.flush();

      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
