
package cadastrocliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientApplication {

    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 4321;

    public ClientApplication() {

    }

    private void run() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to CadastroServer.");
            System.out.print("Enter your username: ");
            String username = consoleIn.readLine();
            System.out.print("Enter your password: ");
            String password = consoleIn.readLine();
            
            out.println(username);
            out.println(password);
            
            String response = serverIn.readLine();
            System.out.println(response);

            if (response.equals("Authentication successful. Waiting for commands...")) {
                boolean exitChoice = false;
                while (!exitChoice) {
                    System.out.print("Enter 'L' to list products or 'S' to exit: ");
                    String command = consoleIn.readLine().toUpperCase();
                    out.println(command);

                    switch (command) {
                        case "S":
                            exitChoice = true;
                            break;
                        case "L":
                            receiveAndDisplayProductList(serverIn);
                            break;
                        default:
                            System.out.println("Invalid option!");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ClientApplication.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void receiveAndDisplayProductList(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        new ClientApplication().run();
    }

}
