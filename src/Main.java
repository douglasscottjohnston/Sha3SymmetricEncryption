import java.io.File;
import java.util.*;

public class Main {
    private static File file;
    private static Map<String, Object> argMap = Map.of("-f", false, "-tag", false, "-hash", false, "-encrypt", false, "-decrypt", false);


    public static void main(String[] args) {
        if(args.length > 0) {
            mapArgs(new ArrayList<String>(List.of(args)));
            handleCommandLineArgs();
        } else {
            handleCommandLineInput();
        }
    }

    private static void mapArgs(ArrayList<String> args) {
        String startFlag = args.get(0);
        if(args.size() == 1) { //the start flag is the file path
            argMap.put("-f", startFlag);
            return;
        }

        for (String arg : args) {
            if(argMap.containsKey(arg) && (argMap.containsKey(args.get(args.indexOf(arg) + 1).toLowerCase())
                    || args.indexOf(arg) == args.size() - 1)) { //boolean flag
                argMap.replace(arg, true);
            } else if(argMap.containsKey(arg)) { //string input
                argMap.replace(arg, args.get(args.indexOf(arg) + 1));
            }
        }
    }

    private static void handleCommandLineArgs() {
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            switch (entry.getKey()) {
                case "-f", "-F":
                    file = new File((String)entry.getValue());
                case "-at":

            }
        }
    }

    private static void handleCommandLineInput() {

    }
}
