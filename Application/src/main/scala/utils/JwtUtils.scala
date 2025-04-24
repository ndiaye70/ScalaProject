package utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtUtils {
  // Clé secrète pour signer le token
  private val secret = "secretKey"

  def generateToken(username: String): String = {
    val algorithm = Algorithm.HMAC256(secret)

    val cleanUsername = username.replace("\"", "") // Supprimer les guillemets éventuels

    val token = JWT.create()
      .withSubject(cleanUsername)
      .withIssuedAt(new Date())
      .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
      .sign(algorithm)

    token
  }


  // Fonction pour décoder et valider un token JWT
  def decodeToken(token: String): String = {
    val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()

    try {
      // Vérification et décodage du token
      val decodedJWT = verifier.verify(token)
      decodedJWT.getSubject  // Retourne le nom d'utilisateur contenu dans le token
    } catch {
      case e: Exception =>
        // Debugging: Afficher l'erreur
        println(s"Erreur lors de la vérification du token: ${e.getMessage}")
        throw new RuntimeException("Invalid token", e)
    }
  }
}
