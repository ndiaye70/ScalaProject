create table patients
(
    id               bigserial
        primary key,
    nom              varchar not null,
    prenom           varchar not null,
    date_naissance   date    not null,
    sexe             varchar not null,
    telephone        varchar not null,
    adresse          varchar not null,
    numero_assurance varchar,
    code_patient     varchar not null
);

alter table patients
    owner to postgres;

