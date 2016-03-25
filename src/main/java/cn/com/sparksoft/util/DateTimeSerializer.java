/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cn.com.sparksoft.config.WebConfig;

import java.io.IOException;
import java.util.Date;

/**
 * 日期到字符串序列化（用于jackson）
 *
 * @author hankai
 * @version 1.0
 * @since Jul 27, 2015 11:34:49 AM
 */
public class DateTimeSerializer extends JsonSerializer<Date> {

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
     * com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize( Date value, JsonGenerator jgen, SerializerProvider provider )
                    throws IOException, JsonProcessingException {
        jgen.writeString( WebConfig.dateTimeFormatter.format( value ) );
    }
}
