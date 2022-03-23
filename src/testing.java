import java.util.Scanner;

public class testing {
    private String inputText;
    public testing() {
        this.inputText = "";
        setInputText();
    }

    private void setInputText(){
        Scanner scanner = new Scanner(System.in);
        this.inputText = scanner.nextLine();
        System.out.println("input is " + this.inputText);
    }
}
