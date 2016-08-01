
package ren.hankai;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ren.hankai.Application;
import ren.hankai.Preferences;
import ren.hankai.persist.model.App;
import ren.hankai.persist.util.JpaServiceUtil;
import ren.hankai.util.MobileAppScanner;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration(
    classes = Application.class )
@WebIntegrationTest
@ActiveProfiles( Preferences.PROFILE_TEST )
public abstract class ApplicationTests {

    @Autowired
    protected WebApplicationContext ctx;
    protected MockMvc               mockMvc;
    @Autowired
    protected JpaServiceUtil        jpaServiceUtil;
    @Autowired
    protected MobileAppScanner      appScanner;

    @Before
    public void setUpMVC() {
        mockMvc = MockMvcBuilders.webAppContextSetup( ctx ).build();
        Class<?>[] classes = {
            App.class,
        };
        for ( Class<?> class1 : classes ) {
            jpaServiceUtil.deleteAll( class1 );
        }
    }
}
