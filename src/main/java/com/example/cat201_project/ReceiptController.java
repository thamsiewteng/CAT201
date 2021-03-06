package com.example.cat201_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.io.*;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.URL;
import java.util.ResourceBundle;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class ReceiptController implements Initializable {

    @FXML
    private Button BookedTicket;
    @FXML
    private Button Profile;
    @FXML
    private Button logout;
    @FXML
    private Button home;


    @FXML
    private Text Ticket;
    @FXML
    private Text Total;
    @FXML
    private Text Movie;
    @FXML
    private Text Date;
    @FXML
    private Text Time;
    @FXML
    private Text Seats;

    @FXML
    private ImageView MoviePoster;
    @FXML
    private ImageView ReceiptQR;

    private JSONArray orderData = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException{
        JSONObject orderInfo = JsonEditor.getJSONObject("orderInfo.json");
        orderData = (JSONArray) orderInfo.get("orderInfo");

        String T = (((JSONObject)orderData.get(orderData.size() - 1)).get("Ticket")).toString();
        String TOT = (((JSONObject)orderData.get(orderData.size() - 1)).get("Total")).toString();

        //Initialise movie details from JSON file
        Ticket.setText(T);
        Total.setText(TOT);

        //Initialise movie poster using path from JSON file
        Image image;
        try
        {
            String moviePosterSource = (((JSONObject) orderData.get(orderData.size() - 1)).get("Poster")).toString();
            image = new Image(new FileInputStream(moviePosterSource));
            MoviePoster.setImage(image);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //Get order details from JSON file to be embedded on receipt
        String movie = (((JSONObject)orderData.get(orderData.size() - 1)).get("Movie")).toString();
        String date = (((JSONObject)orderData.get(orderData.size() - 1)).get("Date")).toString();
        String time = (((JSONObject)orderData.get(orderData.size() - 1)).get("Time")).toString();
        String seats = "";
        int numTicket = Integer.parseInt(BuyTicketController.OrderedTicket);
        for(int i = 0; i < numTicket; i++){
            seats = seats + BuyTicketController.OrderedSeats[i] + " ";
        }
        Movie.setText(movie);
        Date.setText(date);
        Time.setText(time);
        Seats.setText(seats);

        //Generate a QR code to be embedded on the receipt
        String str= movie+date+time+seats;//concatenate strings of data into one string
        String path = "src/main/resources/com/example/cat201_project/img/QR"+ orderData.size() + ".png";
        generateQR(str, path);

        //Display QR code onto receipt
        Image image2;
        try
        {
            String QRSource = path;
            image2 = new Image(new FileInputStream(QRSource));
            ReceiptQR.setImage(image2);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void handleBookedTicketBttn() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bookedTicket.fxml"));
        Stage stage = (Stage) BookedTicket.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1280, 720));
        stage.show();
    }

    public void handleProfileBttn() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("myProfile.fxml"));
        Stage stage = (Stage) Profile.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1280, 720));
        stage.show();
    }

    public void handleLogoutBttn() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1280, 720));
        stage.show();
    }

    public void handleHomeBttn() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home-movie.fxml"));
        Stage stage = (Stage) home.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 1280, 720));
        stage.show();
    }

    public void generateQR(String str, String path){
        ByteArrayOutputStream out = QRCode.from(str).to(ImageType.JPG).stream();
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //insert updated qr path to JSON file
        JSONParser parser = new JSONParser();
        JSONObject ordInf = null;
        File inputFile = new File("src/main/resources/com/example/cat201_project/JSON_file/orderInfo.json");
        try{
            ordInf = (JSONObject) parser.parse(new FileReader(inputFile));
            JSONObject obj = (JSONObject)((JSONArray)ordInf.get("orderInfo")).get(orderData.size() - 1);
            obj.put("QR", path);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ParseException e){
            e.printStackTrace();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/com/example/cat201_project/JSON_file/orderInfo.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(ordInf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("QR Code created successfully.");
    }
}
