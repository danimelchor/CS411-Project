import java.util.ArrayList;
import java.util.Scanner;

public class CustomScanner {
  // Custom scanner class to run tests or cli input

  Scanner scanner;
  ArrayList<String> answers;

  public CustomScanner(Scanner _scanner) {
    scanner = _scanner;
  }

  public CustomScanner(ArrayList<String> _answers) {
    answers = _answers;
  }

  public int nextInt() {
    if(answers != null) {
      return Integer.parseInt(answers.remove(0));
    } else {
      return scanner.nextInt();
    }
  }

  public double nextDouble() {
    if(answers != null) {
      return Double.parseDouble(answers.remove(0));
    } else {
      return scanner.nextDouble();
    }
  }

  public String next() {
    if(answers != null) {
      return answers.remove(0);
    } else {
      return scanner.next();
    }
  }
}
