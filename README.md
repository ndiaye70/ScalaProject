 🏥 Werral ak Jamm - Application de gestion hospitalière

Application web de gestion hospitalière développée avec **Play Framework (Scala)** pour le back-end et **Angular** pour le front-end.
Realisé Par Pape Abdoulaye Ndiaye Et Maty Seck

 ⚙️ Prérequis

 Backend (Scala / Play Framework)
- Java JDK 11+
- [SBT](https://www.scala-sbt.org/download.html)
- PostgreSQL (ex : `Hospitaldb`)
- Configuration de la DB dans `application.conf`

 Frontend (Angular)
- [Node.js](https://nodejs.org/) (v19)
- Angular CLI (`npm install -g @angular/cli`)

 🚀 Installation & Lancement

 1. Base de données
Un script `database.sql` (fourni) permet de créer les tables et insérer des données initiales, y compris un utilisateur admin.

 2. Backend
```bash
sbt run

Le backend sera accessible sur : http://localhost:8081.

Connexion (admin)
Email : admin1@hopital.sn

Mot de passe : motdepasse1


