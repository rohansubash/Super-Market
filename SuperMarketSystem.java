import java.util.*;

class User {
    String email, password, role;
    String name;
    double credit = 1000;
    int loyaltyPoints = 0;
    List<Bill> purchaseHistory = new ArrayList<>();

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

class Product {
    int id;
    String name;
    double price;
    int quantity;

    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

class Bill {
    static int billCounter = 1;
    int billNumber;
    String date;
    Map<Product, Integer> items;
    double totalAmount;

    public Bill(Map<Product, Integer> items, double totalAmount) {
        this.billNumber = billCounter++;
        this.date = new Date().toString();
        this.items = new HashMap<>(items);
        this.totalAmount = totalAmount;
    }
}

public class SuperMarketSystem {
    static Scanner sc = new Scanner(System.in);
    static Map<String, User> users = new HashMap<>();
    static List<Product> products = new ArrayList<>();
    static Map<Product, Integer> cart = new HashMap<>();
    static User currentUser = null;
    static int productIdCounter = 1;

    public static void main(String[] args) {
        seedData();
        showLoginMenu();
    }

    static void seedData() {
        users.put("admin@example.com", new User("Admin", "admin@example.com", "admin123", "admin"));
        users.put("cust@example.com", new User("Customer", "cust@example.com", "cust123", "customer"));
    }

    static void showLoginMenu() {
        System.out.println("--- Welcome to Super Market System ---");
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String pwd = sc.nextLine();

        User user = users.get(email);
        if (user != null && user.password.equals(pwd)) {
            currentUser = user;
            if (user.role.equals("admin")) adminMenu();
            else customerMenu();
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    
    static void adminMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Product\n2. Modify Product\n3. Delete Product\n4. View Products\n5. Search Product\n6. Add User\n7. Reports\n8. Logout");
            int choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> modifyProduct();
                case 3 -> deleteProduct();
                case 4 -> viewProducts();
                case 5 -> searchProduct();
                case 6 -> addUser();
                case 7 -> showReports();
                case 8 -> {
                    currentUser = null; return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    static void addProduct() {
        System.out.print("Product name: "); String name = sc.nextLine();
        System.out.print("Price: "); double price = sc.nextDouble();
        System.out.print("Quantity: "); int qty = sc.nextInt(); sc.nextLine();
        products.add(new Product(productIdCounter++, name, price, qty));
        System.out.println("Product added.");
    }

    static void modifyProduct() {
        viewProducts();
        System.out.print("Enter Product ID to modify: ");
        int id = sc.nextInt(); sc.nextLine();
        for (Product p : products) {
            if (p.id == id) {
                System.out.print("New Name: "); p.name = sc.nextLine();
                System.out.print("New Price: "); p.price = sc.nextDouble();
                System.out.print("New Quantity: "); p.quantity = sc.nextInt(); sc.nextLine();
                System.out.println("Product updated."); return;
            }
        }
        System.out.println("Product not found.");
    }

    static void deleteProduct() {
        viewProducts();
        System.out.print("Enter Product ID to delete: ");
        int id = sc.nextInt(); sc.nextLine();
        products.removeIf(p -> p.id == id);
        System.out.println("Product deleted.");
    }

    static void viewProducts() {
        System.out.println("\n--- Product List ---");
        for (Product p : products) {
            System.out.printf("ID: %d | Name: %s | Price: %.2f | Qty: %d\n", p.id, p.name, p.price, p.quantity);
        }
    }

    static void searchProduct() {
        System.out.print("Enter product name to search: ");
        String name = sc.nextLine();
        products.stream().filter(p -> p.name.equalsIgnoreCase(name))
                .forEach(p -> System.out.printf("ID: %d | Name: %s | Price: %.2f | Qty: %d\n", p.id, p.name, p.price, p.quantity));
    }

    static void addUser() {
        System.out.print("Enter Name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pwd = sc.nextLine();
        System.out.print("Role (admin/customer): "); String role = sc.nextLine();
        users.put(email, new User(name, email, pwd, role));
        System.out.println("User added.");
    }

    static void showReports() {
        System.out.println("--- Reports ---");
        System.out.println("1. Products with low quantity (<5)");
        products.stream().filter(p -> p.quantity < 5).forEach(p ->
                System.out.printf("%s - Qty: %d\n", p.name, p.quantity));
    }

    
    static void customerMenu() {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. View Products\n2. Add to Cart\n3. View Cart\n4. Checkout\n5. Purchase History\n6. Logout");
            int choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1 -> viewProducts();
                case 2 -> addToCart();
                case 3 -> viewCart();
                case 4 -> checkout();
                case 5 -> viewPurchaseHistory();
                case 6 -> {
                    currentUser = null; return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    static void addToCart() {
        viewProducts();
        System.out.print("Enter Product ID: "); int id = sc.nextInt();
        System.out.print("Quantity: "); int qty = sc.nextInt(); sc.nextLine();
        for (Product p : products) {
            if (p.id == id && p.quantity >= qty) {
                cart.put(p, cart.getOrDefault(p, 0) + qty);
                System.out.println("Added to cart."); return;
            }
        }
        System.out.println("Product not found or insufficient quantity.");
    }

    static void viewCart() {
        System.out.println("\n--- Cart ---");
        cart.forEach((p, q) -> System.out.printf("%s x %d = %.2f\n", p.name, q, p.price * q));
    }

    static void checkout() {
        double total = cart.entrySet().stream().mapToDouble(e -> e.getKey().price * e.getValue()).sum();
        if (total > currentUser.credit) {
            System.out.println("Insufficient credit!"); return;
        }

        currentUser.credit -= total;

        
        if (total >= 5000) {
            currentUser.credit += 100;
        } else {
            currentUser.loyaltyPoints += total / 100;
            if (currentUser.loyaltyPoints >= 50) {
                currentUser.credit += 100;
                currentUser.loyaltyPoints -= 50;
                System.out.println("You earned Rs.100 cashback for loyalty!");
            }
        }

        Bill bill = new Bill(cart, total);
        currentUser.purchaseHistory.add(bill);

        cart.forEach((p, q) -> p.quantity -= q);
        cart.clear();
        System.out.println("Checkout successful. Bill No: " + bill.billNumber);
    }

    static void viewPurchaseHistory() {
        for (Bill b : currentUser.purchaseHistory) {
            System.out.println("Bill No: " + b.billNumber + " | Date: " + b.date + " | Total: " + b.totalAmount);
        }
    }
}
