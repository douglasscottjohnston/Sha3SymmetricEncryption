import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    private static Sha_3 mySha = new Sha_3();

    /**
     * -H: Computes a plain cryptographic hash of the given file and writes it to output.txt (A passphrase is not required for this flag to work)
     * -T: Computes an authentication tag of the given file and writes it to output.txt (A passphrase is REQUIRED to be entered after the flag for this to work)
     * -S: Does symmetric encryption and decryption of the given file and writes it to output.txt (A passphrase is REQUIRED to be entered after the flag for this to work)
     * -KP: Generate an elliptic key pair from a given passphrase and write the public key to a file.
     * -PKF: Encrypt a data file under a given elliptic public key file and write the ciphertext to a file.
     * -EE: Decrypt a given elliptic-encrypted file from a given password and write the decrypted data to a file.
     * -SF: Sign a given file from a given password and write the signature to a file.
     * -V: Verify a given data file and its signature file under a given public key file.
     */
    private static ArrayList<String> flags = new ArrayList<>(List.of(new String[]{"-H", "-T", "-S", "-KP", "-PKF", "-EE", "-SF", "-V"}));
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
            } else if (passphrase == null && flag.equals("-PKF")) {
                throw new IllegalArgumentException(flag + " was called with no public key file provided");
            } else if (passphrase == null) {
                throw new IllegalArgumentException(flag + " was called with no passphrase provided");
            } else {
                switch (flag) {
                    case "-T":
                        fileWriter.write("Authentication tag: ");
                        fileWriter.write(mySha.authenticationTag(inputFile, passphrase));
                        fileWriter.write("\n");
                        fileWriter.write("Passphrase: ");
                        fileWriter.write(passphrase);
                    case "-S":
                        fileWriter.write("Encrypted: ");
                        SymmetricCryptogram encrpyted = mySha.encrypt(inputFile, passphrase);
                        fileWriter.write(encrpyted.toString());
                        fileWriter.write("\n");
                        fileWriter.write("Decrpyted: ");
                        fileWriter.write(mySha.decrypt(encrpyted, passphrase));
                        fileWriter.write("\n");
                        fileWriter.write("Passphrase: ");
                        fileWriter.write(passphrase);
                    case "-KP":
                        System.out.println(flag);
                    case "-PKF":
                        System.out.println(flag);
                    case "-EE":
                        System.out.println(flag);
                    case "-SF":
                        System.out.println(flag);
                    case "-V":
                        System.out.println(flag);
                }
//                if (flag.equals("-T")) {
//                    fileWriter.write("Authentication tag: ");
//                    fileWriter.write(mySha.authenticationTag(inputFile, passphrase));
//                    fileWriter.write("\n");
//                    fileWriter.write("Passphrase: ");
//                    fileWriter.write(passphrase);
//                } else if (flag.equals("-s")) {
//                    fileWriter.write("Encrypted: ");
//                    SymmetricCryptogram encrpyted = mySha.encrypt(inputFile, passphrase);
//                    fileWriter.write(encrpyted.toString());
//                    fileWriter.write("\n");
//                    fileWriter.write("Decrpyted: ");
//                    fileWriter.write(mySha.decrypt(encrpyted, passphrase));
//                    fileWriter.write("\n");
//                    fileWriter.write("Passphrase: ");
//                    fileWriter.write(passphrase);
//                }
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
