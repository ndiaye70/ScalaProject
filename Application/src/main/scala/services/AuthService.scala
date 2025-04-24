package services

import models.Utilisateur
import utils.JwtUtils
import scala.concurrent.{ExecutionContext, Future}

class AuthService(utilisateurService: UtilisateurService)(implicit ec: ExecutionContext) {

  // Authentifier un utilisateur et générer un token
  def authentifier(email: String, password: String): Future[Option[String]] = {
    utilisateurService.trouverParEmail(email).map {
      case Some(utilisateur) if utilisateur.motDePasse == password =>
        Some(JwtUtils.generateToken(email))
      case _ => None
    }
  }

  // Valider un token et récupérer l'utilisateur
  def validerToken(token: String): Future[Option[Utilisateur]] = {
    try {
      val email = JwtUtils.decodeToken(token)
      utilisateurService.trouverParEmail(email)
    } catch {
      case _: Exception => Future.successful(None)
    }
  }
}