
package inventoryserver;

import inventoryserver.controller.MovementController;
import inventoryserver.controller.PersonController;
import inventoryserver.controller.ProductController;
import inventoryserver.controller.UserController;
import inventoryserver.controller.exceptions.NonexistentEntityException;
import inventoryserver.model.Movement;
import inventoryserver.model.Person;
import inventoryserver.model.Product;
import inventoryserver.model.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CommandHandler {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("InventoryServerPU");

    private final MovementController movementCtrl;
    private final PersonController personCtrl;
    private final ProductController productCtrl;
    private final UserController userCtrl;

    public CommandHandler(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;

        this.movementCtrl = new MovementController(emf);
        this.personCtrl = new PersonController(emf);
        this.productCtrl = new ProductController(emf);
        this.userCtrl = new UserController(emf);
    }

    public void executeCommands() throws IOException, ClassNotFoundException {
        try (out) {
            while (true) {
                String command = (String) in.readObject();
                command = command.toUpperCase();
                Integer personId;
                Integer userId;
                Integer productId;
                Integer quantity;
                Float unitPrice;

                Person person;
                Product product;
                User user;

                Movement movement;

                switch (command) {
                    case "E" -> {
                        personId = Integer.valueOf((String) in.readObject());
                        productId = Integer.valueOf((String) in.readObject());
                        userId = (Integer) in.readObject();
                        quantity = Integer.valueOf((String) in.readObject());
                        unitPrice = Float.valueOf((String) in.readObject());

                        person = personCtrl.findPerson(personId);
                        product = productCtrl.findProduct(productId);
                        user = userCtrl.findUser(userId);

                        if (product == null) {
                            System.out.println("Product not registered in the database.");
                            continue;
                        }

                        movement = new Movement();
                        movement.setPersonId(person);
                        movement.setProductId(product);
                        movement.setQuantity(quantity);
                        movement.setUserId(user);
                        movement.setType("E");
                        movement.setUnitPrice(unitPrice);

                        product.setQuantity(product.getQuantity() + quantity);
                        productCtrl.edit(product);

                        movementCtrl.create(movement);
                    }

                    case "S" -> {
                        personId = Integer.valueOf((String) in.readObject());
                        productId = Integer.valueOf((String) in.readObject());
                        userId = (Integer) in.readObject();
                        quantity = Integer.valueOf((String) in.readObject());
                        unitPrice = Float.valueOf((String) in.readObject());

                        person = personCtrl.findPerson(personId);
                        product = productCtrl.findProduct(productId);
                        user = userCtrl.findUser(userId);

                        if (product == null) {
                            System.out.println("Product not registered!");
                            continue;
                        }

                        movement = new Movement();
                        movement.setPersonId(person);
                        movement.setProductId(product);
                        movement.setQuantity(quantity);
                        movement.setUserId(user);
                        movement.setType("S");
                        movement.setUnitPrice(unitPrice);

                        product.setQuantity(product.getQuantity() - quantity);
                        productCtrl.edit(product);

                        movementCtrl.create(movement);
                    }
                    case "L" -> {
                        List<Product> productList = productCtrl.findProductEntities();

                        ArrayList<String> productNames = new ArrayList<>();
                        ArrayList<Integer> productQuantities = new ArrayList<>();

                        for (Product item : productList) {
                            productNames.add(item.getName());
                            productQuantities.add(item.getQuantity());
                        }
                        out.writeObject(productNames);
                        out.writeObject(productQuantities);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CommandHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CommandHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            in.close();
        }
    }
}
