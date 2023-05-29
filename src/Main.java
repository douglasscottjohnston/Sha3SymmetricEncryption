import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Main {
    /**
     * -H: Computes a plain cryptographic hash of the given file and writes it to output.txt (A passphrase is not required for this flag to work)
     * -T: Computes an authentication tag of the given file and writes it to output.txt (A passphrase is REQUIRED to be entered after the flag for this to work)
     * -S: Does symmetric encryption and decryption of the given file and writes it to output.txt (A passphrase is REQUIRED to be entered after the flag for this to work)
     * -KP: Generate an elliptic key pair from a given passphrase and writes the public and private keys to a file.
     * -PKF: Encrypt a data file under a given elliptic public key file and write the ciphertext to a file.
     * -EE: Decrypt a given elliptic-encrypted file from a given password and write the decrypted data to a file.
     * -SF: Sign a given file from a given password and write the signature to a file.
     * -V: Verify a given data file and its signature file under a given public key file.
     */
    private static final ArrayList<String> flags = new ArrayList<>(List.of(new String[]{"-H", "-T", "-S", "-KP", "-PKF", "-EE", "-SF", "-V"}));
    private static final ArrayList<String> options = new ArrayList<>(List.of(new String[]{"H", "T", "S", "PKF", "SF", "Q"}));
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
        File publicKeyFile;
        File signatureFile = null;
        Scanner scanner;
        SymmetricCryptogram encrypted;
        String flag = "-H";
        String passphrase = null;
        BigInteger publicKeyX;
        BigInteger publicKeyY;

        if (args.length > 1) {
            flag = args[1];
        }

        if (args.length > 2) {
            passphrase = args[2];
        }

        if (args.length > 3) {
            signatureFile = new File(args[2]);
            passphrase = args[3];
        }

        try {
            FileWriter fileWriter = new FileWriter(outputFile);

            if (!flags.contains(flag)) {
                throw new IllegalArgumentException("These are the only accepted flags: " + flags);
            }

            if (flag.equals("-H")) {
                fileWriter.write(Sha_3.hash(inputFile));
            } else if (passphrase == null && flag.equals("-PKF")) {
                throw new IllegalArgumentException(flag + " was called with no public key file provided");
            } else if (passphrase == null) {
                throw new IllegalArgumentException(flag + " was called with no passphrase provided");
            } else {
                switch (flag) {
                case "-T":
                    fileWriter.write("Authentication tag:\n");
                    fileWriter.write(Sha_3.authenticationTag(inputFile, passphrase));
                    fileWriter.write("\n");
                    fileWriter.write("Passphrase:\n");
                    fileWriter.write(passphrase);
                    break;
                case "-S":
                    fileWriter.write("Encrypted: ");
                    encrypted = Sha_3.encrypt(inputFile, passphrase);
                    fileWriter.write(encrypted.toString());
                    fileWriter.write("\nDecrypted:\n");
                    fileWriter.write(Sha_3.decrypt(encrypted, passphrase));
                    fileWriter.write("\nPassphrase:\n");
                    fileWriter.write(passphrase);
                    break;
                case "-KP": // inputfile -KP passphrase
                    KeyPair kp = Sha_3.dhiesKeyPair(passphrase);
                    fileWriter.write("Public key:\n");
                    fileWriter.write(kp.getV().toString());
                    fileWriter.write("\nPrivate key:\n");
                    fileWriter.write(kp.getS().toString());
                    break;
                case "-PKF": // inputfile -PKF publickeyfilepath
                    publicKeyFile = new File(passphrase);
                    scanner = new Scanner(publicKeyFile);
                    publicKeyX = new BigInteger(scanner.nextLine().getBytes());
                    publicKeyY = new BigInteger(scanner.nextLine().getBytes());
                    scanner.close();
                    fileWriter.write("Encrypted:\n");
                    encrypted = Sha_3.dhiesEncrypt(inputFile, new Point(publicKeyX, publicKeyY));
                    fileWriter.write(encrypted.toString());
                    break;
                case "-EE": // inputfile -EE passphrase
                    scanner = new Scanner(inputFile);
                    BigInteger cryptogramZX = new BigInteger(scanner.nextLine().getBytes());
                    BigInteger cryptogramZY = new BigInteger(scanner.nextLine().getBytes());
                    byte[] cryptogramC = scanner.nextLine().getBytes();
                    byte[] cryptogramT = scanner.nextLine().getBytes();
                    fileWriter.write("Decrypted:\n");
                    fileWriter.write(Sha_3.dhiesDecrypt(new SymmetricCryptogram(new Point(cryptogramZX, cryptogramZY), cryptogramC, cryptogramT), passphrase));
                    break;
                case "-SF": // inputfile -SF password
                    Signature signature = Sha_3.dhiesSign(inputFile, passphrase);
                    fileWriter.write("Signature:\n");
                    fileWriter.write(signature.toString());
                    break;
                case "-V": // inputfile -V signatureFile publickeyfilepath
                    publicKeyFile = new File(passphrase);
                    scanner = new Scanner(publicKeyFile);
                    publicKeyX = new BigInteger(scanner.nextLine().getBytes());
                    publicKeyY = new BigInteger(scanner.nextLine().getBytes());
                    scanner.close();
                    scanner = new Scanner(signatureFile);
                    BigInteger signatureH = new BigInteger(scanner.nextLine().getBytes());
                    BigInteger signatureZ = new BigInteger(scanner.nextLine().getBytes());
                    scanner.close();
                    fileWriter.write("File: ");
                    fileWriter.write(inputFile.toString());
                    if (Sha_3.dhiesVerify(new Signature(signatureH, signatureZ), inputFile, new Point(publicKeyX, publicKeyY))) {
                        fileWriter.write("\nVERIFIED");
                    } else {
                        fileWriter.write("\nUNVERIFIED");
                    }
                    break;
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
            //"H", "T", "S", "PKF", "SF", "Q"

            System.out.println("Type 'H' for cryptographic hash, 'T' for authentication tag, 'S' to symmetrically encrypt, " +
                    "'PKF' to encrypt input under a given elliptic public key file, 'SF' to sign input, or 'Q' to quit");
            String option = scanner.nextLine();

            if (!options.contains(option)) {
                System.out.println("You must type one of these options:" + options);
                continue;
            }

            ArrayList<String> part1Options = new ArrayList<>(List.of(new String[]{"H", "T", "S"}));
            if (option.equals("Q")) {
                running = false;
            } else if (part1Options.contains(option)) {
                handlePart1Commands(scanner, option);
            } else {
                handlePart2Commands(scanner, option);
            }
        }
        scanner.close();
    }

    private static void handlePart1Commands(Scanner scanner, String option) {
        System.out.println("Enter the data to hash:");
        String data = scanner.nextLine();

        if (option.equals("H")) {
            System.out.println("The hashed data:");
            System.out.println(Sha_3.hash(data));
        } else {
            System.out.println("Enter the passphrase:");
            String passphrase = scanner.nextLine();

            if (option.equals("T")) {
                System.out.println("The authentication tag:");
                System.out.println(Sha_3.authenticationTag(data, passphrase));
            } else {
                System.out.println("The symmetrically encrypted data:");
                SymmetricCryptogram encrypted = Sha_3.encrypt(data, passphrase);
                System.out.println(encrypted);
                System.out.println("The decrypted data:");
                System.out.println(Sha_3.decrypt(encrypted, passphrase));
            }
        }
        System.out.println();
    }

    private static void handlePart2Commands(Scanner scanner, String option) {
        String data;
        File outputFile = new File(OUTPUT_FILE_NAME);
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            switch (option) {
            case "PKF":
                System.out.println("Enter full filepath to the elliptic public key file: ");
                File publicKeyFile = new File(scanner.nextLine());
                System.out.println("Enter the data to encrypt: ");
                data = scanner.nextLine();
                Scanner publicKeyScanner = new Scanner(publicKeyFile);
                BigInteger publicKeyX = publicKeyScanner.nextBigInteger();
                BigInteger publicKeyY = publicKeyScanner.nextBigInteger();
                publicKeyScanner.close();
                fileWriter.write("Encrypted:\n");
                fileWriter.write(Sha_3.dhiesEncrypt(data, new Point(publicKeyX, publicKeyY)).toString());
                System.out.println("Encrypted data written to " + OUTPUT_FILE_NAME);
                break;
            case "SF":
                System.out.println("Enter the password: ");
                String password = scanner.nextLine();
                System.out.println("Enter the data to sign: ");
                data = scanner.nextLine();
                fileWriter.write("Signature:\n");
                fileWriter.write(Sha_3.dhiesSign(data, password).toString());
                System.out.println("Signature written to " + OUTPUT_FILE_NAME);
                break;
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("output file not found");
            e.printStackTrace();
        }
    }
}
