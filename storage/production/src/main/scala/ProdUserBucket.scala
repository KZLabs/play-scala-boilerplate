package storage.production

import core.buckets.UserBucket
import core.entities._

class ProdUserBucket extends UserBucket {
  def findByEmail(email: String): Option[User] =
    Some(User(userId = "21",
    firstName = "Jhon",
    lastName = "Doe",
    fullName = "Jhon Doe",
    email = "jhon.doe@mail.com",
    password = "$2a$10$iXIfki6AefgcUsPqR.niQ.FvIK8vdcfup09YmUxmzS/sQeuI3QOFG"))
}

object ProdUserBucket {
  
}
