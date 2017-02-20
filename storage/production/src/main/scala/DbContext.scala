package storage.production

import io.getquill.{ MysqlJdbcContext, SnakeCase }

package object db {

  type DbContext = MysqlJdbcContext[SnakeCase]

}
