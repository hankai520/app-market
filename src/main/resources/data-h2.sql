--程序数据库数据初始化脚本
-- 2016-03-15

-- 插入测试应用信息
MERGE INTO PUBLIC.APPS
    (ID, NAME, SMALLIMAGEURL, LARGEIMAGEURL, PACKAGEURL, VERSION, CREATETIME, STATUS, BUNDLEIDENTIFIER)
KEY(ID)
VALUES 
    (1, 'demo', 'http://www.baidu.com/small.png', 'http://www.baidu.com/large.png', 'http://www.baidu.com/app.ipa', '1.0', '2016-03-15 15:14:21', 0, 'cn.com.sparksoft.zjsc');

-- 新建默认管理员账号（17761748443, 123456）
MERGE INTO PUBLIC.USERS
    (ID, MOBILE, PASSWORD, CREATETIME, STATUS, ROLE)
KEY(ID)
VALUES 
    (1, '17761748443', 'e10adc3949ba59abbe56e057f20f883e', '2016-03-15 15:14:21', 1, 0);