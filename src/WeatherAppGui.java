import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        super("Weather App");
        //et saad panna kinni ekraani
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //screen size
        setSize(450, 650);
        //kui avad on keskel
        setLocationRelativeTo(null);
        //manuaalse asetsuse panemiseks nullime layouti (paneme ise paika)
        setLayout(null);
        //ei saa resizeda ekraani
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);
        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 24));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        //Weather condition Description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);
        //Humidity image
        JLabel humidityConditonImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityConditonImage.setBounds(15,500,74,66);
        add(humidityConditonImage);
        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);
        //windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);
        //windspeed text
        JLabel windspeedText = new JLabel("<html><b>Wind</b> 15km/h</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);




        //searchbutton
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();
                //validate input - renive whitespace etc
                if (userInput.replaceAll("\\s","").length() <= 0){ //if the user put a bunch of whitespace, if statement catches that
                    return;
                }

                //retrieve weatherdata
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weatherCondition");

                //Depending on the condition, we will update the weather image to its correct image
                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;

                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;

                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;

                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/Snow.png"));
                        break;
                }

                //update temperature rext

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Humidity</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);

    }

    private ImageIcon loadImage(String resorcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resorcePath));
            return new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not load image ");
        return null;
    }
}
