/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist;

import org.springframework.stereotype.Service;

import cn.com.sparksoft.persist.model.User;

/**
 * 用户业务
 *
 * @author hankai
 * @version 1.0
 * @since Jul 17, 2015 9:21:02 AM
 */
@Service
public class UserService extends JpaBasedService<User> {

    public UserService() {
        super( User.class );
    }
}
