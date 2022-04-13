package ren.hankai.appmarket.persist.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;
import ren.hankai.appmarket.persist.TencentCloudRepository;
import ren.hankai.cordwood.core.Preferences;

import java.io.File;

import javax.annotation.PostConstruct;

@Component
public class TencentCosRepository implements TencentCloudRepository {

  private COSClient client;
  private String domainName;

  @PostConstruct
  private void init() {
    // 1 初始化用户身份信息（secretId, secretKey）。
    // SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
    final String secretId = Preferences.getCustomConfig("tencentCosSecretId");
    final String secretKey = Preferences.getCustomConfig("tencentCosSecretKey");
    final COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
    // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
    final Region region = new Region("ap-nanjing");
    final ClientConfig clientConfig = new ClientConfig(region);
    // 这里建议设置使用 https 协议
    // 从 5.6.54 版本开始，默认使用了 https
    clientConfig.setHttpProtocol(HttpProtocol.https);
    // 3 生成 cos 客户端。
    client = new COSClient(cred, clientConfig);

    domainName = Preferences.getCustomConfig("tencentCosDomainName");
  }

  @Override
  public boolean save(final String key, final String localFilePath) {
    return save(key, new File(localFilePath));
  }

  @Override
  public boolean save(final String key, final File localFile) {
    // 指定要上传的文件
    if (null == localFile || !localFile.exists() || !localFile.isFile()) {
      return false;
    }
    // 指定文件将要存放的存储桶
    final String bucketName = Preferences.getCustomConfig("tencentCosBucketName");
    // 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
    final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
    final PutObjectResult result = client.putObject(putObjectRequest);
    return result != null && result.getContentMd5() != null;
  }

  @Override
  public String getDownloadUrl(final String key) {
    return String.format("https://%s/%s", domainName, key);
  }

}
