package inventoryserver;

import inventoryserver.controller.UserController;
import inventoryserver.model.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryThread extends Thread {

    private final UserController userCtrl;
    private final Socket socket;

    public InventoryThread(UserController userCtrl, Socket socket) {
        this.userCtrl = userCtrl;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            String login = (String) in.readObject();
            String password = (String) in.readObject();

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(currentDate);
            System.out.println("==== New Communication >> " + formattedDate);

            boolean isValidUser = (validateUser(login, password) != null);

            if (isValidUser) {
                out.writeObject(isValidUser);
                out.writeObject(validateUser(login, password).getIdUser());

                System.out.println("==== User Logged In ====");

                CommandHandler commandHandler = new CommandHandler(out, in);
                commandHandler.executeCommands();

            } else {
                out.writeObject(isValidUser);
                out.writeObject(null);
            }
            out.flush();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(InventoryThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private User validateUser(String login, String password) {
        return userCtrl.validateUser(login, password);
    }
}
