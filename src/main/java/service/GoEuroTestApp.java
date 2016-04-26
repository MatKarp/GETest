package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static utils.Constants.*;

public class GoEuroTestApp {

    final static Logger logger = Logger.getLogger(GoEuroTestApp.class);

    public void compute(String[] args) {

        if (args.length > 0) {
            try {
                String cityName = args[0].toString();
                String response = getResponse(cityName);
                List<JsonNode> filteredList = filterGeoPosition(response);
                fileWritter(filteredList);
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
                e.printStackTrace();
            }

        }else {
            System.out.println("Command line arguments are empty!");
            logger.info("Command line arguments are empty!");
        }
    }

    public String getResponse(String cityName) throws Exception {
        String url = URL + cityName;

        URLConnection con = new URL(url).openConnection();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    public List<JsonNode> filterGeoPosition(String response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        List<JsonNode> jsonNodesWithGeoPositions = root.findParents(GEO_POSITION);

        for (JsonNode jsonNode : jsonNodesWithGeoPositions) {

            JsonNode latitude = jsonNode.findValue(LATITUDE);
            JsonNode longitude = jsonNode.findValue(LONGITUDE);

            if (latitude == null && longitude == null) {
                jsonNodesWithGeoPositions.remove(jsonNode);
            }
        }

        return jsonNodesWithGeoPositions;
    }

    public void fileWritter(List<JsonNode> nodesList) throws IOException {
        FileWriter writer = new FileWriter(FILE_NAME);

        for (JsonNode currentNode : nodesList) {
            String id = currentNode.findValue(ID).asText();
            String name = currentNode.findValue(NAME).asText();
            String type = currentNode.findValue(TYPE).asText();
            String latitude = currentNode.findValue(LATITUDE).asText();
            String longitude = currentNode.findValue(LONGITUDE).asText();
            writer.append(id + ", " + name + ", " + type + ", " + latitude + ", " + longitude + '\n');
        }

        writer.flush();
        writer.close();

        logger.info("File: " + FILE_NAME + " was created");
    }
}
