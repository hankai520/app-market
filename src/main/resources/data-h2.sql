--程序数据库数据初始化脚本
-- 2016-03-15

--注意：初始数据结果集中由于直接查询数据，因此数据内容直接作为列名，如果数据内容重复，需要指定别名

-- 插入测试应用信息
INSERT INTO PUBLIC.APPS 
    (ID, NAME, SMALLIMAGEURL, LARGEIMAGEURL, VERSION, CREATETIME, STATUS, PLATFORM, BUNDLEIDENTIFIER)
SELECT * FROM (
SELECT 1, 'demo', 'http://www.baidu.com/small.png', 'http://www.baidu.com/large.png', 
'1.0.0', '2016-03-15 15:14:21', 1 as c7, 0, 'cn.com.sparksoft.zjsc'
) WHERE NOT EXISTS(SELECT * FROM PUBLIC.APPS);

-- 新建默认管理员账号（17761748443, 123456）
INSERT INTO PUBLIC.USERS 
    (ID, MOBILE, PASSWORD, CREATETIME, STATUS, ROLE)
SELECT * FROM (
SELECT 1, '17761748443', 'e10adc3949ba59abbe56e057f20f883e', '2016-03-15 15:14:21', 1 as c5, 0
) WHERE NOT EXISTS(SELECT * FROM PUBLIC.USERS);