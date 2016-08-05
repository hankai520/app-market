应用市场 - 移动客户端API
========================

 

本接口程序被设计用来支撑移动客户端功能，所有API均为 http web
service，数据交换格式为JSON。

接口若支持POST方式访问，则一般也支持GET方式访问（特殊情况除外，如：文件上传），最佳访问方式请参照各API定义中的建议。

对于接口访问的请求示例，若支持POST，则给出POST
报文样例（仅供参考，若要调试，请使用调试工具自行按要求设置入参），否则给出GET样例，若同时支持，则只给出POST样例，GET方式的请求，请参见HTTP规范自行组织。

对于入参组织结构，若入参为非结构化数据（比如：登录账号、密码，无需作为对象结构传入），则均采用
application/x-www-form-urlencoded 内容类型，否则，采用 application/json。

 

接口规范
--------

接口服务器参数：

domain\_name:

port: 8000

请在构造API地址时，将 domain\_name 与 port 部分用以上参数替换。

 

所有接口响应均遵循以下格式：

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
{
  "code" : 1,
  "message" : null,
  "body" : {
    "success" : true,
    "error" : null,
    "data" : {
      
    }
  }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

| **字段**     | **含义**                          | **必填** | **备注**                                                                    |
|--------------|-----------------------------------|----------|-----------------------------------------------------------------------------|
| code         | 本次API调用结果                   | Y        | 指示API调用过程是否完整（无网络错误或异常），详见接口常量定义一节           |
| message      | 当出现错误或异常时的调试信息      | N        |                                                                             |
| body         | 本次API响应的主体                 | Y        | 类似HTTP body，用于包装数据和业务逻辑代码                                   |
| body.success | 业务逻辑是否正确执行              | Y        | 不同于code，该字段指示业务级的“正确”或“失败“，例如：登录失败，此处返回false |
| body.error   | 当出现业务错误时返回的调试信息    | N        |                                                                             |
| body.data    | 响应数据的根节点（详见各API定义） | N        | 根据具体业务逻辑，非数据接口可能不包含该节点                                |

 

接口常量定义
------------

本节定义接口中的常量信息，如：API响应代码，业务错误码等。

 

### API响应代码

| **代码** | **含义**     |
|----------|--------------|
| 1        | 成功         |
| 2        | 入参错误     |
| 3        | 系统内部错误 |
| 4        | 未知错误     |

 

### 业务错误码

| **代码** | **含义**                             |
|----------|--------------------------------------|
| 1        | 账号或密码不正确，登录授权失败       |
| 2        | 非法的用户角色（例如：非审查员登录） |

 

接口详细定义
------------

本节介绍各接口详细信息，入参，响应数据结构和注意事项。

 

### 查询APP元数据

地址：http://domain\_name:port/api/app/{sku}/metadata

请求方式：GET

 

**入参:**

| **字段** | **含义** | **必传** | 备注     |
|----------|----------|----------|----------|
| sku      | APP的SKU | Y        | 路径参数 |

 

**请求示例:**

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
http://domain_name:port/api/app/myapp.ios/metadata
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

**响应：**

| **字段**         | **含义**       | **必传** | **备注**                              |
|------------------|----------------|----------|---------------------------------------|
| id               | 审查员账号信息 | Y        |                                       |
| sku              | APP 的 SKU     | Y        | SKU 即 Stock Keeping Unit             |
| name             | 名称           | Y        |                                       |
| bundleIdentifier | 包ID           | Y        |                                       |
| version          | 包版本         | Y        | 点分十进制\#build号，例如：1.0.0\#234 |
| updateTime       | 最近更新时间   | N        | yyyy-MM-dd HH:mm:ss                   |
| createTime       | 创建时间       | Y        | yyyy-MM-dd HH:mm:ss                   |
| status           | 状态           | Y        | 0: 开发中, 1: 已上架, 2: 已下架       |
| platform         | 运行平台       | Y        | 0: iOS, 1: Android                    |
| metaData         | 元数据         | N        | 用于存储APP描述信息等                 |

 

**响应示例：**

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
{
  "code" : 1,
  "body" : {
    "success" : true,
    "data" : {
      "id" : 1,
      "sku" : "myapp.ios",
      "name" : "测试",
      "bundleIdentifier" : "cn.com.xxx.test",
      "version" : "1.0.1#28",
      "updateTime" : "2016-08-05 00:00:00",
      "createTime" : "2016-08-01 00:00:00",
      "status" : 1,
      "platform" : 0,
      "metaData" : "test data"
    }
  }
}
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

### APP 安装包下载地址

iOS包下载地址：itms-services://?action=download-manifest&url=https://host:port/api/app/{app\_id}/ios\_manifest.plist

请求方式：GET

注意：此下载方式仅适用于 iOS，这是通过 In-House 方式用自己的服务器提供 APP
下载安装服务，而不是通过苹果的 App Store

 

Android包下载地址：http://domain\_name:port/api/app/{app\_id}/package.apk

请求方式：GET

 

**入参：**

| **字段** | **含义**    | **必传** | **备注**                    |
|----------|-------------|----------|-----------------------------|
| app\_id  | APP唯一编号 | Y        | 此号通过获取APP元数据可获得 |

 

**请求示例：**

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
iOS：
itms-services://?action=download-manifest&url=https://host:port/api/app/1/ios_manifest.plist

Android:
http://domain_name:port/api/app/1/package.apk
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

**响应：**

响应内容为APP安装包数据

 

### 上传 APP 安装包

地址：http://domain\_name:port/api/app/{sku}/update

请求方式：POST ( multipart )

 

**入参：**

| **字段** | **含义**       | **必传** | **备注**       |
|----------|----------------|----------|----------------|
| sku      | APP 的 SKU 号  | Y        | URL路径参数    |
| package  | APP 安装包文件 | Y        | multipart 参数 |

 

**请求示例：**

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
POST /api/app/{sku}/update
Host: host:port
Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryHlbB8VVdqgpBPM4K
Origin: http://localhost
Accept-Encoding: gzip, deflate
Cookie: AJS.conglomerate.cookie="|tabContainer.tabContainer.selectedTab=Capabilities"; BAMBOO-AGENT-FILTER=LAST_25_BUILDS; bamboo.dash.display.toggles=buildQueueActions-actions-queueControl
Connection: keep-alive
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/601.4.4 (KHTML, like Gecko) Version/9.0.3 Safari/601.4.4
Referer: http://localhost/api/my_avatar.html
Content-Length: 571834
Accept-Language: en-us
------WebKitFormBoundaryHlbB8VVdqgpBPM4K
Content-Disposition: form-data; name="package"; filename="package.ipa"

[package data]
------WebKitFormBoundaryHlbB8VVdqgpBPM4K--
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

**响应：**

非数据接口，无响应数据。

HTTP状态码为200表示成功

 

附录
----

 

### 程序发行包结构

根目录

— app.war //程序可执行文件

— run.sh //linux 下的程序控制脚本（ 执行 run.sh start 来启动；执行 run.sh stop
来停止）

— run.bat //windows 下的启动脚本（前台运行，CTLR+C 停止）

 

### 部署环境要求

jdk/jre \>= 1.7.0 （需要设置 jdk/jre 环境变量）

启动后，将会在 run.sh 或 run.bat 同级目录创建 data
目录，此目录包含程序必要配置和数据以及缓存，请不要擅自修改其中的内容。
