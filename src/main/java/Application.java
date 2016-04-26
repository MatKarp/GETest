import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Application {

    public static void main(String[] args){

        String cityName = args[0].toString();

        try {
            String response = getResponse(cityName);
            List<JsonNode> filteredList = filterGeoPosition(response);
            fileWritter(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getResponse(String cityName) throws Exception{
        String url = "http://api.goeuro.com/api/v2/position/suggest/en/" + cityName;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static List<JsonNode> filterGeoPosition(String response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        List<JsonNode> jsonNodesWithGeoPositions = root.findParents("geo_position");

        for(JsonNode jsonNode : jsonNodesWithGeoPositions){

            JsonNode latitude = jsonNode.findValue("latitude");
            JsonNode longitude = jsonNode.findValue("longitude");

            if(latitude == null && longitude == null){
                jsonNodesWithGeoPositions.remove(jsonNode);
            }
        }

        return jsonNodesWithGeoPositions;
    }

    private static void fileWritter(List<JsonNode> nodesList) throws IOException {
        FileWriter writer = new FileWriter("test.csv");

        for(JsonNode currentNode: nodesList){
            String id = currentNode.findValue("_id").asText();
            String name = currentNode.findValue("name").asText();
            String type = currentNode.findValue("type").asText();
            String latitude = currentNode.findValue("latitude").asText();
            String longitude = currentNode.findValue("longitude").asText();
            writer.append(id + ", " + name + ", " + type + ", " + latitude + ", " + longitude + '\n');
        }

        writer.flush();
        writer.close();
    }
}
