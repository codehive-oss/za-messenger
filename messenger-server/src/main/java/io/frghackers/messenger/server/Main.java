package io.frghackers.messenger.server;

public class Main {

    public static void main(String[] args) {
        if(args.length==0) {
            new MessengerServer();
        }else {
            try {
                new MessengerServer(Integer.parseInt(args[0]));
            }catch (NumberFormatException e) {
                throw new RuntimeException("Port has to be a number");
            }
        }

    }

}
