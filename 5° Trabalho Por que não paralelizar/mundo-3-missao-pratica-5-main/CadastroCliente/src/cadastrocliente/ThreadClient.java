
package cadastrocliente;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ThreadClient extends Thread {

    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    private JTextArea textArea;
    private JFrame frame;
    private ArrayList<String> output;

    public ThreadClient() {

    }

    public ThreadClient(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        output = new ArrayList<>();
        try {
            Boolean validate = (Boolean) in.readObject();
            Integer idUsuario = (Integer) in.readObject();

            if (validate) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                String command;
                String idPerson;
                String idProduct;
                String quantity;
                String unitPrice;

                do {
                    System.out.print("""
                                     ======= Commands =======
                                     
                                      L - List 
                                      F - Finish 
                                      E - Entry 
                                      S - Exit 
                                     
                                     Enter command: """);
                    command = reader.readLine().toUpperCase();

                    switch (command) {
                        case "E" -> {
                            out.writeObject("E");

                            System.out.println("======== Entry ========");
                            
                            System.out.print("Person ID: ");
                            idPerson = reader.readLine();
                            out.writeObject(idPerson);

                            System.out.print("Product ID: ");
                            idProduct = reader.readLine();
                            out.writeObject(idProduct);

                            System.out.print("User ID: " + idUsuario);
                            out.writeObject(idUsuario);
                            System.out.println("");

                            System.out.print("Quantity: ");
                            quantity = reader.readLine();
                            out.writeObject(quantity);

                            System.out.print("Unit Price: ");
                            unitPrice = reader.readLine();
                            out.writeObject(unitPrice);

                            output.add("Entry successful.\n");
                        }

                        case "S" -> {
                            out.writeObject("S");

                            System.out.println("======== Exit ========");
                            
                            System.out.print("Person ID: ");
                            idPerson = reader.readLine();
                            out.writeObject(idPerson);

                            System.out.print("Product ID: ");
                            idProduct = reader.readLine();
                            out.writeObject(idProduct);

                            System.out.print("User ID: " + idUsuario);
                            out.writeObject(idUsuario);
                            System.out.println("");

                            System.out.print("Quantity: ");
                            quantity = reader.readLine();
                            out.writeObject(quantity);

                            System.out.print("Unit Price: ");
                            unitPrice = reader.readLine();
                            out.writeObject(unitPrice);

                            output.add("Exit successful.\n");
                        }

                        case "L" -> {
                            out.writeObject("L");
                            try {
                                ArrayList<String> productList = (ArrayList<String>) in.readObject();
                                ArrayList<Integer> productQuantity = (ArrayList<Integer>) in.readObject();
                                if (frame == null || !frame.isVisible()) {
                                    frame = new JFrame("Server Response");
                                    frame.setSize(400, 600);
                                    textArea = new JTextArea(20, 50);
                                    textArea.setEditable(false);
                                    frame.add(new JScrollPane(textArea));
                                    frame.pack();
                                    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                    frame.setVisible(true);
                                    frame.setVisible(true);
                                    SwingUtilities.invokeLater(() -> {
                                        output.add("Product List:\n");
                                        for (int i = 0; i < productList.size(); i++) {
                                            output.add(productList.get(i) + " " + productQuantity.get(i) + "\n");
                                        }
                                        for (String line : output) {
                                            textArea.append(line);
                                        }
                                        textArea.setCaretPosition(textArea.getDocument().getLength());
                                    });
                                } else {
                                    frame.setVisible(false);
                                }

                            } catch (ClassNotFoundException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                        case "F" -> {
                            out.writeObject("F");
                            System.out.println("======== Program finished ========");
                        }

                        default -> System.out.println("Invalid option. Choose again.");
                    }

                } while (!"f".equalsIgnoreCase(command));

            } else {
                System.out.println("User or password incorrect!");
            }

        } catch (HeadlessException | IOException | ClassNotFoundException e) {
            if (!(e instanceof java.io.EOFException)) {
                System.out.println("======== Thread Finished ========");
            }
        }
    }
}
