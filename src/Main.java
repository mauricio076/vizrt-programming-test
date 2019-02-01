import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Main {

    // Global variable to keep track of already computed variables
    private static HashMap<String, Integer> variables = new HashMap();

    // Global variable to keep track of what variables are pending to compute
    private static HashSet<String> pending = new HashSet();

    // Global scanner method to read from cli or file
    private static Scanner scannerIn = new Scanner(System.in);

    // A method to parse the equation in the line and make the corresponding sum
    static String processSum(String line) {
        String[] input = line.split(" ");
        String variable = input[0];
        String response = "";
        List<Integer> operands = new ArrayList<>();
        boolean complete = true;
        for (int i = 2; i < input.length; i++) {
            if (input[i].equals("+") || input[i].equals(" ")) continue; // TODO: implement a better cleaning method
            try {
                operands.add(Integer.parseInt(input[i]));
            } catch (NumberFormatException e) {
                // Check if we have computed that variable already
                if (variables.containsKey(input[i])) {
                    operands.add(variables.get(input[i]));
                } else {
                    complete = false;
                }
            }
        }

        if (complete && !operands.isEmpty()) {
            variables.put(variable, operands.stream().mapToInt(Integer::intValue).sum());
            response = "===> " + variable + " = " + variables.get(variable);
        }
        return response;
    }

    // A method to process new input lines
    static void processInput(String in, BufferedWriter bufferedWriter) throws IOException {
        String response = processSum(in);
        if (response != "") {
            bufferedWriter.write(response);
            bufferedWriter.newLine();

            // Check if we can solve pending
            Iterator<String> iterator = pending.iterator();

            while (iterator.hasNext()) {
                String pend = iterator.next();

                response = processSum(pend);

                //print out the value of variables as soon as available and try to solve pending variables
                if (response != "") {
                    bufferedWriter.write(response);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    // remove from pending as soon as it is solved
                    iterator.remove();

                    //It is important to restart our iterator to make sure we dont left any pending items we already passed but just now can be solved
                    iterator = pending.iterator();
                }
            }
        } else {
            pending.add(in);
        }
    }

    public static void main(String[] args) throws IOException {

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));

        // If we are reading input from file
        if (args.length > 0) {
            scannerIn = new Scanner(new File(args[0]));
        }

        boolean exit = false;

        do {
            String line = scannerIn.nextLine();

            // Simple command to exit cli prompt
            if (line.toLowerCase().equals("exit")) {
                exit = true;
                break;
            }
            processInput(line, bufferedWriter);
        } while (!exit && scannerIn.hasNext());

        bufferedWriter.close();

        scannerIn.close();
    }
}
