package io.frghackers.messenger.client;

public class Main {

    public static void main(String[] args) {
        if(args.length<2) {
            new MessengerClientGUI();
        }else {
            try {
                new MessengerClientGUI(args[0], Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Port has to be a number");
            }
        }
    }

}
