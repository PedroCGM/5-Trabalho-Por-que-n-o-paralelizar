package cadastrocliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientApplication {

    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 4321;

    public ClientApplication() {

    }

    private void startClient() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("===============================");
            System.out.print("Username: ");
            String username = reader.readLine();
            System.out.print("Password: ");
            String password = reader.readLine();
            System.out.println("===============================");
            
            out.writeObject(username);
            out.writeObject(password);
            out.flush();
            
            Thread clientThread = new Thread(new ClientHandler(in, out));
            clientThread.start();
            
        } catch (IOException ex) {
            Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new ClientApplication().startClient();
    }

}
