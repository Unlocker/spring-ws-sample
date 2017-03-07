package ru.unlocker.soap.client;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import ru.unlocker.soap.client.ws.WsClient;

/**
 * Application integration test.
 *
 * @author maksimovsa
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class
)
@DirtiesContext
public class ITApplicationTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private WsClient client;

    public ITApplicationTests() {
    }

    @Test
    public void testReceiveCountryInfo() throws Exception {
        // GIVEN
        final String countryName = "Poland";
        // WHEN
        GetCountryResponse response = client.select(countryName);
        // THEN
        assertThat(response, Matchers.notNullValue());
        assertThat(response.getCountry(), Matchers.notNullValue());
        assertThat(response.getCountry().name, Matchers.is(countryName));
    }

}
