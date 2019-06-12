/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.api.payload;

import java.io.Serializable;
import java.util.List;

/**
 * 用于包装前端表控件数据源
 *
 * @author hankai
 * @version 1.0
 * @since Mar 25, 2016 9:17:29 AM
 */
public class PaginatedList implements Serializable {

  private static final long serialVersionUID = 1L;

  private int total;
  private List<?> rows;

  public int getTotal() {
    return total;
  }

  public void setTotal(final int total) {
    this.total = total;
  }

  public List<?> getRows() {
    return rows;
  }

  public void setRows(final List<?> rows) {
    this.rows = rows;
  }
}
