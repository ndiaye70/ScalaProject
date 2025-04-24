export interface DossierMedical {
  id?: number;
  patientId: number;
  dateCreation?: string;
  antecedents: string;
  allergies: string;
  traitementsEnCours: string;
  notes: string;
}

export interface Consultation {
  id?: number; // Optionnel car c'est un autoincrement
  patientId: number;
  personnelId: number;
  dateRealisation: string; // ISO 8601 string (LocalDateTime → string côté frontend)
  symptomes: string;
  diagnostic: string;
  prescriptions: string;
  notesInternes: string;
}

export interface Hospitalisation {
  id?: number;
  patientId: number;
  litId: number;
  dateDebut: string; // ISO string format: 'YYYY-MM-DDTHH:mm:ss'
  dateFin?: string;  // Optionnel, même format
  motif?: string;
  personnelResponsableId?: number;
  notes?: string;
}

export interface Personnel {
  id?: number; // Optionnel car non défini à la création
  nom: string;
  prenom: string;
  specialite: string;
  typePersonnel: string;
  telephone: string;
  horaires: string;
}

// src/app/models/chambre.model.ts
export interface Chambre {
  id?: number;
  numero: string;                    // Numéro unique (ex: "A101")
  bloc: string;                      // Bloc/Bâtiment (ex: "A", "Cardio")
  statut: 'Libre' | 'Occupée' | 'Hors service'; // Statuts attendus
  dateDerniereMaintenance?: string; // ISO string (ex: "2024-04-21T15:00:00")
  capacite: number;                 // Nombre de lits max
  equipements?: string;             // JSON ou texte libre
}


// src/app/models/lit.model.ts
export interface Lit {
  id?: number;
  numero: string;                   // Numéro du lit (ex: "L1")
  chambreId: number;                // Clé étrangère vers la chambre
  statut: 'Libre' | 'Occupé' | 'Réservé' | 'En nettoyage'; // Statuts possibles
}




interface LitAvecChambre extends Lit {
  chambre?: Chambre;
}
export interface Garde {
  id?: number; // Optionnel car l'id est généré automatiquement
  personnelId: number;
  dateDebutPeriode: string; // Format ISO date (ex: "2025-04-22")
  dateFinPeriode: string;   // Format ISO date (ex: "2025-04-22")
  heureDebut: string;       // Format "HH:mm:ss" (ex: "08:00:00")
  heureFin: string;         // Format "HH:mm:ss" (ex: "18:00:00")
}

export interface Paiement {
  id?: number;
  patientId: number;
  montant: number;
  mode: 'Espèce' | 'Chèque' | 'Assurance';
  datePaiement: string; // format ISO: "2025-04-23T14:30:00"
  reference?: string;
}

export interface RendezVous {
  id?: number;
  patientId: number;
  personnelId: number;
  dateHeure: string; // ISO format, e.g., "2024-04-23T14:30"
  motif: string;
  statut: 'Planifié' | 'Terminé';
}

export interface Materiel {
  id?: number; // Optionnel car il n’existe pas avant la création
  nom: string;
  description?: string;
  typeMateriel: string;
  statut: string;
  dateAchat: string; // Format ISO ex: "2025-04-10T10:30:00"
  dateDerniereMaintenance?: string;
  fournisseur: string;
  quantite: number;
  localisation?: string;
}
