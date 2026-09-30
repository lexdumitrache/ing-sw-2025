package Networking;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Utility class for networking operations.
 * Provides methods to retrieve command line options and select network interfaces.
 */
public class Utils {


    /**
     * Gets the position of a command line option in the provided arguments.
     *
     * @param option The command line option to search for.
     * @param args   The array of command line arguments.
     * @return The index of the option in the args array, or -1 if not found.
     */
    public static int getPosition(String option, String[] args){
        for(int i = 0; i < args.length; i++){
            if(args[i].equalsIgnoreCase(option)){
                return i;
            }
        }

        return -1;
    }

    /**
     * Retrieves the value of a command line option from the provided arguments.
     *
     * @param option The command line option to search for.
     * @param args   The array of command line arguments.
     * @return The value associated with the option, or null if not found or no value is provided.
     */
    public static String getOption(String option, String[] args) {
        int i;
        if((i = getPosition(option, args)) != -1 && i + 1 < args.length){
            return args[i + 1];
        }

        return null;
    }


    /**
     * Selects a network interface based on command line arguments or user input.
     * If no specific option is provided, it lists available network interfaces and prompts the user to select one.
     *
     * @param args The command line arguments.
     * @return The selected network interface address as a string, or "localhost" if no valid selection is made.
     */
    public static String NetworkSelector(String[] args) {
        final String option = getOption("--hostname", args);
        if(option != null) {
            return option;
        }

        final Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return "localhost";
        }

        final Map<NetworkInterface, String[]> activeInterfaces = new HashMap<>();

        while (interfaces.hasMoreElements()) {
            final NetworkInterface intf = interfaces.nextElement();

            try {
                if (intf.isUp() && !intf.isLoopback() && !intf.isVirtual()) {
                    List<String> addresses = new ArrayList<>();

                    Enumeration<InetAddress> rawAddresses = intf.getInetAddresses();

                    while (rawAddresses.hasMoreElements()) {
                        InetAddress addr = rawAddresses.nextElement();

                        addresses.add(addr.getHostAddress());
                    }
                    if (!addresses.isEmpty()) {
                        activeInterfaces.put(intf, addresses.toArray(new String[0]));
                    }
                }
            } catch (SocketException ignored) {}
        }

        NetworkInterface selected = null;
        Set<NetworkInterface> interfacesSet = activeInterfaces.keySet();

        if (interfacesSet.size() == 1) {
            selected = interfacesSet.iterator().next();
        } else {
            for (NetworkInterface intf : interfacesSet) {
                System.out.print("Do you want to use interface " + intf.getName() + " a.k.a " + intf.getDisplayName() + " (");
                for (String address : activeInterfaces.get(intf)) {
                    System.out.print(address + " ");
                }
                System.out.print("\b)? (y/n) ");
                String answer = System.console().readLine();
                if (answer.toLowerCase().startsWith("y")) {
                    selected = intf;
                    break;
                }
            }
        }

        if (selected == null) {
            return "localhost";
        }

        if (activeInterfaces.get(selected).length == 1) {
            return activeInterfaces.get(selected)[0];
        } else {
            for (String address : activeInterfaces.get(selected)) {
                System.out.print("Do you want to use address " + address + "? (y/n) ");
                String answer = System.console().readLine();
                if (answer.toLowerCase().startsWith("y")) {
                    return address;
                }
            }
        }

        return "localhost";
    }
}
