import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.io.IOException;
import org.json.JSONObject;


public class CurrencyConverter extends JFrame {
    // API Setup
    private static final String API_KEY = "595fab43937e427e317fe6e7";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    // Constructor to setup the GUI
    public CurrencyConverter() {
        // Create JFrame and set the properties
        JFrame frame = new JFrame("Currency Convertor");
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

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

        // Test Field for amount input

        // note: schauen, dass man nur valide Zahlen eingeben kann und nicht alles
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

                    // Add the chart note: not finished yet just a space holder

                    JLabel chart = new JLabel("Hello world");
                    chart.setBounds(100,300,100,30);
                    frame.add(chart);

                    frame.revalidate();
                    frame.repaint();

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
            System.out.println("API response" + jasonString);

            // Parse the JSON response

            JSONObject jsonObject = new JSONObject(jasonString);

            // Get the conversion rates objects from the JSON
            JSONObject conversionRates = jsonObject.getJSONObject("conversion_rates");

            // Get the rate for the target currency
            double fromRate = conversionRates.getDouble(from);
            double toRate = conversionRates.getDouble(to);

            // Convert the amount

            return (amount/fromRate) *toRate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        
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
