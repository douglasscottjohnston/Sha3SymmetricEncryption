import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static Sha_3 mySha = new Sha_3();

    private static ArrayList<String> flags = new ArrayList<>(List.of(new String[]{"-H", "-T", "-S"}));
    private static ArrayList<String> options = new ArrayList<>(List.of(new String[]{"H", "T", "S", "Q"}));
    private static final String OUTPUT_FILE_NAME = "output.txt";


    public static void main(String[] args) {
        if (args.length > 0) {
            handleCommandLineArgs(args);
        } else {
            handleCommandLineInput();
        }
        System.out.println();
    }

    private static void handleCommandLineArgs(String[] args) {
        File inputFile = new File(args[0]);
        File outputFile = new File(OUTPUT_FILE_NAME);
        String flag = "-H";
        String passphrase = null;

        if (args.length > 1) {
            flag = args[1];
        }

        if (args.length > 2) {
            passphrase = args[2];
        }

        try {
            FileWriter fileWriter = new FileWriter(outputFile);

            if (!flags.contains(flag)) {
                throw new IllegalArgumentException("These are the only accepted flags: " + flags);
            }

            if (flag.equals("-H")) {
                fileWriter.write(mySha.hash(inputFile));
            } else if (passphrase == null) {
                throw new IllegalArgumentException(flag + " was called with no passphrase provided");
            } else {
                if (flag.equals("-T")) {
                    fileWriter.write("Authentication tag: ");
                    fileWriter.write(mySha.authenticationTag(inputFile, passphrase));
                    fileWriter.write("\n");
                    fileWriter.write("Passphrase: ");
                    fileWriter.write(passphrase);
                } else {
                    fileWriter.write("Encrypted: ");
                    SymmetricCryptogram encrpyted = mySha.encrypt(inputFile, passphrase);
                    fileWriter.write(encrpyted.toString());
                    fileWriter.write("\n");
                    fileWriter.write("Decrpyted: ");
                    fileWriter.write(mySha.decrypt(encrpyted, passphrase));
                    fileWriter.write("\n");
                    fileWriter.write("Passphrase: ");
                    fileWriter.write(passphrase);
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("output file not found");
            e.printStackTrace();
        }
    }

    private static void handleCommandLineInput() {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        while (running) {

            System.out.println("Type 'H' for cryptographic hash, 'T' for authentication tag, 'S' to symmetrically encrypt, or Q to quit");
            String option = scanner.nextLine();

            if (!options.contains(option)) {
                System.out.println("You must type one of these options:" + options);
                continue;
            }

            if (option.equals("Q")) {
                running = false;
                continue;
            }

            System.out.println("Enter the data to hash:");
            String data = scanner.nextLine();

            if (option.equals("H")) {
                System.out.println("The hashed data:");
                System.out.println(mySha.hash(data));
            } else {
                System.out.println("Enter the passphrase:");
                String passphrase = scanner.nextLine();

                if (option.equals("T")) {
                    System.out.println("The authentication tag:");
                    System.out.println(mySha.authenticationTag(data, passphrase));
                } else {
                    System.out.println("The symmetrically encrypted data:");
                    SymmetricCryptogram encrypted = mySha.encrypt(data, passphrase);
                    System.out.println(encrypted);
                    System.out.println("The decrypted data:");
                    System.out.println(mySha.decrypt(encrypted, passphrase));
                }
            }
            System.out.println();
        }
    }
}
