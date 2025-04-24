package utils

import models.{Chambre, Consultation, DossierMedical, Garde, Hospitalisation, Lit, Materiel, Paiement, Patient, Personnel, PersonnelAvecUtilisateur, RendezVous, Utilisateur}
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat, deserializationError, enrichAny}
import LocalTimeJsonProtocol.LocalTimeFormat

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

// Format pour LocalDate
object LocalDateJsonProtocol extends DefaultJsonProtocol {
  implicit object LocalDateFormat extends RootJsonFormat[LocalDate] {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    def write(date: LocalDate): JsValue = JsString(date.format(dateFormatter))
    def read(value: JsValue): LocalDate = value match {
      case JsString(dateStr) => LocalDate.parse(dateStr, dateFormatter)
      case _ => deserializationError("Expected a date string")
    }
  }
}
// Format pour LocalTime
object LocalTimeJsonProtocol extends DefaultJsonProtocol {
  import java.time.LocalTime
  import java.time.format.DateTimeFormatter
  import spray.json._

  implicit object LocalTimeFormat extends RootJsonFormat[LocalTime] {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    def write(time: LocalTime): JsValue = JsString(time.format(timeFormatter))
    def read(value: JsValue): LocalTime = value match {
      case JsString(timeStr) => LocalTime.parse(timeStr, timeFormatter)
      case _ => deserializationError("Expected a time string")
    }
  }
}

// Format pour LocalDateTime
object LocalDateTimeJsonProtocol extends DefaultJsonProtocol {
  implicit object LocalDateTimeFormat extends RootJsonFormat[LocalDateTime] {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    def write(dateTime: LocalDateTime): JsValue = JsString(dateTime.format(dateFormatter))
    def read(value: JsValue): LocalDateTime = value match {
      case JsString(dateTimeStr) => LocalDateTime.parse(dateTimeStr, dateFormatter)
      case _ => deserializationError("Expected a date-time string")
    }
  }
}

object JsonProtocols {
  import LocalDateJsonProtocol.LocalDateFormat
  import LocalDateTimeJsonProtocol.LocalDateTimeFormat
  import spray.json.DefaultJsonProtocol._

  // Format pour Patient (sans dossiers)
  implicit val patientFormat: RootJsonFormat[Patient] = jsonFormat9(Patient)

  implicit val gardeFormat: RootJsonFormat[Garde] = jsonFormat6(Garde)

  // Format pour Dossier Médical
  implicit val dossierMedicalFormat: RootJsonFormat[DossierMedical] = jsonFormat7(DossierMedical)

  // Format pour Personnel
  implicit val personnelFormat: RootJsonFormat[Personnel] = jsonFormat7(Personnel)

  implicit val stringLongMapFormat = mapFormat[String, Long]


  // Format pour une liste de Patients
  implicit val seqPatientFormat: RootJsonFormat[Seq[Patient]] = new RootJsonFormat[Seq[Patient]] {
    def write(seq: Seq[Patient]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Patient] = deserializationError("Deserialization not implemented for Seq[Patient]")
  }

  // Format pour une liste de Dossiers Médicaux
  implicit val seqDossierMedicalFormat: RootJsonFormat[Seq[DossierMedical]] = new RootJsonFormat[Seq[DossierMedical]] {
    def write(seq: Seq[DossierMedical]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[DossierMedical] = deserializationError("Deserialization not implemented for Seq[DossierMedical]")
  }

  // Format pour RendezVous
  implicit val rendezVousFormat: RootJsonFormat[RendezVous] = jsonFormat6(RendezVous)

  // Format pour une liste de RendezVous
  implicit val seqRendezVousFormat: RootJsonFormat[Seq[RendezVous]] = new RootJsonFormat[Seq[RendezVous]] {
    def write(seq: Seq[RendezVous]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[RendezVous] = deserializationError("Deserialization not implemented for Seq[RendezVous]")
  }

  // Format pour une liste de Gardes
  implicit val seqGardeFormat: RootJsonFormat[Seq[Garde]] = new RootJsonFormat[Seq[Garde]] {
    def write(seq: Seq[Garde]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Garde] = deserializationError("Deserialization not implemented for Seq[Garde]")
  }

  // Ajoutez dans object JsonProtocols (à la fin, avant la dernière parenthèse)

  // Format pour Consultation
  implicit val consultationFormat: RootJsonFormat[Consultation] = jsonFormat8(Consultation)

  // Format pour une liste de Consultations
  implicit val seqConsultationFormat: RootJsonFormat[Seq[Consultation]] = new RootJsonFormat[Seq[Consultation]] {
    def write(seq: Seq[Consultation]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Consultation] = deserializationError("Deserialization not implemented for Seq[Consultation]")
  }



  // Format combiné Patient + Consultations
  implicit val patientWithConsultationsFormat: RootJsonFormat[(Patient, Seq[Consultation])] =
    new RootJsonFormat[(Patient, Seq[Consultation])] {
      def write(data: (Patient, Seq[Consultation])): JsValue = {
        val (patient, consultations) = data
        JsObject(
          "patient" -> patient.toJson,
          "consultations" -> consultations.toJson
        )
      }

      def read(json: JsValue): (Patient, Seq[Consultation]) = {
        json.asJsObject.getFields("patient", "consultations") match {
          case Seq(patient, consultations) =>
            (patient.convertTo[Patient], consultations.convertTo[Seq[Consultation]])
          case _ => deserializationError("Expected JSON with patient and consultations fields")
        }
      }
    }

  // Format combiné Personnel + Consultations
  implicit val personnelWithConsultationsFormat: RootJsonFormat[(Personnel, Seq[Consultation])] =
    new RootJsonFormat[(Personnel, Seq[Consultation])] {
      def write(data: (Personnel, Seq[Consultation])): JsValue = {
        val (personnel, consultations) = data
        JsObject(
          "personnel" -> personnel.toJson,
          "consultations" -> consultations.toJson
        )
      }

      def read(json: JsValue): (Personnel, Seq[Consultation]) = {
        json.asJsObject.getFields("personnel", "consultations") match {
          case Seq(personnel, consultations) =>
            (personnel.convertTo[Personnel], consultations.convertTo[Seq[Consultation]])
          case _ => deserializationError("Expected JSON with personnel and consultations fields")
        }
      }
    }

  // Ajoutez ceci dans object JsonProtocols

  // Format pour la réponse d'historique médical complet
  implicit val medicalHistoryResponseFormat: RootJsonFormat[(Seq[Consultation], DossierMedical)] =
    new RootJsonFormat[(Seq[Consultation], DossierMedical)] {
      def write(data: (Seq[Consultation], DossierMedical)): JsValue = {
        val (consultations, dossier) = data
        JsObject(
          "consultations" -> consultations.toJson,
          "dossier" -> dossier.toJson
        )
      }

      def read(json: JsValue): (Seq[Consultation], DossierMedical) = {
        json.asJsObject.getFields("consultations", "dossier") match {
          case Seq(consultations, dossier) =>
            (consultations.convertTo[Seq[Consultation]], dossier.convertTo[DossierMedical])
          case _ => deserializationError("Expected JSON with consultations and dossier fields")
        }
      }
    }

  implicit val personnelAvecUtilisateurFormat: RootJsonFormat[PersonnelAvecUtilisateur] =
    jsonFormat2(PersonnelAvecUtilisateur)


  // Ajoutez dans utils.JsonProtocols

  // Format pour Chambre
  implicit val chambreFormat: RootJsonFormat[Chambre] = jsonFormat7(Chambre)

  // Format pour une liste de Chambres
  implicit val seqChambreFormat: RootJsonFormat[Seq[Chambre]] = new RootJsonFormat[Seq[Chambre]] {
    def write(seq: Seq[Chambre]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Chambre] = deserializationError("Deserialization not implemented for Seq[Chambre]")
  }

  // Format pour Lit
  implicit val litFormat: RootJsonFormat[Lit] = jsonFormat4(Lit)

  // Format pour une liste de lits
  implicit val seqLitFormat: RootJsonFormat[Seq[Lit]] = new RootJsonFormat[Seq[Lit]] {
    def write(seq: Seq[Lit]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Lit] = deserializationError("Deserialization not implemented for Seq[Lit]")
  }


  // Format pour une Hospitalisation
  implicit val hospitalisationFormat: RootJsonFormat[Hospitalisation] = jsonFormat8(Hospitalisation)

  // Format pour une liste d'Hospitalisation
  implicit val seqHospitalisationFormat: RootJsonFormat[Seq[Hospitalisation]] = new RootJsonFormat[Seq[Hospitalisation]] {
    def write(seq: Seq[Hospitalisation]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Hospitalisation] = deserializationError("Deserialization not implemented for Seq[Hospitalisation]")
  }

  // Format pour Materiel
  implicit val materielFormat: RootJsonFormat[Materiel] = jsonFormat10(Materiel)

  // Format pour une liste de matériels
  implicit val seqMaterielFormat: RootJsonFormat[Seq[Materiel]] = new RootJsonFormat[Seq[Materiel]] {
    def write(seq: Seq[Materiel]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Materiel] = deserializationError("Deserialization not implemented for Seq[Materiel]")
  }


  // utils/JsonProtocols.scala
  implicit val paiementFormat: RootJsonFormat[Paiement] = jsonFormat6(Paiement)

  implicit val seqPaiementFormat: RootJsonFormat[Seq[Paiement]] = new RootJsonFormat[Seq[Paiement]] {
    def write(seq: Seq[Paiement]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Paiement] = deserializationError("Deserialization not implemented for Seq[Paiement]")
  }


  // Format JSON pour Utilisateur
  implicit val utilisateurFormat: RootJsonFormat[Utilisateur] = jsonFormat5(Utilisateur)

  // Format JSON pour une liste d'utilisateurs
  implicit val seqUtilisateurFormat: RootJsonFormat[Seq[Utilisateur]] = new RootJsonFormat[Seq[Utilisateur]] {
    def write(seq: Seq[Utilisateur]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Utilisateur] = deserializationError("Deserialization not implemented for Seq[Utilisateur]")
  }

  // Format combiné Personnel + Utilisateur
  implicit val personnelUtilisateurTupleFormat: RootJsonFormat[(Personnel, Utilisateur)] =
    new RootJsonFormat[(Personnel, Utilisateur)] {
      def write(data: (Personnel, Utilisateur)): JsValue = {
        val (personnel, utilisateur) = data
        JsObject(
          "personnel" -> personnel.toJson,
          "utilisateur" -> utilisateur.toJson
        )
      }

      def read(json: JsValue): (Personnel, Utilisateur) = {
        json.asJsObject.getFields("personnel", "utilisateur") match {
          case Seq(personnel, utilisateur) =>
            (personnel.convertTo[Personnel], utilisateur.convertTo[Utilisateur])
          case _ => deserializationError("Expected JSON with personnel and utilisateur fields")
        }
      }
    }







  // Format pour une liste de Personnel
  implicit val seqPersonnelFormat: RootJsonFormat[Seq[Personnel]] = new RootJsonFormat[Seq[Personnel]] {
    def write(seq: Seq[Personnel]): JsValue = JsArray(seq.map(_.toJson).toVector)
    def read(json: JsValue): Seq[Personnel] = deserializationError("Deserialization not implemented for Seq[Personnel]")
  }

  // Format combiné Patient + Dossiers
  implicit val patientWithDossiersFormat: RootJsonFormat[(Patient, Seq[DossierMedical])] =
    new RootJsonFormat[(Patient, Seq[DossierMedical])] {
      def write(data: (Patient, Seq[DossierMedical])): JsValue = {
        val (patient, dossiers) = data
        JsObject(
          "patient" -> patient.toJson,
          "dossiers" -> dossiers.toJson
        )
      }

      def read(json: JsValue): (Patient, Seq[DossierMedical]) = {
        json.asJsObject.getFields("patient", "dossiers") match {
          case Seq(patient, dossiers) =>
            (patient.convertTo[Patient], dossiers.convertTo[Seq[DossierMedical]])
          case _ => deserializationError("Expected JSON with patient and dossiers fields")
        }
      }
    }
}
