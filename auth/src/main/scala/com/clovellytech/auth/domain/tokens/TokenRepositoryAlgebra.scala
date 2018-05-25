package com.clovellytech.auth
package domain.tokens

import com.clovellytech.db.CRUDAlgebra
import tsec.common.SecureRandomId


trait TokenRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, SecureRandomId, BearerToken, Unit]