package inventoryserver;

import inventoryserver.controller.MovementController;
import inventoryserver.controller.PersonController;
import inventoryserver.controller.ProductController;
import inventoryserver.controller.UserController;
import inventoryserver.model.Product;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class InventoryServer {
    
    private static final int PORT = 4321;
    
    public InventoryServer() {
        
    }
    
    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("======== SERVER CONNECTED - PORT " + PORT + " ========");
            // Initialize controllers
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventoryServerPU");
            ProductController productController = new ProductController(emf);
            MovementController movementController = new MovementController(emf);
            PersonController personController = new PersonController(emf);
            UserController userController = new UserController(emf);
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");
                ClientHandler clientHandler = new ClientHandler(socket, productController, movementController, personController, userController);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            Logger.getLogger(InventoryServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void main(String[] args) {
        new InventoryServer().run();
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final ProductController productController;
        private final UserController userController;

        public ClientHandler(Socket socket, ProductController productController, MovementController movementController, PersonController personController, UserController userController) {
            this.socket = socket;
            this.productController = productController;
            this.userController = userController;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // Authentication
                String username = in.readLine();
                String password = in.readLine();
                if (validateCredentials(username, password)) {
                    out.println("Authentication successful. Awaiting commands...");
                    boolean exitSignal = false;
                    while (!exitSignal) {
                        String command = in.readLine();
                        if (command != null) {
                            switch (command) {
                                case "L": sendProductList(out); break; // Send product list from database
                                case "S": exitSignal = true; break; // Command to exit
                                default: break;
                            }
                        }
                    }
                } else {
                    try (socket) {
                        out.println("Invalid credentials. Connection closed.");
                    }
                }
            } catch (IOException e) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            } catch (Exception e) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        private boolean validateCredentials(String username, String password) {
            return userController.validateUser(username, password) != null;
        }

        private void sendProductList(PrintWriter out) {
            List<Product> productList = productController.findProductEntities();
            out.println("Available product list:");
            for (Product product : productList) {
                out.println(product.getName());
            }
            out.println();
        }
    }
}
