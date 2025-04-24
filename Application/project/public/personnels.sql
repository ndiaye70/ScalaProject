create table personnels
(
    id             bigserial
        primary key,
    nom            varchar not null,
    prenom         varchar not null,
    specialite     varchar not null,
    type_personnel varchar not null,
    telephone      varchar not null,
    email          varchar not null,
    horaires       varchar not null
);

alter table personnels
    owner to postgres;

