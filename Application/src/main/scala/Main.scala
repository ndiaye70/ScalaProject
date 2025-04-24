import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import directives.AuthentificationDirective
import services.{AuthService, ChambreService, ConsultationService, DossierMedicalService, GardeServices, HospitalisationService, LitService, MaterielService, MedicalCoordinatorService, PaiementService, PatientService, PersonnelService, RendezVousService, UtilisateurService}
import routes.{AuthRoute, ChambreRoutes, ConsultationRoutes, DossierMedicalRoutes, GardeRoutes, HospitalisationRoutes, LitRoutes, MaterielRoutes, PaiementRoutes, PatientRoutes, PersonnelRoutes, RendezVousRoutes, UtilisateurRoutes}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContextExecutor}

object Main extends App {
  // Initialisation du système
  implicit val system: ActorSystem = ActorSystem("hospitalSystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  // Configuration de la base de données
  val db = Database.forConfig("slick.dbs.default.db")
  sys.addShutdownHook {
    db.close()
    system.terminate()
  }

  // Initialisation des services
  val utilisateurService = new UtilisateurService(db)
  utilisateurService.init()

  val authService = new AuthService(utilisateurService)
  val authDirective = new AuthentificationDirective(authService)

  // Initialisation des autres services (sans modification)
  val patientService = new PatientService(db)
  val gardeServices = new GardeServices(db)
  val dossierMedicalService = new DossierMedicalService(db)
  val consultationService = new ConsultationService(db)
  val medicalCoordinatorService = new MedicalCoordinatorService(consultationService, dossierMedicalService)
  val rendezVousService = new RendezVousService(db)
  val personnelService = new PersonnelService(db)
  val chambreService = new ChambreService(db)
  val litService = new LitService(db,chambreService
  )
  val materielService = new MaterielService(db)
  val hospitalisationService = new HospitalisationService(db)
  val paiementService = new PaiementService(db)

  // Initialisation des schémas individuellement
  patientService.init()
  gardeServices.init()
  dossierMedicalService.init()
  consultationService.init()
  rendezVousService.init()
  personnelService.init()
  chambreService.init()
  litService.init()
  materielService.init()
  hospitalisationService.init()
  paiementService.init()

  // Routes avec authentification (seulement PatientRoutes modifié)
  val authRoute = new AuthRoute(authService)
  val patientRoutes = new PatientRoutes(patientService, authDirective)

  // Routes sans authentification (non modifiées)
  val utilisateurRoutes = new UtilisateurRoutes(utilisateurService).routes
  val dossierMedicalRoutes = new DossierMedicalRoutes(dossierMedicalService, authDirective).routes
  val personnelRoutes = new PersonnelRoutes(utilisateurService,personnelService, authDirective).routes
  val gardeRoutes = new GardeRoutes(gardeServices, authDirective).routes
  val rendezVousRoutes = new RendezVousRoutes(rendezVousService, authDirective).routes
  val consultationRoutes = new ConsultationRoutes(consultationService, medicalCoordinatorService, authDirective).routes
  val chambreRoutes = new ChambreRoutes(chambreService, authDirective).routes
  val litRoutes = new LitRoutes(litService, authDirective).routes
  val hospitalisationRoutes = new HospitalisationRoutes(hospitalisationService, authDirective).routes
  val materielRoutes = new MaterielRoutes(materielService, authDirective).routes
  val paiementRoutes = new PaiementRoutes(paiementService, authDirective).routes

  // Concaténation des routes


  import utils.CorsSupport._

  val allRoutes: Route = corsHandler {
    Directives.concat(
      authRoute.routes,
      patientRoutes.routes,
      utilisateurRoutes,
      dossierMedicalRoutes,
      personnelRoutes,
      gardeRoutes,
      rendezVousRoutes,
      consultationRoutes,
      chambreRoutes,
      litRoutes,
      hospitalisationRoutes,
      materielRoutes,
      paiementRoutes
    )
  }


  // Démarrage du serveur HTTP
  val bindingFuture = Http()
    .newServerAt("localhost", 8081)
    .bind(allRoutes)



  scala.sys.addShutdownHook {
    println("Shutting down server...")
    bindingFuture
      .flatMap(_.unbind())
      .onComplete { _ =>
        system.terminate()
        db.close()
      }
  }


  val binding = Await.result(bindingFuture, 10.seconds)
  println(s"Server online at ${binding.localAddress}")


  CoordinatedShutdown(system).addJvmShutdownHook {
    println("Graceful shutdown complete")
  }


  Thread.currentThread.join()
}