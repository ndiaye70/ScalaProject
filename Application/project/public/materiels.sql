create table materiels
(
    id                        bigserial
        primary key,
    nom                       varchar   not null,
    description               varchar,
    type_materiel             varchar   not null,
    statut                    varchar   not null,
    date_achat                timestamp not null,
    date_derniere_maintenance timestamp,
    fournisseur               varchar   not null,
    quantite                  integer   not null,
    localisation              varchar
);

alter table materiels
    owner to postgres;

