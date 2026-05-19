package org.example.server;

import org.example.dao.AppointmentDAO;
import org.example.dao.CarDAO;
import org.example.dao.CustomerDAO;
import org.example.model.Appointment;
import org.example.model.Car;
import org.example.model.Customer;
import org.example.util.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final int clientId;

    private final CustomerDAO    customerDAO    = new CustomerDAO();
    private final CarDAO         carDAO         = new CarDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public ClientHandler(Socket socket, int clientId) {
        this.clientSocket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        System.out.println("[Server] Client #" + clientId + " connected: "
                + clientSocket.getInetAddress());

        // IO Streams — reading from and writing to the client over the network
        try (BufferedReader in  = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter   out  = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[Client #" + clientId + "] >> " + line);
                String response = handleCommand(line);
                out.println(response);

                if (line.trim().equalsIgnoreCase(Protocol.EXIT)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] Client #" + clientId + " error: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("[Server] Client #" + clientId + " disconnected.");
        }
    }

    /**
     * Parses the command and dispatches to the correct DAO method.
     * Protocol format: COMMAND|param1|param2|...
     */
    private String handleCommand(String rawCommand) {
        String[] parts = rawCommand.split(Protocol.REGEX_DELIMITER);
        String command = parts[0].trim().toUpperCase();

        try {
            switch (command) {

                // ADD_CUSTOMER|name|phone|email
                case Protocol.ADD_CUSTOMER: {
                    if (parts.length < 4)
                        return Protocol.ERROR + "|Missing fields. Usage: ADD_CUSTOMER|name|phone|email";
                    Customer c = new Customer(0, parts[1], parts[2], parts[3]);
                    boolean ok = customerDAO.addCustomer(c);
                    return ok ? Protocol.SUCCESS + "|Customer added successfully."
                            : Protocol.ERROR   + "|Failed to add customer.";
                }

                // REMOVE_CUSTOMER|customerId
                case Protocol.REMOVE_CUSTOMER: {
                    if (parts.length < 2)
                        return Protocol.ERROR + "|Missing fields. Usage: REMOVE_CUSTOMER|customerId";
                    int id = Integer.parseInt(parts[1]);
                    boolean ok = customerDAO.removeCustomer(id);
                    return ok ? Protocol.SUCCESS + "|Customer removed successfully."
                            : Protocol.ERROR   + "|Customer not found or could not be removed.";
                }

                // ADD_CAR|customerId|make|model|plateNumber
                case Protocol.ADD_CAR: {
                    if (parts.length < 5)
                        return Protocol.ERROR + "|Missing fields. Usage: ADD_CAR|customerId|make|model|plate";
                    Car car = new Car(0, Integer.parseInt(parts[1]), parts[2], parts[3], parts[4]);
                    boolean ok = carDAO.addCar(car);
                    return ok ? Protocol.SUCCESS + "|Car added successfully."
                            : Protocol.ERROR   + "|Failed to add car.";
                }

                // BOOK_APPOINTMENT|carId|date|description
                case Protocol.BOOK_APPOINTMENT: {
                    if (parts.length < 4)
                        return Protocol.ERROR + "|Missing fields. Usage: BOOK_APPOINTMENT|carId|date|description";
                    Appointment appt = new Appointment(0, Integer.parseInt(parts[1]),
                            parts[2], "Pending", parts[3]);
                    boolean ok = appointmentDAO.addAppointment(appt);
                    return ok ? Protocol.SUCCESS + "|Appointment booked successfully."
                            : Protocol.ERROR   + "|Failed to book appointment.";
                }

                // UPDATE_STATUS|appointmentId|newStatus
                case Protocol.UPDATE_STATUS: {
                    if (parts.length < 3)
                        return Protocol.ERROR + "|Missing fields. Usage: UPDATE_STATUS|appointmentId|status";
                    int apptId = Integer.parseInt(parts[1]);
                    String newStatus = parts[2];
                    boolean ok = appointmentDAO.updateStatus(apptId, newStatus);
                    if (ok) {
                        // Save notification to DB
                        appointmentDAO.addNotification(apptId,
                                "Status updated to: " + newStatus);
                        return Protocol.SUCCESS + "|Status updated. Notification saved.";
                    }
                    return Protocol.ERROR + "|Appointment not found or update failed.";
                }

                // VIEW_APPOINTMENTS
                case Protocol.VIEW_APPOINTMENTS: {
                    List<Appointment> list = appointmentDAO.getAllAppointments();
                    if (list.isEmpty()) return Protocol.DATA + "|No appointments found.";
                    StringBuilder sb = new StringBuilder(Protocol.DATA + "|");
                    for (Appointment a : list) {
                        sb.append(String.format("\n  [#%d] Date: %s | Status: %-11s | %s",
                                a.getId(), a.getAppointmentDate(),
                                a.getStatus(), a.getDescription()));
                    }
                    return sb.toString();
                }

                // VIEW_CUSTOMERS
                case Protocol.VIEW_CUSTOMERS: {
                    List<Customer> list = customerDAO.getAllCustomers();
                    if (list.isEmpty()) return Protocol.DATA + "|No customers found.";
                    StringBuilder sb = new StringBuilder(Protocol.DATA + "|");
                    for (Customer c : list) {
                        sb.append(String.format("\n  [#%d] %s | Phone: %s | Email: %s",
                                c.getId(), c.getName(), c.getPhone(), c.getEmail()));
                    }
                    return sb.toString();
                }

                // VIEW_CARS
                case Protocol.VIEW_CARS: {
                    List<Car> list = carDAO.getAllCars();
                    if (list.isEmpty()) return Protocol.DATA + "|No cars found.";
                    StringBuilder sb = new StringBuilder(Protocol.DATA + "|");
                    for (Car c : list) {
                        sb.append(String.format("\n  [#%d] %s %s | Plate: %s | CustomerId: %d",
                                c.getId(), c.getMake(), c.getModel(),
                                c.getPlateNumber(), c.getCustomerId()));
                    }
                    return sb.toString();
                }

                case Protocol.EXIT:
                    return Protocol.SUCCESS + "|Goodbye!";

                default:
                    return Protocol.ERROR + "|Unknown command: " + command;
            }

        } catch (NumberFormatException e) {
            return Protocol.ERROR + "|Invalid number format: " + e.getMessage();
        } catch (Exception e) {
            return Protocol.ERROR + "|Server error: " + e.getMessage();
        }
    }
}
