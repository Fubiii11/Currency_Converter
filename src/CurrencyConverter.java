import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CurrencyConverter extends JFrame {
    // API Setup (add API key)
    private static final String API_KEY = "";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    // Constructor to setup the GUI
    public CurrencyConverter() {
        // Create JFrame and set the properties
        JFrame frame = new JFrame("Currency Convertor");
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        // Create the Labelse
        JLabel labelAmount = new JLabel("Amount:");
        labelAmount.setBounds(30, 30, 100, 30);
        frame.add(labelAmount);

        JLabel labelFrom = new JLabel("From:");
        labelFrom.setBounds(30, 60, 100, 30);
        frame.add(labelFrom);

        JLabel labelTo = new JLabel("To:");
        labelTo.setBounds(30, 90, 100, 30);
        frame.add(labelTo);

        // Textfield to input amount
        JTextField textAmount = new JTextField();
        textAmount.setBounds(100, 30, 150, 30);
        frame.add(textAmount);
        
        // box for selecting currencies
        String[] currencies = {"USD", "EUR", "GBP", "INR", "CHF"};

        JComboBox currencyFrom = new JComboBox(currencies);
        currencyFrom.setBounds(100, 60, 150, 30);
        frame.add(currencyFrom);

        JComboBox currencyTo = new JComboBox(currencies);
        currencyTo.setBounds(100, 90, 150, 30);
        frame.add(currencyTo);

        // Button for converting
        JButton buttonConvert  = new JButton("Convert");
        buttonConvert.setBounds(150, 130, 100, 30);
        frame.add(buttonConvert);

        // Label for result
        JLabel labelResult = new JLabel("Result: ");
        labelResult.setBounds(30,160,200,30);
        frame.add(labelResult);
        
        // Button action listener
        buttonConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try {
                    // Get the input values
                    String fromCurrency = (String) currencyFrom.getSelectedItem();
                    String toCurrency = (String) currencyTo.getSelectedItem();
                    double amount = Double.parseDouble(textAmount.getText());

                    // Call the conversion function
                    double convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);

                    // Show the result in the label
                    labelResult.setText("Result: " + convertedAmount + " " + toCurrency);

                } catch (NumberFormatException ex) {
                    // If the amount input is not valid, show an error message
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for the amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);

    }

    // Function to convert currency
    public double convertCurrency(String from, String to, double amount){
        try {
            //Make API call to fetch exchange rate
            String jasonString = getExchangeRates(from);

            // Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(jasonString);

            // Get the conversion rates objects from the JSON
            JSONObject conversionRates = (JSONObject) jo.get("conversion_rates");

            // Get the rate for the target currency
            double toRate = (double) conversionRates.get(to);

            // Convert the amount
            return amount*toRate;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
        
    }

    // Function to make the API call
    public String getExchangeRates(String baseCurrency){
        // URL with the right covert currency
        String urlString = API_URL + baseCurrency;             

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    // run the GUI
    public static void main(String[] args) {
        new CurrencyConverter();
    }

}
