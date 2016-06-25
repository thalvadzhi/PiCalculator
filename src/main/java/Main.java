import static Constants.Constants.CLI_BENCHMARK;
import static Constants.Constants.CLI_OUTPUT_FILE;
import static Constants.Constants.CLI_PRECISION;
import static Constants.Constants.CLI_THREAD_COUNT;
import static Constants.Constants.CLI_VERBOSE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
   public static long precision = 1000000;
   public static int threads = 8;
   public static boolean verbose = false;
   public static String outputFile = "pi-ramanujan.txt";
   public static boolean benchmark = false;

   public static void main(String[] args) {
      parseCommandLineInput(args);

      if (!benchmark) {
         PiCalculator calc = new PiCalculator(precision, threads, verbose);
         long time = System.currentTimeMillis();
         Pi pi = calc.calculatePi();
         System.out.printf("Calculated pi to %d digits on %d threads in %d ms\n",
               precision, threads, (System.currentTimeMillis() - time));

         PiWriter writer = new PiWriter(outputFile, pi);
         writer.write();
         System.out.println("Wrote pi to file!");
      } else {
         Benchmark.start(precision);
      }

   }

   public static void compare(String a1, String a2) {
      for (int i = 0; i < a1.length(); i++) {
         if (a1.charAt(i) != a2.charAt(i)) {
            System.out.println("Differs at " + i);
            break;
         }
      }
      System.out.println("Are equal");
   }

   private static void parseCommandLineInput(String[] args) {
      Options options = setOptions();
      CommandLine cmd = getCommandLineParser(args, options);
      HelpFormatter formatter = new HelpFormatter();

      if (cmd.hasOption(CLI_PRECISION)) {
         precision = Integer.parseInt(cmd.getOptionValue(CLI_PRECISION));
      } else {
         formatter.printHelp("Precision must be set!", options);
         System.exit(1);
      }

      if (cmd.hasOption(CLI_THREAD_COUNT)) {
         threads = Integer.parseInt(cmd.getOptionValue(CLI_THREAD_COUNT));
      } else {
         formatter.printHelp("Thread count must be set!", options);
         System.exit(1);
      }

      if (cmd.hasOption(CLI_VERBOSE)) {
         verbose = true;
      } else {
         verbose = false;
      }

      if (cmd.hasOption(CLI_OUTPUT_FILE)) {
         outputFile = cmd.getOptionValue(CLI_OUTPUT_FILE);
      }

      if (cmd.hasOption(CLI_BENCHMARK)) {
         benchmark = true;
      }

   }

   private static CommandLine getCommandLineParser(String[] args, Options opts) {
      Options options = opts;

      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = null;
      try {
         cmd = parser.parse(options, args);
      } catch (ParseException e) {
         System.out.println("Couldn't parse input.");
      }
      return cmd;
   }

   private static Options setOptions() {
      Options options = new Options();
      options.addOption(CLI_VERBOSE, false,
            "determine whether to print thread information or not")
            .addOption(CLI_THREAD_COUNT, true, "number of threads to calculate pi on")
            .addOption(CLI_PRECISION, true, "number of digits of pi to calculate")
            .addOption(CLI_OUTPUT_FILE, false, "the file to output the calculated pi to")
            .addOption(CLI_BENCHMARK, false, "whether to run a benchmark or not");

      return options;
   }

}
