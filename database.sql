DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'Hospitaldb') THEN
        CREATE DATABASE "Hospitaldb" WITH OWNER postgres;
    END IF;
END $$;

-- === Tables ===
CREATE TABLE public.patients (
    id bigserial PRIMARY KEY,
    nom varchar NOT NULL,
    prenom varchar NOT NULL,
    date_naissance date NOT NULL,
    sexe varchar NOT NULL,
    telephone varchar NOT NULL,
    adresse varchar NOT NULL,
    numero_assurance varchar,
    code_patient varchar NOT NULL
);

CREATE TABLE public.dossiers_medicaux (
    id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL REFERENCES public.patients ON DELETE CASCADE,
    date_creation timestamp NOT NULL,
    antecedents varchar NOT NULL,
    allergies varchar NOT NULL,
    traitements_en_cours varchar NOT NULL,
    notes varchar NOT NULL
);

CREATE TABLE public.personnels (
    id bigserial PRIMARY KEY,
    nom varchar NOT NULL,
    prenom varchar NOT NULL,
    specialite varchar NOT NULL,
    type_personnel varchar NOT NULL,
    telephone varchar NOT NULL,
    email varchar NOT NULL,
    horaires varchar NOT NULL
);

CREATE TABLE public.rendez_vous (
    id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL REFERENCES public.patients,
    personnel_id bigint NOT NULL REFERENCES public.personnels,
    date_heure timestamp NOT NULL,
    motif varchar NOT NULL,
    statut varchar NOT NULL
);

CREATE TABLE public.consultations (
    id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL REFERENCES public.patients,
    personnel_id bigint NOT NULL REFERENCES public.personnels,
    date_realisation timestamp NOT NULL,
    symptomes varchar(1000) NOT NULL,
    diagnostic varchar(2000) NOT NULL,
    prescriptions varchar NOT NULL,
    notes_internes varchar NOT NULL
);

CREATE TABLE public.chambres (
    id bigserial PRIMARY KEY,
    numero varchar NOT NULL UNIQUE,
    bloc varchar NOT NULL,
    statut varchar NOT NULL,
    date_derniere_maintenance timestamp,
    capacite integer NOT NULL,
    equipements varchar
);

CREATE TABLE public.lits (
    id bigserial PRIMARY KEY,
    numero varchar NOT NULL,
    chambre_id bigint NOT NULL REFERENCES public.chambres ON DELETE CASCADE,
    statut varchar NOT NULL
);

CREATE TABLE public.hospitalisations (
    id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL REFERENCES public.patients,
    lit_id bigint NOT NULL REFERENCES public.lits,
    date_debut timestamp NOT NULL,
    date_fin timestamp,
    motif varchar,
    personnel_responsable_id bigint REFERENCES public.personnels,
    notes varchar
);

CREATE TABLE public.materiels (
    id bigserial PRIMARY KEY,
    nom varchar NOT NULL,
    description varchar,
    type_materiel varchar NOT NULL,
    statut varchar NOT NULL,
    date_achat timestamp NOT NULL,
    date_derniere_maintenance timestamp,
    fournisseur varchar NOT NULL,
    quantite integer NOT NULL,
    localisation varchar
);

CREATE TABLE public.paiements (
    id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL,
    montant numeric(21, 2) NOT NULL,
    mode varchar NOT NULL,
    date_paiement timestamp NOT NULL,
    reference varchar
);

CREATE TABLE public.utilisateurs (
    id bigserial PRIMARY KEY,
    id_personnel bigint NOT NULL REFERENCES public.personnels,
    nom varchar NOT NULL,
    email varchar NOT NULL UNIQUE,
    mot_de_passe varchar NOT NULL,
    role varchar NOT NULL
);

CREATE TABLE public.gardes (
    id bigserial PRIMARY KEY,
    personnel_id bigint NOT NULL REFERENCES public.personnels,
    date_debut_periode date NOT NULL,
    date_fin_periode date NOT NULL,
    heure_debut time NOT NULL,
    heure_fin time NOT NULL
);

-- === Insertion de données ===
-- Les insertions sont longues, elles sont générées dans la réponse principale.


-- === Patients ===
INSERT INTO public.patients (nom, prenom, date_naissance, sexe, telephone, adresse, numero_assurance, code_patient) VALUES
('Diop', 'Aminata', '1990-05-12', 'F', '771234567', 'Dakar Plateau', 'ASS12345', 'PAT001'),
('Sow', 'Mamadou', '1985-08-20', 'M', '776543210', 'Parcelles', NULL, 'PAT002'),
('Fall', 'Fatou', '1992-03-18', 'F', '770112233', 'Guédiawaye', 'ASS67890', 'PAT003'),
('Ndiaye', 'Cheikh', '1978-11-07', 'M', '775556677', 'Medina', NULL, 'PAT004');

-- === Personnels ===
INSERT INTO public.personnels (nom, prenom, specialite, type_personnel, telephone, email, horaires) VALUES
('Ba', 'Ousmane', 'Cardiologie', 'Médecin', '770000001', 'oba@hopital.sn', '08h-16h'),
('Camara', 'Aissatou', 'Pédiatrie', 'Médecin', '770000002', 'acamara@hopital.sn', '09h-17h'),
('Diallo', 'Mohamed', 'Infirmier', 'Infirmier', '770000003', 'mdiallo@hopital.sn', '07h-15h'),
('Kane', 'Seynabou', 'Radiologie', 'Technicien', '770000004', 'skane@hopital.sn', '10h-18h');

-- === Chambres ===
INSERT INTO public.chambres (numero, bloc, statut, date_derniere_maintenance, capacite, equipements) VALUES
('C101', 'Bloc A', 'Libre', '2024-12-01', 2, 'Oxygène, ECG'),
('C102', 'Bloc A', 'Occupée', '2024-11-15', 1, 'Lit électrique'),
('C201', 'Bloc B', 'Maintenance', NULL, 3, 'Monitorage'),
('C202', 'Bloc B', 'Libre', '2025-01-10', 2, NULL);

-- === Lits ===
INSERT INTO public.lits (numero, chambre_id, statut) VALUES
('LIT01', 1, 'Libre'),
('LIT02', 1, 'Occupé'),
('LIT03', 2, 'Occupé'),
('LIT04', 3, 'Libre');

-- === Dossiers médicaux ===
INSERT INTO public.dossiers_medicaux (patient_id, date_creation, antecedents, allergies, traitements_en_cours, notes) VALUES
(1, now(), 'Hypertension', 'Aucune', 'Bêtabloquants', 'Suivi régulier nécessaire'),
(2, now(), 'Asthme', 'Pénicilline', 'Ventoline', 'Amélioration'),
(3, now(), 'Diabète type 2', 'Aucune', 'Metformine', 'Contrôle stable'),
(4, now(), 'Aucun', 'Arachide', 'Antihistaminiques', 'Surveillance');

-- === Rendez-vous ===
INSERT INTO public.rendez_vous (patient_id, personnel_id, date_heure, motif, statut) VALUES
(1, 1, '2025-04-25 09:00', 'Consultation cardiologie', 'Confirmé'),
(2, 2, '2025-04-26 10:00', 'Suivi pédiatrique', 'En attente'),
(3, 3, '2025-04-27 11:00', 'Vaccination', 'Confirmé'),
(4, 1, '2025-04-28 14:00', 'Check-up', 'Annulé');

-- === Consultations ===
INSERT INTO public.consultations (patient_id, personnel_id, date_realisation, symptomes, diagnostic, prescriptions, notes_internes) VALUES
(1, 1, now(), 'Douleur thoracique', 'Angine stable', 'Aspirine', 'Contrôle à 1 mois'),
(2, 2, now(), 'Fièvre, toux', 'Bronchite', 'Paracétamol', 'Revoir dans 7 jours'),
(3, 3, now(), 'Vaccination bébé', 'RAS', 'Carnet vaccins à jour', 'Tout est bon'),
(4, 1, now(), 'Fatigue', 'Anémie légère', 'Fer', 'Analyse ferritine');

-- === Hospitalisations ===
INSERT INTO public.hospitalisations (patient_id, lit_id, date_debut, date_fin, motif, personnel_responsable_id, notes) VALUES
(1, 1, '2025-04-20', NULL, 'Observation', 1, 'Surveillance ECG'),
(2, 2, '2025-04-15', '2025-04-18', 'Asthme sévère', 2, 'Crise maîtrisée'),
(3, 3, '2025-04-10', '2025-04-14', 'Diabète décompensé', 3, 'Amélioration'),
(4, 4, '2025-04-22', NULL, 'Tests complémentaires', 1, 'En cours');

-- === Matériels ===
INSERT INTO public.materiels (nom, description, type_materiel, statut, date_achat, date_derniere_maintenance, fournisseur, quantite, localisation) VALUES
('ECG', 'Électrocardiographe 12 pistes', 'Diagnostic', 'Fonctionnel', '2023-05-01', '2024-05-01', 'MEDTECH', 2, 'Bloc A'),
('Lit médicalisé', 'Réglable', 'Mobilier', 'Fonctionnel', '2022-11-10', NULL, 'ORTHO-SERVICE', 5, 'Bloc B'),
('Ventilateur', 'Respirateur assisté', 'Réanimation', 'En panne', '2021-08-20', '2024-01-05', 'BIOMEDICAL', 1, 'Urgences'),
('Scanner', 'Scanner cérébral', 'Imagerie', 'Fonctionnel', '2020-02-02', '2025-02-01', 'SCANMED', 1, 'Imagerie');

-- === Paiements ===
INSERT INTO public.paiements (patient_id, montant, mode, date_paiement, reference) VALUES
(1, 15000.00, 'Espèce', '2025-04-20', 'REF001'),
(2, 22000.00, 'Carte', '2025-04-18', 'REF002'),
(3, 10000.00, 'Mobile Money', '2025-04-15', 'REF003'),
(4, 5000.00, 'Espèce', '2025-04-22', NULL);

-- === Utilisateurs ===
INSERT INTO public.utilisateurs (id_personnel, nom, email, mot_de_passe, role) VALUES
(1, 'admin1', 'admin1@hopital.sn', 'motdepasse1', 'ADMIN'),
(2, 'user2', 'user2@hopital.sn', 'motdepasse2', 'MEDECIN'),
(3, 'user3', 'user3@hopital.sn', 'motdepasse3', 'INFIRMIER'),
(4, 'user4', 'user4@hopital.sn', 'motdepasse4', 'TECHNICIEN');

-- === Gardes ===
INSERT INTO public.gardes (personnel_id, date_debut_periode, date_fin_periode, heure_debut, heure_fin) VALUES
(1, '2025-04-24', '2025-04-25', '08:00', '20:00'),
(2, '2025-04-25', '2025-04-26', '08:00', '20:00'),
(3, '2025-04-26', '2025-04-27', '08:00', '20:00'),
(4, '2025-04-27', '2025-04-28', '08:00', '20:00');

