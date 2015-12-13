package bowlpicker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * The FXML controller for the closing screen of BowlPicker
 * Outputs the picks and instructs the user how to submit them
 * @author Ryan Burns
 */
public class ClosingScreenController implements Initializable {
    @FXML
    private Label finalMsg;
    @FXML
    private TextField outputField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Try to send the output to the Groupme chat using the bot
        boolean success = sendMessage(
                "!submit " + Driver.output, "58e3406f09337706ed30dc3872");
        if (success) {
            finalMsg.setText("Your picks have been submitted."
                    + " Thanks and good luck!");
            outputField.setVisible(false);
        } else {
            finalMsg.setText("Since we are having a problem submitting over "
                    + "your internet connection, you will need to copy and "
                    + "paste *exactly* the string below this message into the "
                    + "Groupme chat. Thanks and good luck!");
            outputField.setText("!submit " + Driver.output);
        }
    }

    /**
     * 
     * @param message
     * @param botID
     * @return 
     */
    private boolean sendMessage(String message, String botID) {
        String requestUrl = "https://api.groupme.com/v3/bots/post";
        String urlParameters = "bot_id=" + botID+ "&text="
                + message + "&param3=c";
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection
                    = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            DataOutputStream wr
                    = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            connection.disconnect();

            int responseCode = connection.getResponseCode();
            if (responseCode != 202) {
                System.out.println(responseCode + " error has occured while"
                        + " sending the message: " + message);
            }
        } catch (MalformedURLException mue) {
            System.out.println("Error occured while establishing a connection");
        } catch (IOException ioe) {
            System.out.println("Error occured while sending data");
            return false;
        }
        return true;
    }
}