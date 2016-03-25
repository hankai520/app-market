
package cn.com.sparksoft;

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

import cn.com.sparksoft.persist.model.App;
import cn.com.sparksoft.persist.util.JpaServiceUtil;

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
