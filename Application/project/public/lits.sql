create table lits
(
    id         bigserial
        primary key,
    numero     varchar not null,
    chambre_id bigint  not null
        constraint fk_lit_chambre
            references chambres
            on delete cascade,
    statut     varchar not null
);

alter table lits
    owner to postgres;

