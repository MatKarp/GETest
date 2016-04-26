import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import service.GoEuroTestApp;
import utils.Constants;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static utils.Constants.FILE_NAME;
import static utils.Constants.URL;


public class GoEuroTest {

    @Test
    public void connectionCheck() throws Exception {

        String url = URL + "Berlin";
        URLConnection con = new URL(url).openConnection();
        assertEquals(((HttpURLConnection) con).getResponseCode(), 200);
    }

    @Test
    public void testBerlinCityResponse() throws Exception {

        String response = new GoEuroTestApp().getResponse("Berlin");
        assertNotNull(response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        assertEquals(8, root.size());
    }

    @Test
    public void testWrongCityName() throws Exception {

        String response = new GoEuroTestApp().getResponse("xsdrasdq");
        assertNotNull(response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        assertEquals(0, root.size());
    }

    @Test
    public void testFileCreation() throws Exception {

        File testFile = new File(FILE_NAME);
        testFile.delete();

        assertFalse(testFile.isFile());

        String[] args = {"Berlin"};

        new GoEuroTestApp().compute(args);

        assertTrue(testFile.isFile());

    }

    @Test
    public void testFileContent() throws Exception {

        File testFile = new File(FILE_NAME);
        testFile.delete();

        assertFalse(testFile.isFile());

        String[] args = {"Berlin"};

        new GoEuroTestApp().compute(args);

        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        assertThat(sb.toString().length(),is(equalTo(414)));
        assertThat(sb.toString().substring(8, 14), is(equalTo("Berlin")));

        assertTrue(testFile.isFile());

    }
}