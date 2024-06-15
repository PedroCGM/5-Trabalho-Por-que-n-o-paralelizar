
package inventoryserver;

import inventoryserver.controller.UserController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class InventoryServerV2 {

    private static final int PORT = 4321;

    public InventoryServerV2() {
    }

    private void run() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventoryServerPU");
        UserController userCtrl = new UserController(emf);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("======== SERVER CONNECTED - PORT " + PORT + " ========");
            while (true) {
                Socket socket = serverSocket.accept();
                InventoryThread clientThread = new InventoryThread(userCtrl, socket);
                clientThread.start();
                System.out.println("======== Thread started ========");
            }
        } catch (IOException ex) {
            Logger.getLogger(InventoryServerV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new InventoryServerV2().run();
    }
}
