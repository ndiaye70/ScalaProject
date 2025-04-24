create table utilisateurs
(
    id           bigserial
        primary key,
    id_personnel bigint  not null
        constraint personnel_fk
            references personnels,
    nom          varchar not null,
    email        varchar not null
        unique,
    mot_de_passe varchar not null,
    role         varchar not null
);

alter table utilisateurs
    owner to postgres;

