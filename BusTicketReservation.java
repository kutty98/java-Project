package ticket;

import java.sql.*;
import java.util.Scanner;

public class BusTicketReservation {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/busticketreservation";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Kutty@97";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                // Establishing connection
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

                boolean loggedIn = false;
                int userId = -1;
                while (!loggedIn) {
                    // Login
                    userId = login(connection);
                    if (userId != -1) {
                        loggedIn = true;
                    } else {
                        System.out.println("Login failed! Please enter correct Username and Password.");
                    }
                }

                // Display options after successful login
                while (true) {
                    // Display available options
                    displayOptions();

                    Scanner scanner = new Scanner(System.in);
                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            displayAvailableBuses(connection);
                            break;
                        case 2:
                            System.out.println("Enter new bus details:");
                            createBus(connection, scanner);
                            break;
                        case 3:
                            System.out.println("Enter BusID to update:");
                            int busIdToUpdate = scanner.nextInt();
                            System.out.println("Enter new capacity:");
                            int newCapacity = scanner.nextInt();
                            scanner.nextLine(); // Consume newline
                            System.out.println("Enter new departure location:");
                            String newDepartureLocation = scanner.nextLine();
                            System.out.println("Enter new arrival location:");
                            String newArrivalLocation = scanner.nextLine();
                            updateBus(connection, busIdToUpdate, newCapacity, newDepartureLocation, newArrivalLocation);
                            break;
                        case 4:
                            System.out.println("Enter BusID to delete:");
                            int busIdToDelete = scanner.nextInt();
                            deleteBus(connection, busIdToDelete);
                            break;
                        case 5:
                            System.out.println("Exiting...");
                            connection.close();
                            return; // Exit the program
                        default:
                            System.out.println("Invalid choice.");
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void displayOptions() {
        System.out.println("Options:");
        System.out.println("1. Display available buses");
        System.out.println("2. Add a new bus");
        System.out.println("3. Update a bus");
        System.out.println("4. Delete a bus");
        System.out.println("5. Exit");
        System.out.println("Enter your choice:");
    }

    private static int login(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PreparedStatement statement = connection.prepareStatement("SELECT UserID FROM User WHERE Username = ? AND Password = ?");
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("UserID");
        }
        return -1;
    }

    private static void displayAvailableBuses(Connection connection) {
        try {
            System.out.println("Available Buses:");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Bus");
            while (resultSet.next()) {
                System.out.println("BusID: " + resultSet.getInt("BusID") +
                        ", BusNumber: " + resultSet.getString("BusNumber") +
                        ", Capacity: " + resultSet.getInt("Capacity") +
                        ", Departure Location: " + resultSet.getString("DepartureLocation") +
                        ", Arrival Location: " + resultSet.getString("ArrivalLocation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createBus(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Bus Number: ");
            String busNumber = scanner.nextLine();
            System.out.print("Enter Capacity: ");
            int capacity;
            while (true) {
                try {
                    capacity = Integer.parseInt(scanner.nextLine());
                    if (capacity <= 0) {
                        throw new NumberFormatException();
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Capacity must be a positive integer.");
                    System.out.print("Enter Capacity again: ");
                }
            }
            System.out.print("Enter Departure Location: ");
            String departureLocation = scanner.nextLine();
            System.out.print("Enter Arrival Location: ");
            String arrivalLocation = scanner.nextLine();

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Bus (BusNumber, Capacity, DepartureLocation, ArrivalLocation) VALUES (?, ?, ?, ?)");
            statement.setString(1, busNumber);
            statement.setInt(2, capacity);
            statement.setString(3, departureLocation);
            statement.setString(4, arrivalLocation);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new bus was inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateBus(Connection connection, int busId, int newCapacity, String newDepartureLocation, String newArrivalLocation) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE Bus SET Capacity = ?, DepartureLocation = ?, ArrivalLocation = ? WHERE BusID = ?");
            statement.setInt(1, newCapacity);
            statement.setString(2, newDepartureLocation);
            statement.setString(3, newArrivalLocation);
            statement.setInt(4, busId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Bus with ID " + busId + " was updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteBus(Connection connection, int busId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM Bus WHERE BusID = ?");
            statement.setInt(1, busId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Bus with ID " + busId + " was deleted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
