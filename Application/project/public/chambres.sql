create table chambres
(
    id                        bigserial
        primary key,
    numero                    varchar not null
        unique,
    bloc                      varchar not null,
    statut                    varchar not null,
    date_derniere_maintenance timestamp,
    capacite                  integer not null,
    equipements               varchar
);

alter table chambres
    owner to postgres;

create unique index idx_chambre_numero
    on chambres (numero);

create index idx_chambre_statut
    on chambres (statut);

