package ren.hankai.appmarket.persist.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import ren.hankai.ApplicationTests;

import java.io.File;

public class TencentCosRepositoryTest extends ApplicationTests {

  @Autowired
  private TencentCosRepository repo;

  @Test
  public void testSave() throws Exception {
    final File testFile = ResourceUtils.getFile("classpath:test_cos.txt");
    final boolean result = repo.save("test_cos.txt", testFile);
    assertTrue(result);
  }

  @Test
  public void testGetDownloadUrl() {
    final String url = repo.getDownloadUrl("test_cos.txt");
    assertEquals("https://apps-1301799117.cos.ap-nanjing.myqcloud.com/test_cos.txt", url);
  }

}
